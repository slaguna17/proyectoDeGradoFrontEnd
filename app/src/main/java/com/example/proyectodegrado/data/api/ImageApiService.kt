package com.example.proyectodegrado.data.api

// Importa tus clases DTO (Data Transfer Object) aquí
import com.example.proyectodegrado.data.model.ConfirmUploadRequest
import com.example.proyectodegrado.data.model.ConfirmUploadResponse
import com.example.proyectodegrado.data.model.PresignedUrlRequest
import com.example.proyectodegrado.data.model.PresignedUrlResponse
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ImageApiService {

    @POST("api/images/presigned-url") // Endpoint para obtener URL prefirmada
    suspend fun getPresignedUrl(
        @Body request: PresignedUrlRequest // <-- Espera UN objeto
    ): Response<PresignedUrlResponse>

    @PUT // Método PUT para subir a S3
    suspend fun uploadImageToS3(
        @Url uploadUrl: String, // URL dinámica de S3
        @Header("Content-Type") contentType: String, // Cabecera Content-Type es crucial
        @Body imageBytes: RequestBody // Cuerpo de la solicitud con los bytes
    ): Response<ResponseBody> // La respuesta de S3 suele ser vacía en éxito (200 OK)

    @POST("api/images/confirm-upload") // Endpoint para confirmar subida
    suspend fun confirmImageUpload(
        @Body request: ConfirmUploadRequest
        // @Header("Authorization") token: String? = null // Añadir si usas Auth
    ): Response<ConfirmUploadResponse>
}