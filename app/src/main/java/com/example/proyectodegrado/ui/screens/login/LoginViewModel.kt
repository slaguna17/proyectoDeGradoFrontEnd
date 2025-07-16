package com.example.proyectodegrado.ui.screens.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectodegrado.data.model.LoginResponse
import com.example.proyectodegrado.data.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel (private val userRepository: UserRepository) : ViewModel() {

    private val _loginState = MutableLiveData<Result<LoginResponse>>()
    val loginState: LiveData<Result<LoginResponse>> = _loginState

    private val _forgotPasswordResult = MutableLiveData<Result<String>>()
    val forgotPasswordResult: LiveData<Result<String>> = _forgotPasswordResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = userRepository.login(email, password)
                if (response.isSuccessful) {
                    response.body()?.let {
                        _loginState.postValue(Result.success(it)) // it es LoginResponse
                    } ?: run {
                        _loginState.postValue(Result.failure(Exception("Empty response body")))
                    }
                } else {
                    _loginState.postValue(Result.failure(Exception("Error: ${response.code()}")))
                }
            } catch (e: Exception) {
                _loginState.postValue(Result.failure(e))
            }
        }
    }

    fun sendPasswordReset(email: String) {
        viewModelScope.launch {
            try {
                val response = userRepository.forgotPassword(email)
                if (response.isSuccessful) {
                    _forgotPasswordResult.postValue(Result.success(response.body()?.message ?: "Te enviamos instrucciones si el correo existe."))
                } else {
                    _forgotPasswordResult.postValue(Result.failure(Exception("No se pudo enviar el correo. Intenta más tarde.")))
                }
            } catch (e: Exception) {
                _forgotPasswordResult.postValue(Result.failure(e))
            }
        }
    }

    fun resetPassword(token: String, newPassword: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = userRepository.resetPassword(token, newPassword)
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError("No se pudo cambiar la contraseña. Intenta de nuevo.")
                }
            } catch (e: Exception) {
                onError("Ocurrió un error: ${e.message}")
            }
        }
    }
}

