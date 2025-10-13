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
    val selectedPermitId: Int? = null
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
            _uiState.update { it.copy(isLoading = true, error = null) }
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
        _uiState.update { it.copy(isLoading = true, error = null) }
        if (role != null) {
            // --- UPDATE ---
            viewModelScope.launch {
                try {
                    val assignedPermits = roleRepository.getPermitsByRole(role.id)
                    _uiState.update {
                        it.copy(
                            isDialogShown = true,
                            currentRoleInDialog = role,
                            selectedPermitId = assignedPermits.firstOrNull()?.id,
                            isLoading = false
                        )
                    }
                } catch (e: Exception) {
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
            }
        } else {
            // --- CREATE ---
            _uiState.update {
                it.copy(
                    isDialogShown = true,
                    currentRoleInDialog = null,
                    selectedPermitId = null,
                    isLoading = false
                )
            }
        }
    }

    fun closeDialog() {
        _uiState.update { it.copy(isDialogShown = false) }
    }

    fun onPermitSelected(permitId: Int) {
        _uiState.update { it.copy(selectedPermitId = permitId) }
    }

    fun onErrorShown() {
        _uiState.update { it.copy(error = null) }
    }

    fun saveRole(name: String, description: String, isAdmin: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val roleToEdit = _uiState.value.currentRoleInDialog
                val selectedPermit = _uiState.value.selectedPermitId
                val permitIds = if (selectedPermit != null) listOf(selectedPermit) else emptyList()
                val roleRequest = RoleRequest(name, description, isAdmin)

                if (roleToEdit != null) {
                    // --- UPDATE ---
                    roleRepository.updateRole(roleToEdit.id, roleRequest)
                    roleRepository.assignPermitsToRole(roleToEdit.id, permitIds)
                } else {
                    // --- CREATE ---
                    val newRoleResponse = roleRepository.createRole(roleRequest)
                    val newRoleId = newRoleResponse.role.id
                    roleRepository.assignPermitsToRole(newRoleId, permitIds)
                }

                closeDialog()
                loadInitialData()
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error al guardar: ${e.message}") }
            }
        }
    }

    fun deleteRole(roleId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                roleRepository.deleteRole(roleId)
                loadInitialData()
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}