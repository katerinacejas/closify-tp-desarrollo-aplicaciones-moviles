package com.closify.myapplication.data.repository

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import com.closify.myapplication.domain.repository.ProfileImageRepository
import com.closify.myapplication.domain.repository.ProfileImageType
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirebaseProfileImageRepository private constructor(
    context: Context
) : ProfileImageRepository {

    companion object {
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

            reference.putFile(uri, metadata).await()
            reference.downloadUrl.await().toString()
        }
    }
}
