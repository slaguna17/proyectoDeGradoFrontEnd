package com.example.proyectodegrado.data.model

import com.google.gson.annotations.SerializedName

data class Role(
    val id: Int,
    val name: String,
    val description: String,
    @SerializedName("is_admin")
    val isAdmin: Boolean
)
