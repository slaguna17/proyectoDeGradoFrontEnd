package com.example.proyectodegrado.data.model

import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("id")
    val id: Int,
    @SerializedName("SKU")
    val sku: String?,
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("image")
    val image: String?,
    @SerializedName("brand")
    val brand: String,
    @SerializedName("category_id")
    val categoryId: Int,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("updated_at")
    val updatedAt: String?,

    // --- CORRECCIÓN 1: AÑADIR CAMPOS NULABLES ---
    // Estos campos pueden venir o no, dependiendo del endpoint
    @SerializedName("stock")
    val stock: Int?,
    @SerializedName("expiration_date")
    val expirationDate: String?,
    // --- FIN CORRECCIÓN 1 ---

    @SerializedName("stores")
    val stores: List<StoreInfoFromProduct>?,
    @SerializedName("providers")
    val providers: List<ProviderInfoFromProduct>?
)

// --- CORRECCIÓN 2: AÑADIR LA CLASE 'PIVOT' QUE USA EL MODELO DE TIENDA ---
// El modelo StoreInfoFromProduct ahora puede contener la información de la tabla pivote
data class StoreInfoFromProduct(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("pivot") val pivot: StoreProductPivot // <-- Añadido para el stock
)

data class StoreProductPivot(
    @SerializedName("stock") val stock: Int,
    @SerializedName("expiration_date") val expirationDate: String?
)
// --- FIN CORRECCIÓN 2 ---


data class ProviderInfoFromProduct(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)

data class ProductRequest(
    @SerializedName("SKU")
    val sku: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("brand")
    val brand: String,
    @SerializedName("category_id")
    val categoryId: Int,
    @SerializedName("store_id")
    val storeId: Int,
    @SerializedName("stock")
    val stock: Int,
    @SerializedName("expiration_date")
    val expirationDate: String
)

data class ProductResponse(
    val message: String,
    val product: Product? = null
)

data class CreateProductFormState(
    val name: String = "",
    val description: String = "",
    val sku: String = "",
    val brand: String = "",
    val stock: String = "0",
    val expirationDate: String = "",
    val categoryId: Int = -1,
    val imageUrl: String? = null
)