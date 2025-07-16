package com.example.proyectodegrado.data.model

data class Worker(
    val id: Int,
    val username: String,
    val fullName: String?,
    val email: String?,
    val roles: List<String> = emptyList(),
    val avatar: String? = null,
    val storeId: Int? = null,
    val storeName: String? = null,
    val scheduleId: Int? = null,
    val scheduleName: String? = null
)

data class AssignScheduleRequest(
    val storeId: Int,
    val scheduleId: Int
)

data class AssignScheduleFormState(
    val storeId: Int? = null,
    val scheduleId: Int? = null
)

data class SelectedWorkerContext(
    val worker: Worker,
    val formState: AssignScheduleFormState = AssignScheduleFormState()
)
