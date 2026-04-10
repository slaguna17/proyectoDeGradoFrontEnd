package com.example.proyectodegrado.data.model

import com.google.gson.annotations.SerializedName

data class Provider(
    val id: Int,
    val name: String,
    val address: String,
    val email: String,
    val phone: String,
    @SerializedName("contact_person_name")
    val contactPersonName: String,
    val notes: String,
    val products: List<ProviderLinkedProduct> = emptyList()
)

data class ProviderLinkedProduct(
    val id: Int,
    @SerializedName("SKU")
    val sku: String? = null,
    val name: String,
    val description: String? = null,
    val image: String? = null,
    val brand: String? = null,
    @SerializedName("sale_price")
    val salePrice: Double? = null,
    @SerializedName("purchase_price")
    val purchasePrice: Double? = null,
    @SerializedName("category_id")
    val categoryId: Int? = null,
    @SerializedName("category_name")
    val categoryName: String? = null
)

data class ProviderRequest(
    val name: String,
    val address: String,
    val email: String,
    val phone: String,
    @SerializedName("contact_person_name")
    val contactPersonName: String,
    val notes: String
)

data class ProviderResponse(
    val message: String,
    val provider: Provider? = null
)

data class SyncProviderProductsRequest(
    val productIds: List<Int>
)

data class SyncProviderProductsResponse(
    val message: String,
    val products: List<ProviderLinkedProduct> = emptyList()
)