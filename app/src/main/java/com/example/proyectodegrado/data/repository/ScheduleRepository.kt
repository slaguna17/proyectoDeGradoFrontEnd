package com.example.proyectodegrado.data.repository

import com.example.proyectodegrado.data.api.ScheduleService
import com.example.proyectodegrado.data.model.Schedule
import com.example.proyectodegrado.data.model.ScheduleRequest
import com.example.proyectodegrado.data.model.ScheduleResponse
import retrofit2.Response

class ScheduleRepository (private val scheduleService: ScheduleService) {
    suspend fun getAllSchedules(): List<Schedule>{
        return scheduleService.getAllSchedules()
    }

    suspend fun getSchedule(scheduleId: Int): Schedule {
        return scheduleService.getSchedule(scheduleId)
    }

    suspend fun createSchedule(request: ScheduleRequest): Response<ScheduleResponse> {
        return scheduleService.createSchedule(request)
    }

    suspend fun updateSchedule(scheduleId: Int, request: ScheduleRequest): Response<ScheduleResponse> {
        return scheduleService.updateSchedule(scheduleId,request)
    }

    suspend fun deleteSchedule(scheduleId: Int): Response<ScheduleResponse> {
        return scheduleService.deleteSchedule(scheduleId)
    }
}