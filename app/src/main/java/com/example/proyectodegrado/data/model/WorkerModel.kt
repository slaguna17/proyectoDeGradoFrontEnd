package com.example.proyectodegrado.data.model

// Representa al empleado recuperado desde la API

data class Worker(
    val id: Int,
    val username: String,
    val fullName: String?,
    val email: String?,
    val roles: List<String> = emptyList(),
    val avatar: String? = null
)

data class AssignScheduleRequest(
    val storeId: Int,
    val scheduleId: Int
)

data class AssignScheduleFormState(
    val storeId: Int? = null,
    val scheduleId: Int? = null
)

// Para mostrar diálogos con ID de usuario preseleccionado
data class SelectedWorkerContext(
    val worker: Worker,
    val formState: AssignScheduleFormState = AssignScheduleFormState()
)