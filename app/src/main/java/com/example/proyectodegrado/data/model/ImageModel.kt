package com.example.proyectodegrado.data.model

/**
 * Cuerpo de la petición para solicitar una URL de subida al backend.
 * folder: "stores/1", "products/101", etc.
 * fileName: "logo.webp", "avatar.jpg", etc.
 */
data class PresignRequest(
    val folder: String,
    val fileName: String,
    val contentType: String
)

/**
 * Respuesta del backend con la URL segura para subir el archivo a S3.
 */
data class PresignResponse(
    val key: String, // La ruta completa del archivo en S3 (ej: "stores/1/logo.webp")
    val url: String  // La URL temporal para hacer el PUT
)

/**
 * Resultado sellado para las operaciones del ImageRepository.
 */
sealed class ImageUploadResult {
    data class Success(val imageKey: String) : ImageUploadResult()
    data class Error(val message: String) : ImageUploadResult()
}