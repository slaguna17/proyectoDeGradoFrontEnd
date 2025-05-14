package com.example.proyectodegrado.data.model

// Modelo para representar un empleado en la UI, combinado desde varias fuentes del backend
data class EmployeeUI(
    val userId: Int,
    val username: String,
    val fullName: String?,
    val email: String?,
    val avatarUrl: String?,
    val status: String?,
    val roleName: String?,
    val assignedStore: StoreInfo,
    val shifts: List<ShiftInfo>
)

data class StoreInfo(
    val id: Int,
    val name: String?
)

data class ShiftInfo(
    val id: Int,
    val name: String?
)

// Para listar empleados (respuesta del backend)
// Esta estructura debe coincidir con lo que retorna tu endpoint GET /api/employees
data class EmployeeDetailResponse(
    val userId: Int,
    val username: String,
    val fullName: String?,
    val email: String?,
    val avatarUrl: String?,
    val status: String?, // estado_usuario
    val roleName: String?, // rol_nombre
    val assignedStore: StoreInfo,
    val shifts: List<ShiftInfo> // Lista de turnos asignados a esa tienda principal
)

// Para crear una asignación de empleado (Request)
data class CreateEmployeeAssignmentRequest(
    val id_usuario: Int,
    val id_tienda: Int,
    val id_horario_principal: Int, // Turno principal en la tabla 'empleado'
    val shifts_ids: List<Int>? = null // IDs de horarios para la tabla 'usuario_horario_tienda'
)

// Para actualizar una asignación de empleado (Request)
data class UpdateEmployeeAssignmentRequest(
    val id_tienda: Int,
    val id_horario_principal: Int,
    val shifts_ids: List<Int>? = null
)

// Respuesta genérica para operaciones de creación/actualización/eliminación si el backend la provee
data class EmployeeAssignmentResponse(
    val message: String,
    val data: EmployeeEntry? = null // Opcional, si se retorna la entidad creada/actualizada
)

data class EmployeeEntry( // Representa una entrada en la tabla 'empleado'
    val id_usuario: Int,
    val id_tienda: Int,
    val id_horario: Int
    // ...otros campos si los hay
)

// Estado para el formulario de creación/edición de empleados
data class CreateEmployeeFormState(
    val selectedUserId: Int? = null, // Para seleccionar un usuario existente
    val selectedStoreId: Int? = null,
    val selectedPrincipalShiftId: Int? = null,
    val selectedShiftIds: List<Int> = emptyList(), // Para la tabla de unión
    // Puedes añadir más campos si se crea el usuario desde cero aquí
    val username: String = "", // Si se crea nuevo usuario
    val fullName: String = "", // Si se crea nuevo usuario
    val email: String = "",    // Si se crea nuevo usuario
    val password: String = "", // Si se crea nuevo usuario
    val avatarUrl: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)