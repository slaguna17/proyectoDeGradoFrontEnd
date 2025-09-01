// src/data/api/ImageApiService.kt
package com.example.proyectodegrado.data.api

import com.example.proyectodegrado.data.model.PresignRequest
import com.example.proyectodegrado.data.model.PresignResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ImageApiService {

    /**
     * Pide a nuestro backend una URL segura para subir un archivo.
     * Usa el endpoint genérico /api/images/presign/put.
     */
    @POST("/api/images/presign/put")
    suspend fun getPresignedUrl(@Body request: PresignRequest): Response<PresignResponse>

    /**
     * Sube los bytes de la imagen a la URL que nos dio S3.
     * No devuelve cuerpo, solo nos importa si la respuesta fue exitosa (código 200).
     */
    @PUT
    suspend fun uploadImageToS3(
        @Url uploadUrl: String,
        @Header("Content-Type") contentType: String,
        @Body imageBytes: RequestBody
    ): Response<Unit>
}