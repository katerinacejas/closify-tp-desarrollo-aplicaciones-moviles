package com.closify.myapplication.data.repository

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.exceptions.ClearCredentialException
import com.closify.myapplication.data.local.AppDatabase
import com.closify.myapplication.data.local.mapper.toDomain
import com.closify.myapplication.data.local.mapper.toEntity
import com.closify.myapplication.data.local.mapper.toFirestoreMap
import com.closify.myapplication.data.local.mapper.toUserEntity
import com.closify.myapplication.domain.model.GoogleAuthCredential
import com.closify.myapplication.domain.model.User
import com.closify.myapplication.domain.model.UserProfile
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class UserRepository private constructor(context: Context) {

    companion object {
        @Volatile private var _instance: UserRepository? = null

        fun initialize(context: Context) {
            if (_instance == null) {
                synchronized(this) {
                    if (_instance == null) {
                        _instance = UserRepository(context.applicationContext)
                    }
                }
            }
        }

        val instance: UserRepository
            get() = _instance ?: error("UserRepository.initialize(context) no fue llamado.")
    }

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val userDao = AppDatabase.getInstance(context).userDao()
    private val credentialManager = CredentialManager.create(context)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    val currentUserId: String get() = auth.currentUser?.uid ?: ""
    val currentUsername: String get() = _currentUser.value?.profile?.username ?: ""

    fun isLoggedIn(): Boolean = auth.currentUser != null

    fun getCurrentUser(): User? = _currentUser.value

    fun getCurrentUserOrDefault(): User = _currentUser.value
        ?: error("getCurrentUserOrDefault() llamado sin usuario logueado.")

    suspend fun getUserById(userId: String): User? {
        _currentUser.value?.let { if (it.id == userId) return it }
        val entity = userDao.getById(userId)
        if (entity != null) return entity.toDomain()
        return try {
            val doc = firestore.collection("users").document(userId).get().await()
            if (!doc.exists()) return null
            val remoteEntity = doc.toUserEntity() ?: return null
            userDao.upsert(remoteEntity)
            remoteEntity.toDomain()
        } catch (e: Exception) { null }
    }

    suspend fun getUserSummary(userId: String): com.closify.myapplication.domain.model.UserSummary? =
        getUserById(userId)?.toSummary()

    // Restaura la sesión al abrir la app si ya había un usuario logueado
    suspend fun restoreSession() {
        val uid = auth.currentUser?.uid ?: return
        val entity = userDao.getById(uid)
        if (entity != null) {
            _currentUser.value = entity.toDomain()
        }
        // Siempre sincroniza desde Firestore para tener datos frescos
        fetchAndCacheFromFirestore(uid)
    }

    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: throw Exception("No se pudo obtener el usuario.")
            fetchAndCacheFromFirestore(uid)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(mapAuthError(e)))
        }
    }

    suspend fun loginWithGoogle(googleCredential: GoogleAuthCredential): Result<Unit> {
        return try {
            val firebaseCredential = GoogleAuthProvider.getCredential(googleCredential.idToken, null)
            val result = auth.signInWithCredential(firebaseCredential).await()
            val firebaseUser = result.user ?: throw Exception("No se pudo obtener el usuario.")
            val uid = firebaseUser.uid
            val userDoc = firestore.collection("users").document(uid).get().await()

            if (userDoc.exists()) {
                fetchAndCacheFromFirestore(uid)
            } else {
                createGoogleUser(
                    uid = uid,
                    email = googleCredential.email ?: firebaseUser.email.orEmpty(),
                    displayName = googleCredential.displayName ?: firebaseUser.displayName,
                    profileImageUrl = googleCredential.profileImageUrl ?: firebaseUser.photoUrl?.toString()
                )
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(mapAuthError(e)))
        }
    }

    suspend fun register(
        email: String,
        password: String,
        username: String,
        fullName: String,
        birthDate: String,
        bio: String
    ): Result<Unit> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: throw Exception("No se pudo crear el usuario.")
            val createdAt = LocalDate.now().format(
                DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", Locale.forLanguageTag("es-AR"))
            )
            val user = User(
                id = uid,
                email = email,
                profile = com.closify.myapplication.domain.model.UserProfile(
                    id = uid,
                    fullName = fullName.trim(),
                    username = normalizeUsername(username),
                    birthDate = birthDate,
                    bio = bio.trim(),
                    avatarImageResId = com.closify.myapplication.R.drawable.avatar_default,
                    bannerImageResId = com.closify.myapplication.R.drawable.banner_default
                ),
                createdAt = createdAt
            )
            userDao.upsert(user.toEntity())
            _currentUser.value = user
            scope.launch {
                firestore.collection("users").document(uid).set(user.toFirestoreMap()).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(mapAuthError(e)))
        }
    }

    suspend fun isUsernameAvailable(username: String): Boolean {
        return try {
            val normalized = normalizeUsername(username)
            val snapshot = firestore.collection("users")
                .whereEqualTo("username", normalized)
                .limit(1)
                .get()
                .await()
            snapshot.isEmpty
        } catch (e: Exception) {
            true
        }
    }

    suspend fun requestPasswordRecovery(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(mapAuthError(e)))
        }
    }

    suspend fun updateCurrentUserProfile(
        fullName: String,
        username: String,
        birthDate: String,
        bio: String,
        avatarImageUrl: String? = null,
        bannerImageUrl: String? = null
    ): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception("No hay un usuario logueado."))
            val current = _currentUser.value
                ?: return Result.failure(Exception("No hay un usuario logueado."))

            val updated = current.copy(
                profile = current.profile.copy(
                    fullName = fullName.trim(),
                    username = normalizeUsername(username),
                    birthDate = birthDate,
                    bio = bio.trim(),
                    avatarImageUrl = avatarImageUrl ?: current.profile.avatarImageUrl,
                    bannerImageUrl = bannerImageUrl ?: current.profile.bannerImageUrl
                )
            )
            val entity = updated.toEntity()
            val firestoreMap = updated.toFirestoreMap()

            firestore.collection("users").document(uid).update(firestoreMap).await()
            userDao.upsert(entity)

            _currentUser.value = updated
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun changeCurrentUserPassword(
        currentPassword: String,
        newPassword: String
    ): Result<Unit> {
        val user = auth.currentUser
            ?: return Result.failure(Exception("No hay un usuario logueado."))
        val email = user.email
            ?: return Result.failure(Exception("No hay un usuario logueado."))
        return try {
            val credential = EmailAuthProvider.getCredential(email, currentPassword)
            user.reauthenticate(credential).await()
            user.updatePassword(newPassword).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(mapAuthError(e)))
        }
    }

    fun logout() {
        auth.signOut()
        _currentUser.value = null
        scope.launch {
            try {
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
            } catch (_: ClearCredentialException) {
                // Firebase ya cerr\u00F3 sesi\u00F3n; Credential Manager solo limpia el selector de cuentas.
            }
        }
    }

    private suspend fun fetchAndCacheFromFirestore(uid: String) {
        try {
            val doc = firestore.collection("users").document(uid).get().await()
            if (!doc.exists()) return
            val entity = doc.toUserEntity() ?: return
            if (entity.fullName.isBlank() && entity.username.isBlank()) return
            userDao.upsert(entity)
            _currentUser.value = entity.toDomain()
        } catch (e: Exception) {
            // Sin conexión — intenta desde Room
            val entity = userDao.getById(uid)
            if (entity != null) _currentUser.value = entity.toDomain()
        }
    }

    private suspend fun createGoogleUser(
        uid: String,
        email: String,
        displayName: String?,
        profileImageUrl: String?
    ) {
        val createdAt = LocalDate.now().format(
            DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", Locale.forLanguageTag("es-AR"))
        )
        val fullName = displayName?.trim()
            ?.takeIf { it.isNotBlank() }
            ?: email.substringBefore("@").ifBlank { "Usuario Closify" }
        val username = generateAvailableGoogleUsername(email, fullName)
        val user = User(
            id = uid,
            email = email,
            profile = UserProfile(
                id = uid,
                fullName = fullName,
                username = username,
                birthDate = "",
                bio = "",
                avatarImageResId = com.closify.myapplication.R.drawable.avatar_default,
                bannerImageResId = com.closify.myapplication.R.drawable.banner_default,
                avatarImageUrl = profileImageUrl
            ),
            createdAt = createdAt
        )

        firestore.collection("users").document(uid).set(user.toFirestoreMap()).await()
        userDao.upsert(user.toEntity())
        _currentUser.value = user
    }

    private suspend fun generateAvailableGoogleUsername(email: String, displayName: String): String {
        val rawBase = email.substringBefore("@").ifBlank { displayName }.ifBlank { "google_user" }
        val base = sanitizeUsernameBase(rawBase)
        var candidate = base
        var suffix = 1
        while (!isUsernameAvailable(candidate)) {
            candidate = "$base$suffix"
            suffix++
        }
        return normalizeUsername(candidate)
    }

    private fun sanitizeUsernameBase(value: String): String {
        val withoutAccents = java.text.Normalizer.normalize(value, java.text.Normalizer.Form.NFD)
            .replace("\\p{Mn}+".toRegex(), "")
        return withoutAccents
            .lowercase(Locale.ROOT)
            .replace("[^a-z0-9._]+".toRegex(), "_")
            .trim('.', '_')
            .take(24)
            .ifBlank { "google_user" }
    }

    private fun normalizeUsername(username: String): String =
        username.trim().let { if (it.startsWith("@")) it else "@$it" }

    private fun mapAuthError(e: Exception): String = when (e) {
        is FirebaseAuthInvalidUserException -> "No existe una cuenta con ese email."
        is FirebaseAuthInvalidCredentialsException -> "El email o la contraseña son incorrectos."
        is FirebaseAuthUserCollisionException -> "Ya existe una cuenta con ese email."
        is FirebaseAuthWeakPasswordException -> "La contraseña es muy débil."
        else -> e.message ?: "Ocurrió un error inesperado."
    }
}
