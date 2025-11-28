package com.example.proyectodegrado.data.api

import com.example.proyectodegrado.data.model.PresignRequest
import com.example.proyectodegrado.data.model.PresignResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ImageApiService {

    @POST("/api/images/presign/put")
    suspend fun getPresignedUrl(@Body request: PresignRequest): Response<PresignResponse>

    @PUT
    suspend fun uploadImageToS3(
        @Url uploadUrl: String,
        @Header("Content-Type") contentType: String,
        @Body imageBytes: RequestBody
    ): Response<Unit>
}