package com.example.proyectodegrado.data.model

/**
 * Respuesta del backend después de intentar confirmar la subida y guardar la URL.
 *
 * @property message Un mensaje indicando éxito o error.
 * @property accessUrl Opcional: El backend podría devolver la URL guardada como confirmación extra.
 */
data class ConfirmUploadResponse(
    val message: String,
    val accessUrl: String? = null // Es nullable si el backend no siempre lo devuelve
)