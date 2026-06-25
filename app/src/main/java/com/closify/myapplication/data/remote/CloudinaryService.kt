package com.closify.myapplication.data.remote

import com.closify.myapplication.BuildConfig
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.security.MessageDigest

object CloudinaryService {

    private val client = OkHttpClient()
    private val cloudName = BuildConfig.CLOUDINARY_CLOUD_NAME
    private val apiKey = BuildConfig.CLOUDINARY_API_KEY
    private val apiSecret = BuildConfig.CLOUDINARY_API_SECRET

    suspend fun upload(imageFile: File): String? {
        return try {
            val timestamp = (System.currentTimeMillis() / 1000).toString()
            val folder = "closify/garments"
            val signature = generateSignature(timestamp, folder)

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "file",
                    imageFile.name,
                    imageFile.asRequestBody("image/png".toMediaType())
                )
                .addFormDataPart("api_key", apiKey)
                .addFormDataPart("timestamp", timestamp)
                .addFormDataPart("folder", folder)
                .addFormDataPart("signature", signature)
                .build()

            val request = Request.Builder()
                .url("https://api.cloudinary.com/v1_1/$cloudName/image/upload")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val json = JSONObject(response.body?.string() ?: return null)
                json.getString("secure_url")
            } else {
                android.util.Log.w("CloudinaryService", "Upload failed: ${response.code}")
                null
            }
        } catch (e: Exception) {
            android.util.Log.w("CloudinaryService", "Upload error: ${e.message}")
            null
        }
    }

    private fun generateSignature(timestamp: String, folder: String): String {
        val toSign = "folder=$folder&timestamp=$timestamp$apiSecret"
        val digest = MessageDigest.getInstance("SHA-1")
        val bytes = digest.digest(toSign.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
