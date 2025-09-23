package com.example.proyectodegrado.data.repository

import com.example.proyectodegrado.data.api.RoleService
import com.example.proyectodegrado.data.model.Role
import com.example.proyectodegrado.data.model.RoleRequest
import com.example.proyectodegrado.data.model.RoleResponse
import retrofit2.Response

class RoleRepository (private val roleService: RoleService) {
    //Get all Roles
    suspend fun getAllRoles(): List<Role>{
        return roleService.getAllRoles()
    }

    //Get specific Role
    suspend fun getRole(roleId: Int): Role {
        return roleService.getRole(roleId)
    }

    //Create new Role
    suspend fun createRole(request: RoleRequest): Response<RoleResponse> {
        return roleService.createRole(request)
    }

    //Update Role
    suspend fun updateRole(roleId: Int, request: RoleRequest): Response<RoleResponse> {
        return roleService.updateRole(roleId,request)
    }

    //Delete Role
    suspend fun deleteRole(roleId: Int): Response<RoleResponse> {
        return roleService.deleteRole(roleId)
    }

    //Assign Permits
    suspend fun assignPermits (request: Array<Int>): Response<RoleResponse> {
        return roleService.assignPermits(request)
    }
}