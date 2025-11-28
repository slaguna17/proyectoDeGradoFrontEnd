package com.example.proyectodegrado.data.api

import com.example.proyectodegrado.data.model.Schedule
import com.example.proyectodegrado.data.model.ScheduleRequest
import com.example.proyectodegrado.data.model.ScheduleResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ScheduleService {
    @GET("/api/schedules")
    suspend fun getAllSchedules():List<Schedule>

    @GET("/api/schedules/{id}")
    suspend fun getSchedule(@Path("id") scheduleId: Int): Schedule

    @POST("/api/schedules/createSchedule")
    suspend fun createSchedule(@Body request: ScheduleRequest): Response<ScheduleResponse>

    @PUT("/api/schedules/updateSchedule/{id}")
    suspend fun updateSchedule(@Path("id")scheduleId: Int, @Body request: ScheduleRequest): Response<ScheduleResponse>

    @DELETE("/api/schedules/deleteSchedule/{id}")
    suspend fun deleteSchedule(@Path("id")scheduleId: Int): Response<ScheduleResponse>
}