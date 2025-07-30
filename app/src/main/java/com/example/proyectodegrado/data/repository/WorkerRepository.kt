package com.example.proyectodegrado.data.repository

import com.example.proyectodegrado.data.api.WorkerService
import com.example.proyectodegrado.data.model.*

class WorkerRepository(
    private val workerService: WorkerService
) {

    suspend fun getAllEmployees(): List<Worker> {
        return try {
            workerService.getAllEmployees()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getEmployeesByStore(storeId: Int): List<Worker> {
        return try {
            workerService.getEmployeesByStore(storeId)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun assignSchedule(userId: Int, storeId: Int, scheduleId: Int): Boolean {
        return try {
            val response = workerService.assignSchedule(
                userId,
                AssignScheduleRequest(storeId, scheduleId)
            )
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun registerWorker(request: RegisterWorkerRequest): Result<Unit> {
        return try {
            val response = workerService.createEmployee(request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
