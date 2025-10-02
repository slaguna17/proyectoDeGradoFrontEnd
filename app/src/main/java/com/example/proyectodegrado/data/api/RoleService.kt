package com.example.proyectodegrado.data.api

import com.example.proyectodegrado.data.model.Permit
import com.example.proyectodegrado.data.model.Role
import com.example.proyectodegrado.data.model.RoleRequest
import com.example.proyectodegrado.data.model.RoleResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RoleService {
    //Get all Roles, GET
    @GET("/api/roles")
    suspend fun getAllRoles():List<Role>

    //Get specific Role
    @GET("/api/roles/{id}")
    suspend fun getRole(@Path("id") roleId: Int): Role

    //Create new Role
    @POST("/api/roles/createRoles")
    suspend fun createRole(@Body request: RoleRequest): Response<RoleResponse>

    //Update Role
    @PUT("/api/roles/updateRole/{id}")
    suspend fun updateRole(@Path("id") roleId: Int, @Body request: RoleRequest): Response<RoleResponse>

    //Delete Role
    @DELETE("/api/roles/deleteRole/{id}")
    suspend fun deleteRole(@Path("id") roleId: Int): Response<RoleResponse>

    //Assign Permits
    @POST("/api/roles/{id}/assignPermit")
    suspend fun assignPermits(@Body request: Array<Int>): Response<RoleResponse>

    @GET("/api/roles/{id}/permits")
    suspend fun getPermitsByRole(@Path("id") roleId: Int): List<Permit>

    @POST("/api/roles/{id}/assignPermit")
    suspend fun assignPermitsToRole(@Path("id") roleId: Int, @Body permitIds: Map<String, List<Int>>): Response<Unit>
}