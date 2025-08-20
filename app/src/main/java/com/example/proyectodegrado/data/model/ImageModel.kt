package com.example.proyectodegrado.data.model

/** Request/Response para presign PUT */
data class PresignPutRequest(
    val folder: String,
    val fileName: String,
    val contentType: String
)
data class PresignPutResponse(
    val method: String, // "PUT"
    val key: String,
    val url: String,
    val expiresIn: Int,
    val maxMB: Int
)

/** (Opcional) Response para resolver URL de lectura, si lo usas desde app */
data class ImageUrlResponse(
    val key: String,
    val url: String,
    val signed: Boolean,
    val expiresIn: Int?
)