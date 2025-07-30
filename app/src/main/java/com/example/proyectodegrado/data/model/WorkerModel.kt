package com.example.proyectodegrado.data.model

import com.google.gson.annotations.SerializedName

data class Worker(
    val id: Int,
    val username: String,
    @SerializedName("full_name")
    val fullName: String?,
    val email: String?,
    val roles: List<String> = emptyList(),
    val avatar: String? = null,
    @SerializedName("store_id")
    val storeId: Int? = null,
    @SerializedName("store_name")
    val storeName: String? = null,
    @SerializedName("schedule_id")
    val scheduleId: Int? = null,
    @SerializedName("schedule_name")
    val scheduleName: String? = null
)

data class AssignScheduleRequest(
    @SerializedName("store_id")
    val storeId: Int,
    @SerializedName("schedule_id")
    val scheduleId: Int
)

data class AssignScheduleFormState(
    @SerializedName("store_id")
    val storeId: Int? = null,
    @SerializedName("schedule_id")
    val scheduleId: Int? = null
)

data class SelectedWorkerContext(
    val worker: Worker,
    val formState: AssignScheduleFormState = AssignScheduleFormState()
)
