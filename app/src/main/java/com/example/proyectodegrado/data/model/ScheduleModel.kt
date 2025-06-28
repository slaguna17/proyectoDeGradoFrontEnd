package com.example.proyectodegrado.data.model

import com.google.gson.annotations.SerializedName

data class Schedule(
    val id: Int,
    val name: String,
    val length: Int,
    @SerializedName("start_time")
    val startTime: String,
    @SerializedName("end_time")
    val endTime: String
)

data class ScheduleRequest(
    val name: String,
    val length: Int,
    @SerializedName("start_time")
    val startTime: String,
    @SerializedName("end_time")
    val endTime: String
)

data class ScheduleResponse(
    val message: String
)