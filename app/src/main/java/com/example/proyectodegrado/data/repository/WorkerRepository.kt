package com.example.proyectodegrado.data.repository

import com.example.proyectodegrado.data.api.WorkerService
import com.example.proyectodegrado.data.model.AssignScheduleRequest
import com.example.proyectodegrado.data.model.Worker
import retrofit2.Response

class WorkerRepository(private val workerService: WorkerService) {

    suspend fun searchEmployees(query: String): List<Worker> {
        return workerService.searchEmployees(query)
    }

    suspend fun assignSchedule(userId: Int, storeId: Int, shiftId: Int): Boolean {
        val response: Response<Unit> = workerService.assignSchedule(userId, AssignScheduleRequest(storeId, shiftId))
        return response.isSuccessful
    }
}
