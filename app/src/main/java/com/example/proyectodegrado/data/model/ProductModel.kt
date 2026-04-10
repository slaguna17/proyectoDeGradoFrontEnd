package com.example.proyectodegrado.data.model

import android.net.Uri
import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("id") val id: Int,
    @SerializedName("SKU") val sku: String?,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("image") val image: String?,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("brand") val brand: String,
    @SerializedName("category_id") val categoryId: Int,
    @SerializedName("stock") val stock: Int?,
    @SerializedName("stores") val stores: List<StoreInfoFromProduct>?,
    @SerializedName("providers") val providers: List<ProviderInfoFromProduct>?,
    @SerializedName("purchase_price") val purchasePrice: Double,
    @SerializedName("sale_price") val salePrice: Double
)

data class StoreInfoFromProduct(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("pivot") val pivot: StoreProductPivot
)

data class ProviderInfoFromProduct(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)

data class StoreProductPivot(
    @SerializedName("stock") val stock: Int,
    @SerializedName("expiration_date") val expirationDate: String? = null
)

data class StoreProductRequest(
    val storeId: Int,
    val productId: Int,
    val stock: Int,
    @SerializedName("expiration_date") val expirationDate: String? = null
)

data class StoreProductUpsertResponse(
    val message: String,
    val data: StoreProductUpsertData? = null
)

data class StoreProductUpsertData(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("store_id") val storeId: Int,
    @SerializedName("product_id") val productId: Int,
    @SerializedName("stock") val stock: Int,
    @SerializedName("expiration_date") val expirationDate: String? = null
)

data class ProductRequest(
    @SerializedName("SKU") val sku: String?,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("image") val imageKey: String?,
    @SerializedName("brand") val brand: String,
    @SerializedName("category_id") val categoryId: Int,
    @SerializedName("store_id") val storeId: Int,
    @SerializedName("stock") val stock: Int,
    @SerializedName("purchase_price") val purchasePrice: Double,
    @SerializedName("sale_price") val salePrice: Double,
    @SerializedName("expiration_date") val expirationDate: String? = null,
    val providerIds: List<Int> = emptyList()
)

data class CreateProductFormState(
    val name: String = "",
    val description: String = "",
    val sku: String = "",
    val brand: String = "",
    val stock: String = "0",
    val purchasePrice: String = "0.0",
    val salePrice: String = "0.0",
    val expirationDate: String = "",
    val categoryId: Int = -1,
    val imageKey: String? = null,
    val imageUrl: String? = null,
    val localImageUri: Uri? = null
)