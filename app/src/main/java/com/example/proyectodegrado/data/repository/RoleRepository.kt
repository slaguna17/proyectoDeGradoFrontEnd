package com.example.proyectodegrado.data.repository

import com.example.proyectodegrado.data.api.RoleService
import com.example.proyectodegrado.data.model.AssignPermitsRequest
import com.example.proyectodegrado.data.model.CreateRoleResponse
import com.example.proyectodegrado.data.model.GenericRoleResponse
import com.example.proyectodegrado.data.model.Permit
import com.example.proyectodegrado.data.model.Role
import com.example.proyectodegrado.data.model.RoleRequest
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
    suspend fun createRole(request: RoleRequest): CreateRoleResponse {
        return roleService.createRole(request)
    }

    //Update Role
    suspend fun updateRole(roleId: Int, request: RoleRequest): Response<GenericRoleResponse> {
        return roleService.updateRole(roleId,request)
    }

    //Delete Role
    suspend fun deleteRole(roleId: Int): Response<GenericRoleResponse> {
        return roleService.deleteRole(roleId)
    }

    //Assign Permits
    suspend fun assignPermitsToRole(roleId: Int, permitIds: List<Int>) {
        val request = AssignPermitsRequest(permitIds = permitIds)
        roleService.assignPermitsToRole(roleId, request)
    }

    suspend fun getPermitsByRole(roleId: Int): List<Permit> {
        return roleService.getPermitsByRole(roleId)
    }
}