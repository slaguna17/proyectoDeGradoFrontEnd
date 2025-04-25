package com.example.proyectodegrado.data.model

/**
 * Cuerpo de la solicitud para obtener una URL prefirmada de S3 desde el backend.
 *
 * @property contentType El tipo MIME del archivo que se va a subir (ej: "image/jpeg").
 * @property entityType Cadena opcional para indicar el tipo de entidad (ej: "product", "user")
 * y organizar los archivos en S3.
 */
data class PresignedUrlRequest(
    val contentType: String,
    val entityType: String? // Es nullable si puede no enviarse siempre
)