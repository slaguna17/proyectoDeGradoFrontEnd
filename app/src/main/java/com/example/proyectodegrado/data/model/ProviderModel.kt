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
    val notes: String
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
    @SerializedName("store_id")
    val storeId: Int? = null
)
