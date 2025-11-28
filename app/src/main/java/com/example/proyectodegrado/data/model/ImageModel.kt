package com.example.proyectodegrado.data.model

data class PresignRequest(
    val folder: String,
    val fileName: String,
    val contentType: String
)

data class PresignResponse(
    val key: String,
    val url: String
)

/**
 * Resultado sellado para las operaciones del ImageRepository.
 */
sealed class ImageUploadResult {
    data class Success(val imageKey: String) : ImageUploadResult()
    data class Error(val message: String) : ImageUploadResult()
}