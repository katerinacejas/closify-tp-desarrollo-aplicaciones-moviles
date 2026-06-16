package com.closify.myapplication.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.closify.myapplication.R
import com.closify.myapplication.domain.model.User
import com.closify.myapplication.domain.model.UserProfile
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.tasks.await

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

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val prefs: SharedPreferences =
        context.getSharedPreferences("closify_profiles", Context.MODE_PRIVATE)

    val currentUserId: String
        get() = auth.currentUser?.uid ?: ""

    val currentUsername: String
        get() = auth.currentUser?.uid
            ?.let { prefs.getString("${it}_username", "") } ?: ""

    fun isLoggedIn(): Boolean = auth.currentUser != null

    fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser ?: return null
        val profile = loadProfile(firebaseUser.uid) ?: return null
        return User(
            id = firebaseUser.uid,
            email = firebaseUser.email ?: "",
            profile = profile
        )
    }

    fun getCurrentUserOrDefault(): User =
        getCurrentUser() ?: MockClosifyData.currentUser

    fun getUserById(userId: String): User? =
        MockClosifyData.authUserById(userId) ?: MockClosifyData.userById(userId)

    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
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
            saveProfile(uid, username, fullName, birthDate, bio)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(mapAuthError(e)))
        }
    }

    suspend fun isUsernameAvailable(username: String): Boolean {
        // TODO: verificar contra Firestore cuando esté integrado
        return true
    }

    suspend fun requestPasswordRecovery(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(mapAuthError(e)))
        }
    }

    fun updateCurrentUserProfile(
        fullName: String,
        username: String,
        birthDate: String,
        bio: String
    ): Result<Unit> {
        val uid = auth.currentUser?.uid
            ?: return Result.failure(Exception("No hay un usuario logueado."))
        saveProfile(uid, username, fullName, birthDate, bio)
        return Result.success(Unit)
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
    }

    private fun saveProfile(uid: String, username: String, fullName: String, birthDate: String, bio: String) {
        prefs.edit()
            .putString("${uid}_username", normalizeUsername(username))
            .putString("${uid}_fullName", fullName.trim())
            .putString("${uid}_birthDate", birthDate)
            .putString("${uid}_bio", bio.trim())
            .apply()
    }

    private fun loadProfile(uid: String): UserProfile? {
        val username = prefs.getString("${uid}_username", null) ?: return null
        return UserProfile(
            id = uid,
            fullName = prefs.getString("${uid}_fullName", "") ?: "",
            username = username,
            birthDate = prefs.getString("${uid}_birthDate", "") ?: "",
            bio = prefs.getString("${uid}_bio", "") ?: "",
            avatarImageResId = R.drawable.avatar_default,
            bannerImageResId = R.drawable.banner_default
        )
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
