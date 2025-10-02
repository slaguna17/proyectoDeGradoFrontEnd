package com.example.proyectodegrado.data.api

import com.example.proyectodegrado.data.model.Permit
import retrofit2.http.GET

interface PermitService {
    @GET("/api/permits")
    suspend fun getAllPermits(): List<Permit>
}