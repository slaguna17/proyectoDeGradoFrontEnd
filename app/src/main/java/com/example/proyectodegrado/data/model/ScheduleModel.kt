package com.example.proyectodegrado.data.model

data class Schedule(
    val id: Int,
    val name: String,
    val length: String,
    val start_time: String,
    val end_time: String,
)

data class ScheduleRequest(
    val name: String,
    val length: String,
    val start_time: String,
    val end_time: String,
)

data class ScheduleResponse(
    val message: String,
    val storeId: Int? = null
)