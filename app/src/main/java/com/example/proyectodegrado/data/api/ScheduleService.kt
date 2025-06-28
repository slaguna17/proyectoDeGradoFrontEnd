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
    //Get all Schedules, GET
    @GET("/api/schedules")
    suspend fun getAllSchedules():List<Schedule>

    //Get specific Schedule
    @GET("/api/schedules/{id}")
    suspend fun getSchedule(@Path("id") scheduleId: Int): Schedule

    //Create new Schedule
    @POST("/api/schedules/createSchedule")
    suspend fun createSchedule(@Body request: ScheduleRequest): Response<ScheduleResponse>

    //Update Schedule
    @PUT("/api/schedules/updateSchedule/{id}")
    suspend fun updateSchedule(@Path("id")scheduleId: Int, @Body request: ScheduleRequest): Response<ScheduleResponse>

    //Delete Schedule
    @DELETE("/api/schedules/deleteSchedule/{id}")
    suspend fun deleteSchedule(@Path("id")scheduleId: Int): Response<ScheduleResponse>
}