package com.example.proyectodegrado.data.repository

import com.example.proyectodegrado.data.api.ScheduleService
import com.example.proyectodegrado.data.model.Schedule
import com.example.proyectodegrado.data.model.ScheduleRequest
import com.example.proyectodegrado.data.model.ScheduleResponse
import retrofit2.Response

class ScheduleRepository (private val scheduleService: ScheduleService) {
    //Get all Schedules
    suspend fun getAllSchedules(): List<Schedule>{
        return scheduleService.getAllSchedules()
    }

    //Get specific Schedule
    suspend fun getSchedule(scheduleId: Int): Schedule {
        return scheduleService.getSchedule(scheduleId)
    }

    //Create new Schedule
    suspend fun createSchedule(request: ScheduleRequest): Response<ScheduleResponse> {
        return scheduleService.createSchedule(request)
    }

    //Update Schedule
    suspend fun updateSchedule(scheduleId: Int, request: ScheduleRequest): Response<ScheduleResponse> {
        return scheduleService.updateSchedule(scheduleId,request)
    }

    //Delete Schedule
    suspend fun deleteSchedule(scheduleId: Int): Response<ScheduleResponse> {
        return scheduleService.deleteSchedule(scheduleId)
    }
}