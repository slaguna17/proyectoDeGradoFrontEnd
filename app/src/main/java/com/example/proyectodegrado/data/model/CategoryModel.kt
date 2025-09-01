package com.example.proyectodegrado.data.model

import com.google.gson.annotations.SerializedName
import android.net.Uri

data class Category(
    val id: Int,
    val name: String,
    val description: String?,
    /** KEY almacenada en BD (S3 key), puede ser null */
    val image: String?,
    /** URL para mostrar (pública o firmada) provista por el backend cuando usas ?signed=true */
    @SerializedName("image_url")
    val imageUrl: String? = null
)

data class CategoryRequest(
    val name: String,
    val description: String?,
    @SerializedName("image")
    val imageKey: String?
)

data class CreateCategoryFormState(
    val name: String = "",
    val description: String = "",
    val imageUrl: String? = null,
    val imageKey: String? = null,
    val localImageUri: Uri? = null
)
