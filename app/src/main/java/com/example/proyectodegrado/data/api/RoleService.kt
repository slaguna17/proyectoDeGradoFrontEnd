package com.example.proyectodegrado.data.api

import com.example.proyectodegrado.data.model.AssignPermitsRequest
import com.example.proyectodegrado.data.model.CreateRoleResponse
import com.example.proyectodegrado.data.model.GenericRoleResponse
import com.example.proyectodegrado.data.model.Permit
import com.example.proyectodegrado.data.model.Role
import com.example.proyectodegrado.data.model.RoleRequest
import retrofit2.Response
import retrofit2.http.*

interface RoleService {
    // Get all Roles
    @GET("/api/roles")
    suspend fun getAllRoles(): List<Role>

    // Get specific Role
    @GET("/api/roles/{id}")
    suspend fun getRole(@Path("id") roleId: Int): Role

    // Create new Role
    @POST("/api/roles/createRole")
    suspend fun createRole(@Body request: RoleRequest): CreateRoleResponse

    // Update Role
    @PUT("/api/roles/updateRole/{id}")
    suspend fun updateRole(@Path("id") roleId: Int, @Body request: RoleRequest): Response<GenericRoleResponse>

    // Delete Role
    @DELETE("/api/roles/deleteRole/{id}")
    suspend fun deleteRole(@Path("id") roleId: Int): Response<GenericRoleResponse>

    // Get permits by role
    @GET("/api/roles/{id}/permits")
    suspend fun getPermitsByRole(@Path("id") roleId: Int): List<Permit>

    // Assign permits
    @POST("/api/roles/{id}/assignPermit")
    suspend fun assignPermitsToRole(
        @Path("id") roleId: Int,
        @Body request: AssignPermitsRequest
    ): Response<GenericRoleResponse>
}
