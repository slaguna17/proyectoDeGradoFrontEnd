package com.example.proyectodegrado.ui.screens.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectodegrado.data.repository.UserRepository
import com.example.proyectodegrado.di.AppPreferences
import com.example.proyectodegrado.di.DependencyProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException


sealed interface LoginState {
    object Idle : LoginState
    object Loading : LoginState
    object Success : LoginState
    data class Error(val message: String) : LoginState
}

class LoginViewModel(
    private val userRepository: UserRepository,
    private val prefs: AppPreferences
) : ViewModel() {

    private val _loginState = MutableLiveData<LoginState>(LoginState.Idle)
    val loginState: LiveData<LoginState> = _loginState

    fun login(email: String, password: String, rememberMe: Boolean) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val response = withTimeout(15000L) {
                    userRepository.login(email, password)
                }
                val isAdmin = response.isAdmin
                val user    = response.user
                val storeId = prefs.getStoreId()?.toIntOrNull() ?: 1
                val menu = response.menu

                DependencyProvider.saveCurrentSession(
                    userId = user.id,
                    storeId = storeId,
                    isAdmin = isAdmin,
                    userEmail = email,
                    userName = user.username?.ifBlank { user.full_name },
                    menu = menu
                )

                if (!rememberMe) {
                    prefs.clearUserEmail()
                }
                _loginState.value = LoginState.Success
            } catch (e: Exception) {
                val userMessage = when (e) {
                    is HttpException -> {
                        when (e.code()) {
                            401 -> "Credenciales inválidas"
                            in 500..599 -> "Error en el servidor. Intenta nuevamente más tarde."
                            else -> "Error al iniciar sesión (${e.code()})"
                        }
                    }
                    is SocketTimeoutException -> "Tiempo de espera agotado. Intenta nuevamente."
                    is IOException -> "No se pudo conectar con el servidor. Revisa tu conexión a internet."
                    else -> e.message ?: "Error desconocido"
                }

                _loginState.value = LoginState.Error(userMessage)
            }
        }
    }

    fun clearError() {
        if (loginState.value is LoginState.Error) {
            _loginState.value = LoginState.Idle
        }
    }

    fun resetState() {
        _loginState.value = LoginState.Idle
    }
}
