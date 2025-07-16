package com.example.proyectodegrado.data.api

import com.example.proyectodegrado.data.model.ForgotPasswordRequest
import com.example.proyectodegrado.data.model.ForgotPasswordResponse
import com.example.proyectodegrado.data.model.LoginRequest
import com.example.proyectodegrado.data.model.LoginResponse
import com.example.proyectodegrado.data.model.RegisterRequest
import com.example.proyectodegrado.data.model.RegisterResponse
import com.example.proyectodegrado.data.model.ResetPasswordRequest
import com.example.proyectodegrado.data.model.ResetPasswordResponse
import com.example.proyectodegrado.data.model.Role
import com.example.proyectodegrado.data.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserService {
    //Valid auth credentials
    @POST("/api/users/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    //Get all Users, GET
    @GET("/api/users")
    suspend fun getAllUsers():List<User>

    //Get specific User
    @GET("/api/users/{id}")
    suspend fun getUser(@Path("id") userId: Int): User

    //Create new User
    @POST("/api/users/register")
    suspend fun registerUser(@Body request: RegisterRequest): Response<RegisterResponse>

    //Get user roles
    @GET("/api/users/default/roles")
    suspend fun getRoles(): List<Role>

    //Change Password
    @PUT("/api/users/changePassword/{id}")
    suspend fun changePassword(@Path("id") userId: Int): String

    //Forgot Password
    @POST("/api/users/forgotPassword")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<ForgotPasswordResponse>

    //Reset Password
    @POST("/api/users/resetPassword")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<ResetPasswordResponse>

}