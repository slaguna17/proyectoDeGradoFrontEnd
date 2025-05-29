package com.example.proyectodegrado.data.api

import com.example.proyectodegrado.data.model.AssignScheduleRequest
import com.example.proyectodegrado.data.model.Worker
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface WorkerService {

    @GET("/api/users/search/employees")
    suspend fun searchEmployees(@Query("query") query: String): List<Worker>

    @PUT("/api/users/{id}/assign-schedule")
    suspend fun assignSchedule(
        @Path("id") userId: Int,
        @Body request: AssignScheduleRequest
    ): Response<Unit>
}
