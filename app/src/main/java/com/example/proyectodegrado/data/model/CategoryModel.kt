package com.example.proyectodegrado.data.model

import com.google.gson.annotations.SerializedName

data class Category(
    val id: Int,
    val name: String,
    val description: String,
    /** KEY almacenada en BD (S3 key), puede ser null */
    val image: String?,
    /** URL para mostrar (pública o firmada) provista por el backend cuando usas ?signed=true */
    @SerializedName("image_url")
    val imageUrl: String? = null
)

/** Para crear/editar: enviamos SOLO la key */
data class CategoryRequest(
    val name: String,
    val description: String,
    @SerializedName("image_key")
    val imageKey: String? // <- antes era "image"
)

/** Respuesta de create/update alineada al backend { message, category } */
data class CategoryWriteResponse(
    val message: String,
    val category: Category? = null
)

data class CreateCategoryFormState(
    val name: String = "",
    val description: String = "",
    /** Sólo para UI local; el backend recibe imageKey */
    val imageUrl: String? = null,
    val imageKey: String? = null
)
