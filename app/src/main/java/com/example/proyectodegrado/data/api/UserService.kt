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
    @POST("/api/users/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("/api/users")
    suspend fun getAllUsers(): List<User>

    @GET("/api/users/{id}")
    suspend fun getUser(@Path("id") userId: Int): User

    @POST("/api/users/register")
    suspend fun registerUser(@Body request: RegisterRequest): Response<RegisterResponse>

    @GET("/api/users/default/roles")
    suspend fun getRoles(): List<Role>

    @PUT("/api/users/changePassword/{id}")
    suspend fun changePassword(@Path("id") userId: Int): String

    @POST("/api/users/forgotPassword")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<ForgotPasswordResponse>

    @POST("/api/users/resetPassword")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<ResetPasswordResponse>

    // NUEVO: Update user profile
    @PUT("/api/users/updateUser/{id}")
    suspend fun updateUser(
        @Path("id") userId: Int,
        @Body fields: Map<String, @JvmSuppressWildcards Any>
    ): Response<Unit>
}
