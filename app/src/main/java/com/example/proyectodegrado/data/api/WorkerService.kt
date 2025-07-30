package com.example.proyectodegrado.data.api

import com.example.proyectodegrado.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface WorkerService {
    @GET("/api/users/employees")
    suspend fun getAllEmployees(): List<Worker>

    @GET("/api/users/search/employees")
    suspend fun searchEmployees(@Query("query") query: String): List<Worker>

    @GET("/api/users/employeesByStore/{storeId}")
    suspend fun getEmployeesByStore(@Path("storeId") storeId: Int): List<Worker>

    @PUT("/api/users/{id}/assign-schedule")
    suspend fun assignSchedule(
        @Path("id") userId: Int,
        @Body request: AssignScheduleRequest
    ): Response<Unit>

    @POST("/api/users/employees")
    suspend fun createEmployee(@Body request: RegisterWorkerRequest): Response<Unit>
}
