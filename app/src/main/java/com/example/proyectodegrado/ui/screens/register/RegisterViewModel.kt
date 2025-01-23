package com.example.proyectodegrado.ui.screens.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectodegrado.data.model.RegisterRequest
import com.example.proyectodegrado.data.repository.UserRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class RegisterViewModel(private val userRepository: UserRepository) : ViewModel() {
    private var registerResult: String = ""

    fun registerUser(request: RegisterRequest, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = userRepository.registerUser(request)
                if (response.isSuccessful) {
                    registerResult = response.body()?.message ?: "Registration successful!"
                    onSuccess()
                } else {
                    onError("Failed: ${response.errorBody()?.string()}")
                }
            } catch (e: IOException) {
                onError("Network error: ${e.message}")
            } catch (e: HttpException) {
                onError("Unexpected error: ${e.message}")
            }
        }
    }
}