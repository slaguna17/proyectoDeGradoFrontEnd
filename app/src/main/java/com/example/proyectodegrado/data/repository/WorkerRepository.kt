package com.example.proyectodegrado.data.repository

import com.example.proyectodegrado.data.api.WorkerService
import com.example.proyectodegrado.data.model.AssignScheduleRequest
import com.example.proyectodegrado.data.model.Worker
import retrofit2.Response

class WorkerRepository(private val workerService: WorkerService) {

    // Search employees by text
    suspend fun searchEmployees(query: String): List<Worker> {
        return workerService.searchEmployees(query)
    }

    // Search employee by Store
    suspend fun getEmployeesByStore(storeId: Int): List<Worker> {
        return workerService.getEmployeesByStore(storeId)
    }

    // Assign schedule
    suspend fun assignSchedule(userId: Int, storeId: Int, scheduleId: Int): Boolean {
        val response: Response<Unit> = workerService.assignSchedule(
            userId,
            AssignScheduleRequest(storeId, scheduleId)
        )
        return response.isSuccessful
    }
}
