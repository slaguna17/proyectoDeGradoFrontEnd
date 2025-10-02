package com.example.proyectodegrado.data.repository

import com.example.proyectodegrado.data.api.PermitService
import com.example.proyectodegrado.data.model.Permit

class PermitRepository(private val permitService: PermitService) {
    suspend fun getAllPermits(): List<Permit> {
        return permitService.getAllPermits()
    }
}