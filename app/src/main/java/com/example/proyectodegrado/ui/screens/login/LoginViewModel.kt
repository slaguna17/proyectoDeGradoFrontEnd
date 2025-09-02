package com.example.proyectodegrado.ui.screens.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectodegrado.data.model.LoginResponse
import com.example.proyectodegrado.data.repository.UserRepository
import kotlinx.coroutines.launch
import retrofit2.Response

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _loginState = MutableLiveData<Result<LoginResponse>>()
    val loginState: LiveData<Result<LoginResponse>> = _loginState

    private val _forgotPasswordResult = MutableLiveData<Result<String>>()
    val forgotPasswordResult: LiveData<Result<String>> = _forgotPasswordResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val http: Response<LoginResponse> = userRepository.login(email, password)

                if (http.isSuccessful) {
                    val body: LoginResponse? = http.body()
                    if (body != null) {
                        _loginState.postValue(Result.success(body))
                    } else {
                        _loginState.postValue(Result.failure(Exception("Respuesta vacía del servidor")))
                    }
                } else {
                    _loginState.postValue(
                        Result.failure(
                            Exception("HTTP ${http.code()} ${http.message()}")
                        )
                    )
                }
            } catch (e: Exception) {
                _loginState.postValue(Result.failure(e))
            }
        }
    }

    fun sendPasswordReset(email: String) {
        viewModelScope.launch {
            try {
                val http: retrofit2.Response<com.example.proyectodegrado.data.model.ForgotPasswordResponse> =
                    userRepository.forgotPassword(email)

                if (http.isSuccessful) {
                    _forgotPasswordResult.postValue(
                        Result.success(http.body()?.message ?: "Si el correo existe, te enviamos instrucciones.")
                    )
                } else {
                    _forgotPasswordResult.postValue(
                        Result.failure(Exception("No se pudo enviar el correo. Intenta más tarde."))
                    )
                }
            } catch (e: Exception) {
                _forgotPasswordResult.postValue(Result.failure(e))
            }
        }
    }

    fun resetPassword(
        token: String,
        newPassword: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val http: retrofit2.Response<com.example.proyectodegrado.data.model.ResetPasswordResponse> =
                    userRepository.resetPassword(token, newPassword)
                if (http.isSuccessful) {
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
