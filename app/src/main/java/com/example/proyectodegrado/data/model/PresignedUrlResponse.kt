package com.example.proyectodegrado.data.model

/**
 * Respuesta del backend al solicitar una URL prefirmada.
 *
 * @property uploadUrl La URL prefirmada (temporal y segura) para usar con PUT para subir el archivo a S3.
 * @property accessUrl La URL pública o final del archivo una vez subido a S3 (la que se guarda en la BD).
 * @property imageKey La clave (ruta/nombre de archivo) única del objeto en el bucket S3.
 */
data class PresignedUrlResponse(
    val uploadUrl: String,
    val accessUrl: String,
    val imageKey: String
)