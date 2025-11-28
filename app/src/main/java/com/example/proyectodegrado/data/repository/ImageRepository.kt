package com.example.proyectodegrado.data.repository

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.example.proyectodegrado.data.api.ImageApiService
import com.example.proyectodegrado.data.model.ImageUploadResult
import com.example.proyectodegrado.data.model.PresignRequest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.source
import java.util.*

class ImageRepository(
    private val imageService: ImageApiService,
    private val context: Context
) {
    suspend fun uploadImage(
        imageUri: Uri,
        entityType: String,
        entityId: Int,
        fileKind: String
    ): ImageUploadResult {
        val contentType = context.contentResolver.getType(imageUri)
        if (contentType == null || !contentType.startsWith("image/")) {
            return ImageUploadResult.Error("Tipo de archivo no válido: $contentType")
        }
        val extension = when {
            contentType.endsWith("webp") -> ".webp"
            contentType.endsWith("png") -> ".png"
            contentType.endsWith("jpeg") -> ".jpeg"
            else -> ".jpg"
        }
        val folder = "$entityType/$entityId"
        val fileName = "${fileKind}_${UUID.randomUUID()}$extension"
        val presignRequest = PresignRequest(folder, fileName, contentType)

        val presignResponse = try {
            imageService.getPresignedUrl(presignRequest)
        } catch (e: Exception) {
            return ImageUploadResult.Error("Error de conexión al obtener URL: ${e.message}")
        }

        if (!presignResponse.isSuccessful || presignResponse.body() == null) {
            return ImageUploadResult.Error("El servidor no pudo generar la URL de subida.")
        }
        val presignData = presignResponse.body()!!

        val requestBody = object : RequestBody() {
            override fun contentType() = contentType.toMediaTypeOrNull()

            override fun contentLength(): Long {
                return context.contentResolver.query(imageUri, null, null, null, null)?.use { cursor ->
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    cursor.moveToFirst()
                    cursor.getLong(sizeIndex)
                } ?: -1L
            }

            override fun writeTo(sink: okio.BufferedSink) {
                context.contentResolver.openInputStream(imageUri)?.source()?.use(sink::writeAll)
            }
        }

        return try {
            val uploadResponse = imageService.uploadImageToS3(
                uploadUrl = presignData.url,
                contentType = contentType,
                imageBytes = requestBody
            )
            if (uploadResponse.isSuccessful) {
                ImageUploadResult.Success(imageKey = presignData.key)
            } else {
                ImageUploadResult.Error("La subida a S3 falló con código: ${uploadResponse.code()}")
            }
        } catch (e: Exception) {
            ImageUploadResult.Error("Error de red durante la subida a S3: ${e.message}")
        }
    }
}