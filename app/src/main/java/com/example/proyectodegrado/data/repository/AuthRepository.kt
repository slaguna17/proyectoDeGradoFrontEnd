package com.example.proyectodegrado.data.repository


import ApiService
import LoginRequest
import LoginResponse
import retrofit2.Response



class AuthRepository (private val apiService: ApiService) {
    suspend fun login(email: String, password: String): Response<LoginResponse> {
        return apiService.login(LoginRequest(email, password))
    }
}
