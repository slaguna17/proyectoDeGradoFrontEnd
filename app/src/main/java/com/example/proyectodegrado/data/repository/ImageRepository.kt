// app/src/main/java/com/example/proyectodegrado/data/repository/ImageRepository.kt
package com.example.proyectodegrado.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.proyectodegrado.data.api.ImageApiService
// Importa tus DTOs y la clase sellada de resultado
import com.example.proyectodegrado.data.model.ConfirmUploadRequest
import com.example.proyectodegrado.data.model.PresignedUrlRequest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream

// Clase sellada para manejar resultados
sealed class ImageUploadResult {
    data class Success(val accessUrl: String) : ImageUploadResult()
    data class Error(val message: String, val cause: Throwable? = null) : ImageUploadResult()
}

class ImageRepository(
    private val apiService: ImageApiService,
    private val context: Context // Necesario para ContentResolver
) {

    private val tag = "ImageRepository" // Para logs

    suspend fun getPresignedUrlAndUpload(
        imageUri: Uri,
        entityType: String,
        entityId: Int // O el tipo de ID que uses (Long, String)
    ): ImageUploadResult {

        // 1. Obtener ContentType y InputStream del Uri
        val contentType = context.contentResolver.getType(imageUri)
        if (contentType == null || !contentType.startsWith("image/")) {
            Log.e(tag, "Tipo de archivo no válido o desconocido: $contentType para $imageUri")
            return ImageUploadResult.Error("Tipo de archivo no válido ($contentType). Selecciona una imagen.")
        }

        var inputStream: InputStream? = null
        try {
            inputStream = context.contentResolver.openInputStream(imageUri)
            if (inputStream == null) {
                Log.e(tag, "No se pudo abrir InputStream para el URI: $imageUri")
                return ImageUploadResult.Error("No se pudo leer el archivo seleccionado.")
            }

            // 2. Solicitar URL prefirmada al backend
            Log.d(tag, "Solicitando Presigned URL para contentType: $contentType, entityType: $entityType")
            val presignedUrlRequest = PresignedUrlRequest(contentType, entityType)
            val presignedResponse = apiService.getPresignedUrl(presignedUrlRequest)

            if (!presignedResponse.isSuccessful || presignedResponse.body() == null) {
                val errorBody = presignedResponse.errorBody()?.string() ?: "Error desconocido"
                Log.e(tag, "Error al obtener Presigned URL: ${presignedResponse.code()} - $errorBody")
                return ImageUploadResult.Error("Error del servidor al obtener URL: ${presignedResponse.code()}")
            }

            val presignedData = presignedResponse.body()!!
            Log.d(tag, "Presigned URL obtenida.")

            // 3. Preparar RequestBody y subir a S3
            val imageBytes = inputStream.readBytes()
            val requestBody = imageBytes.toRequestBody(contentType.toMediaTypeOrNull())

            Log.d(tag, "Subiendo imagen a S3 (${imageBytes.size / 1024} KB)...")
            val uploadResponse = apiService.uploadImageToS3(
                uploadUrl = presignedData.uploadUrl,
                contentType = contentType,
                imageBytes = requestBody
            )

            if (!uploadResponse.isSuccessful) {
                val errorBody = uploadResponse.errorBody()?.string() ?: "Error desconocido S3"
                Log.e(tag, "Error al subir a S3: ${uploadResponse.code()} - $errorBody")
                return ImageUploadResult.Error("Error al subir imagen (${uploadResponse.code()})")
            }

            Log.d(tag, "Subida a S3 exitosa!")

            // 4. Confirmar subida al backend
            Log.d(tag, "Confirmando subida al backend (Key: ${presignedData.imageKey})...")
            val confirmRequest = ConfirmUploadRequest(
                entityId = entityId,
                entityType = entityType,
                imageUrl = presignedData.accessUrl, // La URL pública final
                imageKey = presignedData.imageKey
            )
            val confirmResponse = apiService.confirmImageUpload(confirmRequest)

            if (!confirmResponse.isSuccessful || confirmResponse.body() == null) {
                val errorBody = confirmResponse.errorBody()?.string() ?: "Error desconocido confirmación"
                Log.e(tag, "Error al confirmar subida: ${confirmResponse.code()} - $errorBody")
                // Considera intentar borrar de S3 aquí si la confirmación falla
                // imageService.deleteImageFromS3(presignedData.imageKey) -> Necesitarías una función y endpoint para esto
                return ImageUploadResult.Error("Error al confirmar en servidor (${confirmResponse.code()})")
            }

            Log.d(tag, "Confirmación exitosa. URL final: ${presignedData.accessUrl}")
            return ImageUploadResult.Success(presignedData.accessUrl)

        } catch (e: Exception) {
            Log.e(tag, "Excepción durante el proceso de subida: ${e.message}", e)
            return ImageUploadResult.Error("Error durante la subida: ${e.message ?: "Desconocido"}", e)
        } finally {
            // Asegúrate de cerrar el InputStream
            try {
                inputStream?.close()
            } catch (ioe: Exception) {
                Log.e(tag, "Error al cerrar InputStream: ${ioe.message}")
            }
        }
    }
}