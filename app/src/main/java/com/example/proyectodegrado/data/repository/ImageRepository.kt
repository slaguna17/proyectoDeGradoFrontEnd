// app/src/main/java/com/example/proyectodegrado/data/repository/ImageRepository.kt
package com.example.proyectodegrado.data.repository

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import com.example.proyectodegrado.data.api.ImageApiService
import com.example.proyectodegrado.data.model.PresignPutRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream

sealed class ImageUploadResult {
    data class Success(val imageKey: String, val readUrl: String?) : ImageUploadResult()
    data class Error(val message: String, val cause: Throwable? = null) : ImageUploadResult()
}

class ImageRepository(
    private val apiService: ImageApiService,
    private val context: Context
) {
    private val tag = "ImageRepository"

    private fun guessFileName(uri: Uri, fallback: String = "image.jpg"): String {
        return try {
            context.contentResolver.query(uri, null, null, null, null)?.use { c ->
                val nameIdx = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (c.moveToFirst() && nameIdx >= 0) c.getString(nameIdx) else fallback
            } ?: fallback
        } catch (_: Exception) { fallback }
    }

    private fun buildFolder(entityType: String, entityId: Int) =
        "${entityType.lowercase()}/${entityId}"

    suspend fun uploadWithPresignPut(
        imageUri: Uri,
        entityType: String, // "categories" | "products" | "users" | "stores"
        entityId: Int
    ): ImageUploadResult = withContext(Dispatchers.IO) {
        val contentType = context.contentResolver.getType(imageUri)
        if (contentType == null || !contentType.startsWith("image/")) {
            return@withContext ImageUploadResult.Error("Invalid or unknown content type: $contentType")
        }

        var inputStream: InputStream? = null
        try {
            inputStream = context.contentResolver.openInputStream(imageUri)
            if (inputStream == null) return@withContext ImageUploadResult.Error("Cannot read selected file.")

            val fileName = guessFileName(imageUri)
            val folder = buildFolder(entityType, entityId)

            // 1) Presign PUT
            val presignResp = apiService.presignPut(
                PresignPutRequest(folder = folder, fileName = fileName, contentType = contentType)
            )
            if (!presignResp.isSuccessful || presignResp.body() == null) {
                val code = presignResp.code()
                val body = presignResp.errorBody()?.string()
                Log.e(tag, "Presign error: $code - $body")
                return@withContext ImageUploadResult.Error("Server error getting presigned URL ($code)")
            }
            val presign = presignResp.body()!!

            // 2) Upload to S3 (PUT)
            val bytes = inputStream.readBytes()
            val rb = bytes.toRequestBody(contentType.toMediaTypeOrNull())

            val upload = apiService.uploadImageToS3(
                uploadUrl = presign.url,
                contentType = contentType,
                imageBytes = rb
            )
            if (!upload.isSuccessful) {
                val code = upload.code()
                val body = upload.errorBody()?.string()
                Log.e(tag, "S3 upload error: $code - $body")
                return@withContext ImageUploadResult.Error("S3 upload failed ($code)")
            }

            // 3) Listo: devolvemos la KEY (para guardar en BD)
            //    y (opcional) pedimos URL firmada si quisieras mostrar de inmediato.
            //    OJO: tu backend ya devuelve image_url en GET ?signed=true, así que esto es opcional.
            return@withContext ImageUploadResult.Success(imageKey = presign.key, readUrl = null)

        } catch (e: Exception) {
            Log.e(tag, "Upload exception: ${e.message}", e)
            return@withContext ImageUploadResult.Error("Upload error: ${e.message}", e)
        } finally {
            try { inputStream?.close() } catch (_: Exception) {}
        }
    }

    suspend fun getSignedUrl(key: String): String? {
        return apiService.getImageUrl(key = key, signed = true).body()?.url
    }
}
