package com.example.proyectodegrado.data.model

import com.google.gson.annotations.SerializedName

data class Worker(
    val id: Int,
    val username: String,
    @SerializedName("full_name") val fullName: String?,
    val email: String?,
    val roles: List<String> = emptyList(),
    val avatar: String? = null,
    val phone: String,
    @SerializedName("store_id") val storeId: Int? = null,
    @SerializedName("store_name") val storeName: String? = null,
    @SerializedName("schedule_id") val scheduleId: Int? = null,
    @SerializedName("schedule_name") val scheduleName: String? = null
)

data class RegisterWorkerRequest(
    val username: String,
    val email: String,
    val password: String,
    @SerializedName("full_name") val fullName: String,
    val phone: String,
    val storeId: Int,
    val scheduleId: Int,
    val roleId: Int
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

data class UpdateWorkerRequest(
    val full_name: String,
    val email: String,
    val phone: String
)