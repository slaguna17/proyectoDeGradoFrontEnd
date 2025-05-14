package com.example.proyectodegrado.data.repository

import com.example.proyectodegrado.data.api.WorkerService
import com.example.proyectodegrado.data.model.CreateEmployeeAssignmentRequest
import com.example.proyectodegrado.data.model.EmployeeAssignmentResponse
import com.example.proyectodegrado.data.model.EmployeeDetailResponse
import com.example.proyectodegrado.data.model.UpdateEmployeeAssignmentRequest
import retrofit2.Response

class WorkerRepository(private val workerService: WorkerService) {

    suspend fun getAllEmployees(): List<EmployeeDetailResponse> {
        return workerService.getAllEmployees()
    }

    suspend fun getEmployeeByUserId(userId: Int): Response<EmployeeDetailResponse> {
        return workerService.getEmployeeByUserId(userId)
    }

    suspend fun createEmployeeAssignment(request: CreateEmployeeAssignmentRequest): Response<EmployeeAssignmentResponse> {
        return workerService.createEmployeeAssignment(request)
    }

    suspend fun updateEmployeeAssignment(userId: Int, request: UpdateEmployeeAssignmentRequest): Response<EmployeeAssignmentResponse> {
        return workerService.updateEmployeeAssignment(userId, request)
    }

    suspend fun deleteEmployeeAssignment(userId: Int): Response<EmployeeAssignmentResponse> {
        return workerService.deleteEmployeeAssignment(userId)
    }
}