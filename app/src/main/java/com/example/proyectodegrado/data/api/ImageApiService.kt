package com.example.proyectodegrado.data.api

import com.example.proyectodegrado.data.model.ImageUrlResponse
import com.example.proyectodegrado.data.model.PresignPutRequest
import com.example.proyectodegrado.data.model.PresignPutResponse
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ImageApiService {

    // Presign PUT (backend: /api/images/presign/put)
    @POST("/api/images/presign/put")
    suspend fun presignPut(@Body request: PresignPutRequest): Response<PresignPutResponse>

    // Upload a S3 (PUT a la URL de presign)
    @PUT
    suspend fun uploadImageToS3(
        @Url uploadUrl: String,
        @Header("Content-Type") contentType: String,
        @Body imageBytes: RequestBody
    ): Response<ResponseBody>

    // (Opcional) Convertir key -> URL firmada (si necesitas desde app)
    @GET("/api/images/url")
    suspend fun getImageUrl(
        @Query("key") key: String,
        @Query("signed") signed: Boolean = true
    ): Response<ImageUrlResponse>
}
