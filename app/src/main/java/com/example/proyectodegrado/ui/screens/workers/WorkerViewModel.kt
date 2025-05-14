package com.example.proyectodegrado.ui.screens.workers

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectodegrado.data.model.CreateEmployeeAssignmentRequest
import com.example.proyectodegrado.data.model.CreateEmployeeFormState
import com.example.proyectodegrado.data.model.EmployeeUI

import com.example.proyectodegrado.data.model.ShiftInfo
import com.example.proyectodegrado.data.model.Store
import com.example.proyectodegrado.data.model.UpdateEmployeeAssignmentRequest
import com.example.proyectodegrado.data.model.User // Asumiendo que tienes un modelo User simple para listar
import com.example.proyectodegrado.data.repository.ImageRepository
import com.example.proyectodegrado.data.repository.ImageUploadResult
import com.example.proyectodegrado.data.repository.ScheduleRepository
import com.example.proyectodegrado.data.repository.StoreRepository
import com.example.proyectodegrado.data.repository.UserRepository
import com.example.proyectodegrado.data.repository.WorkerRepository
import com.example.proyectodegrado.ui.components.UploadImageState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class WorkersViewModel(
    private val workerRepository: WorkerRepository,
    private val userRepository: UserRepository, // Para listar usuarios
    private val storeRepository: StoreRepository,   // Para listar tiendas
    private val scheduleRepository: ScheduleRepository, // Para listar horarios/turnos
    private val imageRepository: ImageRepository      // Para avatares
) : ViewModel() {

    private val _employees = MutableStateFlow<List<EmployeeUI>>(emptyList())
    val employees: StateFlow<List<EmployeeUI>> = _employees.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Para el diálogo de creación/edición
    private val _employeeFormState = MutableStateFlow(CreateEmployeeFormState())
    val employeeFormState: StateFlow<CreateEmployeeFormState> = _employeeFormState.asStateFlow()

    private val _imageUploadUiState = MutableStateFlow<UploadImageState>(UploadImageState.Idle)
    val imageUploadUiState: StateFlow<UploadImageState> = _imageUploadUiState.asStateFlow()

    // Listas para los dropdowns en los diálogos
    private val _availableUsers = MutableStateFlow<List<User>>(emptyList())
    val availableUsers: StateFlow<List<User>> = _availableUsers.asStateFlow()

    private val _availableStores = MutableStateFlow<List<Store>>(emptyList())
    val availableStores: StateFlow<List<Store>> = _availableStores.asStateFlow()

    private val _availableShifts = MutableStateFlow<List<ShiftInfo>>(emptyList()) // Usando ShiftInfo para consistencia
    val availableShifts: StateFlow<List<ShiftInfo>> = _availableShifts.asStateFlow()

    init {
        fetchInitialDataForDialogs()
        fetchEmployees()
    }

    fun fetchEmployees() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val employeeResponses = workerRepository.getAllEmployees() // Esto devuelve EmployeeDetailResponse
                // Mapear EmployeeDetailResponse a EmployeeUI si es necesario o usar EmployeeDetailResponse directamente
                // Por simplicidad, asumiremos que EmployeeDetailResponse ya tiene el formato que necesitamos para la UI
                // o que el servicio ya lo mapea a una clase común EmployeeUI si es diferente.
                // Aquí asumiré que la respuesta del repo ya es adecuada o la mapeamos aquí.
                _employees.value = employeeResponses.map { response ->
                    EmployeeUI(
                        userId = response.userId,
                        username = response.username,
                        fullName = response.fullName,
                        email = response.email,
                        avatarUrl = response.avatarUrl,
                        status = response.status,
                        roleName = response.roleName,
                        assignedStore = response.assignedStore, // Ya es StoreInfo
                        shifts = response.shifts // Ya es List<ShiftInfo>
                    )
                }
            } catch (e: IOException) {
                _errorMessage.value = "Error de red: ${e.message}"
            } catch (e: HttpException) {
                _errorMessage.value = "Error HTTP: ${e.code()} ${e.message()}"
            } catch (e: Exception) {
                _errorMessage.value = "Error desconocido: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun fetchInitialDataForDialogs() {
        viewModelScope.launch {
            try {
                // Obtener todos los usuarios (o usuarios sin rol de empleado asignado)
                _availableUsers.value = userRepository.getAllUsers() // Asegúrate que este método exista y devuelva List<User>
                _availableStores.value = storeRepository.getAllStores() // Asegúrate que este método exista
                // Para los horarios/turnos, necesitamos una lista simple de ShiftInfo
                val schedules = scheduleRepository.getAllSchedules() // Asume que devuelve List<Schedule> de tu modelo
                _availableShifts.value = schedules.map { ShiftInfo(id = it.id, name = it.name) }

            } catch (e: Exception) {
                _errorMessage.value = "Error cargando datos para diálogos: ${e.message}"
                // Podrías querer manejar esto más granularmente
            }
        }
    }

    fun updateEmployeeFormState(newState: CreateEmployeeFormState) {
        _employeeFormState.value = newState
    }

    fun resetEmployeeFormState(employeeToEdit: EmployeeUI? = null) {
        if (employeeToEdit != null) {
            _employeeFormState.value = CreateEmployeeFormState(
                selectedUserId = employeeToEdit.userId,
                username = employeeToEdit.username, // No editable usualmente si se selecciona usuario
                fullName = employeeToEdit.fullName ?: "",
                email = employeeToEdit.email ?: "",
                avatarUrl = employeeToEdit.avatarUrl,
                selectedStoreId = employeeToEdit.assignedStore.id,
                // Para id_horario_principal, necesitamos una lógica. Si EmployeeDetailUI no lo tiene directo
                // podemos dejarlo null o intentar inferirlo de la lista de shifts.
                // Por ahora lo dejo null, y el diálogo tendría que manejarlo.
                selectedPrincipalShiftId = employeeToEdit.shifts.firstOrNull()?.id, // Ejemplo: tomar el primero como principal
                selectedShiftIds = employeeToEdit.shifts.map { it.id }
            )
        } else {
            _employeeFormState.value = CreateEmployeeFormState()
        }
        _imageUploadUiState.value = UploadImageState.Idle
    }


    fun handleEmployeeAvatarSelection(uri: Uri?) {
        if (uri == null) {
            _employeeFormState.update { it.copy(avatarUrl = null) } // Permitir limpiar la imagen
            _imageUploadUiState.value = UploadImageState.Idle
            return
        }
        viewModelScope.launch {
            _imageUploadUiState.value = UploadImageState.Loading
            // Asumimos que el id de usuario es el que se usa para la carpeta/nombre de archivo del avatar
            // Si es un nuevo empleado sin userId aun, podrías usar un placeholder o generar un ID temporal
            val targetId = _employeeFormState.value.selectedUserId ?: 0 // O manejarlo de otra forma
            when (val result = imageRepository.getPresignedUrlAndUpload(uri, "avatar", targetId)) {
                is ImageUploadResult.Success -> {
                    _imageUploadUiState.value = UploadImageState.Idle
                    _employeeFormState.update { it.copy(avatarUrl = result.accessUrl) }
                }
                is ImageUploadResult.Error -> {
                    _imageUploadUiState.value = UploadImageState.Error(result.message)
                }
            }
        }
    }

    fun createOrUpdateEmployee() {
        viewModelScope.launch {
            val currentState = _employeeFormState.value
            _isLoading.value = true
            _errorMessage.value = null

            // Validaciones básicas
            if (currentState.selectedUserId == null && currentState.username.isBlank()) {
                _errorMessage.value = "Debe seleccionar un usuario o ingresar un nombre de usuario."
                _isLoading.value = false
                return@launch
            }
            if (currentState.selectedStoreId == null) {
                _errorMessage.value = "Debe seleccionar una tienda."
                _isLoading.value = false
                return@launch
            }
            if (currentState.selectedPrincipalShiftId == null) {
                _errorMessage.value = "Debe seleccionar un turno principal."
                _isLoading.value = false
                return@launch
            }

            try {
                val isUpdating = currentState.selectedUserId != null // Asumimos que si hay selectedUserId, es una actualización de asignación
                // O si es un diálogo de edición, tendrás un ID de empleado específico

                // Lógica para decidir si es creación de usuario + empleado, o asignación de empleado
                // Por ahora, simplificaremos y asumiremos que el `id_usuario` viene de la selección
                // y que la tabla `empleado` se crea o actualiza.

                if (isUpdating && currentState.selectedUserId != null) { // Lógica de Actualización
                    val request = UpdateEmployeeAssignmentRequest(
                        id_tienda = currentState.selectedStoreId!!,
                        id_horario_principal = currentState.selectedPrincipalShiftId!!,
                        shifts_ids = currentState.selectedShiftIds.distinct() // Asegurar IDs únicos
                    )
                    val response = workerRepository.updateEmployeeAssignment(currentState.selectedUserId!!, request)
                    if (response.isSuccessful) {
                        fetchEmployees()
                    } else {
                        _errorMessage.value = "Error al actualizar empleado: ${response.errorBody()?.string()}"
                    }
                } else { // Lógica de Creación
                    // Aquí necesitarías un id_usuario. Si es un nuevo usuario, deberías crearlo primero
                    // o tu endpoint de backend /api/employees (POST) se encarga de crear USUARIO y EMPLEADO.
                    // Asumimos que selectedUserId es el ID del usuario a asignar como empleado.
                    if (currentState.selectedUserId == null) {
                        _errorMessage.value = "Debe seleccionar un usuario para asignar como empleado."
                        _isLoading.value = false
                        return@launch
                    }

                    val request = CreateEmployeeAssignmentRequest(
                        id_usuario = currentState.selectedUserId!!,
                        id_tienda = currentState.selectedStoreId!!,
                        id_horario_principal = currentState.selectedPrincipalShiftId!!,
                        shifts_ids = currentState.selectedShiftIds.distinct()
                    )
                    val response = workerRepository.createEmployeeAssignment(request)
                    if (response.isSuccessful) {
                        fetchEmployees()
                    } else {
                        _errorMessage.value = "Error al crear empleado: ${response.errorBody()?.string()}"
                    }
                }
            } catch (e: IOException) {
                _errorMessage.value = "Error de red: ${e.message}"
            } catch (e: HttpException) {
                _errorMessage.value = "Error HTTP: ${e.code()} ${e.message()}"
            } catch (e: Exception) {
                _errorMessage.value = "Error desconocido: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteEmployee(userId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = workerRepository.deleteEmployeeAssignment(userId)
                if (response.isSuccessful) {
                    fetchEmployees()
                } else {
                    _errorMessage.value = "Error al eliminar empleado: ${response.errorBody()?.string()}"
                }
            } catch (e: IOException) {
                _errorMessage.value = "Error de red: ${e.message}"
            } catch (e: HttpException) {
                _errorMessage.value = "Error HTTP: ${e.code()} ${e.message()}"
            } catch (e: Exception) {
                _errorMessage.value = "Error desconocido: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    private val _isNewUserMode = MutableStateFlow(false) // Por defecto, seleccionar existente
    val isNewUserMode: StateFlow<Boolean> = _isNewUserMode.asStateFlow()

    fun toggleNewUserMode(isNew: Boolean) {
        _isNewUserMode.value = isNew
        // Cuando cambias de modo, resetea campos relevantes del formState
        if (isNew) {
            _employeeFormState.update { it.copy(selectedUserId = null, username = "", fullName = "", email = "", password = "", avatarUrl = null) }
        } else {
            _employeeFormState.update { it.copy(username = "", fullName = "", email = "", password = "") } // Limpia campos de nuevo usuario
        }
    }
}