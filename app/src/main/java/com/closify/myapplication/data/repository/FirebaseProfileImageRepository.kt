package com.closify.myapplication.data.repository

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import com.closify.myapplication.domain.repository.ProfileImageRepository
import com.closify.myapplication.domain.repository.ProfileImageType
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirebaseProfileImageRepository private constructor(
    context: Context
) : ProfileImageRepository {

    companion object {
        private const val MAX_PROFILE_IMAGE_BYTES = 10 * 1024 * 1024

        @Volatile private var _instance: FirebaseProfileImageRepository? = null

        fun initialize(context: Context) {
            if (_instance == null) {
                synchronized(this) {
                    if (_instance == null) {
                        _instance = FirebaseProfileImageRepository(context.applicationContext)
                    }
                }
            }
        }

        val instance: FirebaseProfileImageRepository
            get() = _instance ?: error("FirebaseProfileImageRepository.initialize(context) no fue llamado.")
    }

    private val appContext = context.applicationContext
    private val storage = FirebaseStorage.getInstance()

    override suspend fun uploadProfileImage(
        userId: String,
        imageUri: String,
        imageType: ProfileImageType
    ): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            require(userId.isNotBlank()) { "No hay un usuario logueado." }

            val uri = Uri.parse(imageUri)
            val contentType = appContext.contentResolver.getType(uri) ?: "image/jpeg"
            val imageBytes = readImageBytes(uri)
            val extension = MimeTypeMap.getSingleton()
                .getExtensionFromMimeType(contentType)
                ?.takeIf { it.isNotBlank() }
                ?: "jpg"
            val fileName = "${imageType.storageName}_${System.currentTimeMillis()}.$extension"
            val reference = storage.reference
                .child("users")
                .child(userId)
                .child("profile")
                .child(fileName)
            val metadata = StorageMetadata.Builder()
                .setContentType(contentType)
                .build()

            val uploadSnapshot = reference.putBytes(imageBytes, metadata).await()
            uploadSnapshot.storage.downloadUrl.await().toString()
        }.recoverCatching { error ->
            throw Exception(mapUploadError(error))
        }
    }

    private fun readImageBytes(uri: Uri): ByteArray {
        val bytes = appContext.contentResolver.openInputStream(uri)
            ?.use { it.readBytes() }
            ?: error("No se pudo leer la imagen seleccionada.")

        require(bytes.isNotEmpty()) { "La imagen seleccionada esta vacia." }
        require(bytes.size <= MAX_PROFILE_IMAGE_BYTES) {
            "La imagen seleccionada es demasiado grande. Elegi una imagen de hasta 10 MB."
        }
        return bytes
    }

    private fun mapUploadError(error: Throwable): String {
        val storageError = error as? StorageException
        return when (storageError?.errorCode) {
            StorageException.ERROR_OBJECT_NOT_FOUND ->
                "No se pudo encontrar la imagen subida. Volve a seleccionar la foto e intentalo de nuevo."
            StorageException.ERROR_NOT_AUTHENTICATED ->
                "Necesitas iniciar sesion para cambiar tus imagenes."
            StorageException.ERROR_NOT_AUTHORIZED ->
                "No tenes permisos para cambiar estas imagenes."
            StorageException.ERROR_QUOTA_EXCEEDED ->
                "Firebase Storage no tiene cuota disponible para guardar la imagen."
            else -> error.message ?: "No se pudo guardar la imagen de perfil."
        }
    }
}
