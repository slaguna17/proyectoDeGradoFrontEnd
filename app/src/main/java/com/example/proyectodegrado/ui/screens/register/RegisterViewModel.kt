package com.example.proyectodegrado.ui.screens.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.proyectodegrado.data.model.RegisterRequest
import com.example.proyectodegrado.data.model.Role
import com.example.proyectodegrado.data.repository.ImageRepository
import com.example.proyectodegrado.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class RegisterViewModel(private val userRepository: UserRepository, private val imageRepository: ImageRepository) : ViewModel() {

    private var registerResult: String = ""
    private val _roles = MutableStateFlow<List<Role>>(emptyList())
    val roles: StateFlow<List<Role>> = _roles.asStateFlow()
    private var selectedRoleId: Int? = null

    fun registerUser(
        request: RegisterRequest,
        onSuccess: (NavController) -> Unit, // Modificado para recibir NavController
        onError: (String) -> Unit,
        navController: NavController // Recibe NavController como parámetro
    ) {
        viewModelScope.launch {
            try {
                if (selectedRoleId == null) {
                    onError("Por favor, selecciona un rol")
                    return@launch
                }
                val updatedRequest = request.copy(roleId = selectedRoleId!!)
                val response = userRepository.registerUser(updatedRequest)
                if (response.isSuccessful) {
                    registerResult = response.body()?.message ?: "Registration successful!"
                    onSuccess(navController) // Llama a onSuccess con NavController
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


    fun fetchRoles() {
        viewModelScope.launch {
            try {
                val roleList = userRepository.getRoles()
                _roles.value = roleList
            } catch (e: Exception) {
                // Maneja el error
            }
        }
    }

    fun setSelectedRoleId(roleId: Int) {
        selectedRoleId = roleId
    }

    val selectedRoleIdState: Int?
        get() = selectedRoleId
}