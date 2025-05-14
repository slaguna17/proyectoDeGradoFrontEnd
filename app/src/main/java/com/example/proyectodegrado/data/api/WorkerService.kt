package com.example.proyectodegrado.data.api

import com.example.proyectodegrado.data.model.CreateEmployeeAssignmentRequest
import com.example.proyectodegrado.data.model.EmployeeAssignmentResponse
import com.example.proyectodegrado.data.model.EmployeeDetailResponse
import com.example.proyectodegrado.data.model.UpdateEmployeeAssignmentRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface WorkerService { // 'WorkerService' como en tu DependencyProvider

    // GET /api/employees (según lo que definimos en el backend)
    @GET("api/employees") // Ajusta la ruta base si es necesario
    suspend fun getAllEmployees(): List<EmployeeDetailResponse> // La respuesta del backend

    // GET /api/employees/{userId}
    @GET("api/employees/{userId}")
    suspend fun getEmployeeByUserId(@Path("userId") userId: Int): Response<EmployeeDetailResponse> // O directamente EmployeeDetailResponse si no necesitas el Response wrapper

    // POST /api/employees
    @POST("api/employees")
    suspend fun createEmployeeAssignment(@Body request: CreateEmployeeAssignmentRequest): Response<EmployeeAssignmentResponse>

    // PUT /api/employees/{userId}
    @PUT("api/employees/{userId}")
    suspend fun updateEmployeeAssignment(@Path("userId") userId: Int, @Body request: UpdateEmployeeAssignmentRequest): Response<EmployeeAssignmentResponse>

    // DELETE /api/employees/{userId}
    @DELETE("api/employees/{userId}")
    suspend fun deleteEmployeeAssignment(@Path("userId") userId: Int): Response<EmployeeAssignmentResponse> // O Response<Unit> si no hay cuerpo
}