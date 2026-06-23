package com.closify.myapplication.data.remote

import com.closify.myapplication.BuildConfig
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.File

interface RemoveBgApi {
    @Multipart
    @POST("v1.0/removebg")
    suspend fun removeBackground(
        @Header("X-Api-Key") apiKey: String,
        @Part imageFile: MultipartBody.Part,
        @Part("size") size: okhttp3.RequestBody
    ): Response<ResponseBody>
}

object RemoveBgService {

    private val api: RemoveBgApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.remove.bg/")
            .build()
            .create(RemoveBgApi::class.java)
    }

    suspend fun removeBackground(imageFile: File): Result<ByteArray> {
        return try {
            val filePart = MultipartBody.Part.createFormData(
                "image_file",
                imageFile.name,
                imageFile.asRequestBody("image/jpeg".toMediaType())
            )
            val sizePart = "auto".toRequestBody("text/plain".toMediaType())

            val response = api.removeBackground(
                apiKey = BuildConfig.REMOVE_BG_API_KEY,
                imageFile = filePart,
                size = sizePart
            )

            if (response.isSuccessful) {
                val bytes = response.body()?.bytes()
                    ?: return Result.failure(Exception("Respuesta vacía de Remove.bg"))
                Result.success(bytes)
            } else {
                Result.failure(Exception("Error Remove.bg: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
