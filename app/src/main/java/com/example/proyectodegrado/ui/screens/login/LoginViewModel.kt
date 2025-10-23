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
                    userEmail = email, // Guardamos el email temporalmente
                    userName = user.username?.ifBlank { user.full_name },
                    menu = menu
                )

                // 2. Si "Recuérdame" NO está marcado, borramos solo el email guardado.
                //    El resto de la sesión (como el ID) permanece.
                if (!rememberMe) {
                    prefs.clearUserEmail()
                }
                _loginState.value = LoginState.Success
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "Error desconocido")
            }
        }
    }
    fun clearError() {
        if (loginState.value is LoginState.Error) {
            _loginState.value = LoginState.Idle
        }
    }
}
