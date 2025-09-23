package com.example.proyectodegrado.ui.screens.role

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectodegrado.data.model.Role
import com.example.proyectodegrado.data.model.RoleRequest
import com.example.proyectodegrado.data.repository.RoleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class RoleViewModel(private val repository: RoleRepository) : ViewModel() {

    private val _roles = MutableStateFlow<List<Role>>(emptyList())
    val roles: StateFlow<List<Role>> = _roles.asStateFlow()

    fun fetchRoles(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                _roles.value = repository.getAllRoles()
                onSuccess()
            } catch (e: IOException) {
                onError("Error de red: ${e.message}")
            } catch (e: HttpException) {
                onError("Error del servidor: ${e.message}")
            } catch (e: Exception) {
                onError("Error desconocido: ${e.message}")
            }
        }
    }

    fun createRole(request: RoleRequest, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = repository.createRole(request)
                if (response.isSuccessful) {
                    onSuccess()
                    fetchRoles(onSuccess = {}, onError = {})
                } else {
                    onError("Falló la creación: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                onError("Error de conexión al crear.")
            }
        }
    }

    fun updateRole(id: Int, request: RoleRequest, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = repository.updateRole(id, request)
                if (response.isSuccessful) {
                    onSuccess()
                    fetchRoles(onSuccess = {}, onError = {})
                } else {
                    onError("Falló la actualización: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                onError("Error de conexión al actualizar.")
            }
        }
    }

    fun deleteRole(id: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = repository.deleteRole(id)
                if (response.isSuccessful) {
                    onSuccess()
                    fetchRoles(onSuccess = {}, onError = {})
                } else {
                    onError("Falló la eliminación: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                onError("Error de conexión al eliminar.")
            }
        }
    }
}