package com.example.proyectodegrado.ui.screens.role

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectodegrado.data.model.Permit
import com.example.proyectodegrado.data.model.Role
import com.example.proyectodegrado.data.model.RoleRequest
import com.example.proyectodegrado.data.repository.PermitRepository
import com.example.proyectodegrado.data.repository.RoleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RoleScreenUiState(
    val roles: List<Role> = emptyList(),
    val allPermits: List<Permit> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isDialogShown: Boolean = false,
    val currentRoleInDialog: Role? = null,
    val selectedPermitIdsInDialog: Set<Int> = emptySet()
)

class RoleViewModel(
    private val roleRepository: RoleRepository,
    private val permitRepository: PermitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RoleScreenUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val roles = roleRepository.getAllRoles()
                val permits = permitRepository.getAllPermits()
                _uiState.update { it.copy(roles = roles, allPermits = permits, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun openDialog(role: Role? = null) {
        _uiState.update { it.copy(isLoading = true) }
        if (role != null) { // Editando rol
            viewModelScope.launch {
                try {
                    val assignedPermits = roleRepository.getPermitsByRole(role.id)
                    _uiState.update {
                        it.copy(
                            isDialogShown = true,
                            currentRoleInDialog = role,
                            selectedPermitIdsInDialog = assignedPermits.map { p -> p.id }.toSet(),
                            isLoading = false
                        )
                    }
                } catch (e: Exception) {
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
            }
        } else { // Creando rol
            _uiState.update { it.copy(isDialogShown = true, currentRoleInDialog = null, selectedPermitIdsInDialog = emptySet(), isLoading = false) }
        }
    }

    fun closeDialog() {
        _uiState.update { it.copy(isDialogShown = false) }
    }

    fun onPermitCheckedChange(permitId: Int, isChecked: Boolean) {
        val currentSelection = _uiState.value.selectedPermitIdsInDialog.toMutableSet()
        if (isChecked) currentSelection.add(permitId) else currentSelection.remove(permitId)
        _uiState.update { it.copy(selectedPermitIdsInDialog = currentSelection) }
    }

    fun saveRole(name: String, description: String, isAdmin: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val roleToSave = _uiState.value.currentRoleInDialog
                val permitIds = _uiState.value.selectedPermitIdsInDialog.toList()
                val roleRequest = RoleRequest(name, description, isAdmin)

                val roleId = if (roleToSave != null) {
                    roleRepository.updateRole(roleToSave.id, roleRequest)
                    roleToSave.id
                } else {
                    val newRoleResponse = roleRepository.createRole(roleRequest)
                    // Asumimos que la respuesta de creación devuelve el rol o su ID
                    // Si no, necesitarás hacer un GET para obtener el ID
                    // Por ahora, asumimos que el backend no lo devuelve y hacemos un GET por nombre.
                    // ESTO REQUIERE QUE LOS NOMBRES DE ROLES SEAN ÚNICOS EN EL BACKEND
                    roleRepository.getAllRoles().find { it.name == name }?.id
                        ?: throw Exception("No se pudo obtener el ID del nuevo rol")
                }

                roleRepository.assignPermitsToRole(roleId, permitIds)
                closeDialog()
                loadInitialData()
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error al guardar: ${e.message}") }
            }
        }
    }

    fun deleteRole(roleId: Int) {
        viewModelScope.launch {
            try {
                roleRepository.deleteRole(roleId)
                loadInitialData()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
}