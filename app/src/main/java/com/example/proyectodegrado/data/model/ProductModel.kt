package com.example.proyectodegrado.data.model

import com.google.gson.annotations.SerializedName
import android.net.Uri

data class Product(
    @SerializedName("id") val id: Int,
    @SerializedName("SKU") val sku: String?,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("image") val image: String?,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("brand") val brand: String,
    @SerializedName("category_id") val categoryId: Int,
    @SerializedName("stock") val stock: Int?, // Stock general, puede ser null
    @SerializedName("stores") val stores: List<StoreInfoFromProduct>?
)

data class StoreInfoFromProduct(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("pivot") val pivot: StoreProductPivot
)

data class StoreProductRequest(val storeId: Int, val productId: Int, val stock: Int)

data class StoreProductPivot(
    @SerializedName("stock") val stock: Int
)

data class ProductRequest(
    @SerializedName("SKU") val sku: String?,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("image") val imageKey: String?,
    @SerializedName("brand") val brand: String,
    @SerializedName("category_id") val categoryId: Int,
    @SerializedName("store_id") val storeId: Int,
    @SerializedName("stock") val stock: Int
)

data class CreateProductFormState(
    val name: String = "",
    val description: String = "",
    val sku: String = "",
    val brand: String = "",
    val stock: String = "0",
    val expirationDate: String = "",
    val categoryId: Int = -1,
    val imageKey: String? = null,
    val imageUrl: String? = null,
    val localImageUri: Uri? = null
)
