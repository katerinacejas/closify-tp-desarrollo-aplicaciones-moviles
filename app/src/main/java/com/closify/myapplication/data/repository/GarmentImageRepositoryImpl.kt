package com.closify.myapplication.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import com.closify.myapplication.data.remote.CloudinaryService
import com.closify.myapplication.data.remote.RemoveBgService
import com.closify.myapplication.domain.repository.GarmentImageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

class GarmentImageRepositoryImpl private constructor(
    context: Context
) : GarmentImageRepository {

    companion object {
        private const val TAG = "GarmentImageRepository"

        @Volatile private var _instance: GarmentImageRepositoryImpl? = null

        fun initialize(context: Context) {
            if (_instance == null) {
                synchronized(this) {
                    if (_instance == null) {
                        _instance = GarmentImageRepositoryImpl(context.applicationContext)
                    }
                }
            }
        }

        val instance: GarmentImageRepositoryImpl
            get() = _instance ?: error("GarmentImageRepositoryImpl.initialize(context) no fue llamado.")
    }

    private val appContext = context.applicationContext
    private val garmentImagesDir = File(appContext.filesDir, "garment_images")

    override suspend fun prepareImagePreview(imageUri: String): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val sourceFile = copyUriToTempFile(imageUri)
                ?: error("No se pudo leer la imagen seleccionada.")

            val pngBytes = RemoveBgService.removeBackground(sourceFile).getOrThrow()
            val outputFile = createPersistentFile(prefix = "removebg", extension = "png")
            outputFile.writeBytes(pngBytes)
            Uri.fromFile(outputFile).toString()
        }.onFailure { error ->
            Log.w(TAG, "Background removal skipped: ${error.message}")
        }
    }

    override suspend fun storeGarmentImage(imageUri: String): String = withContext(Dispatchers.IO) {
        val localFile = copyUriToPersistentFile(imageUri)
            ?: error("No se pudo guardar la imagen de la prenda.")

        val remoteUrl = CloudinaryService.upload(localFile)
        when {
            remoteUrl != null -> remoteUrl
            CloudinaryService.isConfigured() -> error("No se pudo subir la imagen de la prenda.")
            else -> Uri.fromFile(localFile).toString()
        }
    }

    private fun copyUriToTempFile(uriString: String): File? {
        return readUri(uriString) { extension, readBytes ->
            val tempFile = File(appContext.cacheDir, "garment_upload_${UUID.randomUUID()}.$extension")
            tempFile.outputStream().use { readBytes(it) }
            tempFile
        }
    }

    private fun copyUriToPersistentFile(uriString: String): File? {
        val uri = Uri.parse(uriString)
        if (uri.scheme == "file") {
            val sourceFile = File(uri.path ?: return null)
            if (!sourceFile.exists()) return null
            if (sourceFile.isInside(garmentImagesDir)) return sourceFile
        }

        return readUri(uriString) { extension, readBytes ->
            val outputFile = createPersistentFile(prefix = "garment", extension = extension)
            outputFile.outputStream().use { readBytes(it) }
            outputFile
        }
    }

    private fun <T> readUri(
        uriString: String,
        block: (extension: String, readBytes: (java.io.OutputStream) -> Unit) -> T
    ): T? {
        return try {
            val uri = Uri.parse(uriString)
            val extension = resolveExtension(uri)

            if (uri.scheme == "file") {
                val file = File(uri.path ?: return null)
                if (!file.exists()) return null
                return block(file.extension.ifBlank { extension }) { outputStream ->
                    file.inputStream().use { inputStream -> inputStream.copyTo(outputStream) }
                }
            }

            block(extension) { outputStream ->
                appContext.contentResolver.openInputStream(uri)?.use { inputStream ->
                    inputStream.copyTo(outputStream)
                } ?: error("No se pudo abrir la URI de imagen.")
            }
        } catch (error: Exception) {
            Log.w(TAG, "Image URI read failed: ${error.message}")
            null
        }
    }

    private fun createPersistentFile(prefix: String, extension: String): File {
        garmentImagesDir.mkdirs()
        return File(garmentImagesDir, "${prefix}_${UUID.randomUUID()}.$extension")
    }

    private fun resolveExtension(uri: Uri): String {
        val mimeType = appContext.contentResolver.getType(uri)
        val fromMimeType = mimeType?.let {
            MimeTypeMap.getSingleton().getExtensionFromMimeType(it)
        }
        return fromMimeType ?: uri.path?.substringAfterLast('.', missingDelimiterValue = "")
            ?.takeIf { it.isNotBlank() }
            ?: "jpg"
    }

    private fun File.isInside(parent: File): Boolean {
        val parentPath = parent.canonicalPath
        val filePath = canonicalPath
        return filePath == parentPath || filePath.startsWith("$parentPath${File.separator}")
    }
}
