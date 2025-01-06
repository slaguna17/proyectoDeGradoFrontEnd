package com.example.proyectodegrado.data.repository


import LoginResponse
import com.example.proyectodegrado.data.api.RetrofitClient
import com.example.proyectodegrado.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository { // Podría ser UserRepository si manejas usuarios
    private val apiService = RetrofitClient.apiService

//    suspend fun getAllUsers(): List<User>? = withContext(Dispatchers.IO) {
//        try {
//            apiService.getAllUsers()
//        } catch (e: Exception) {
//            // Manejo de errores (e.g., IOException, HttpException)
//            null // O lanzar una excepción personalizada
//        }
//    }

    suspend fun getUser(userId: Int): User? = withContext(Dispatchers.IO) {
        try {
            apiService.getUser(userId)
        } catch (e: Exception) {
            // Manejo de errores (e.g., IOException, HttpException)
            null // O lanzar una excepción personalizada
        }
    }

    suspend fun login(email: String, password: String) = withContext(Dispatchers.IO) {
        try {
            apiService.login(email,password)
        } catch (e: Exception) {
            // Manejo de errores (e.g., IOException, HttpException)
            null // O lanzar una excepción personalizada
        }
    }
}