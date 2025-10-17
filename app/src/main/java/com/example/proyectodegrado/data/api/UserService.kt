package com.example.proyectodegrado.data.api

import com.example.proyectodegrado.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface UserService {

    // ---------- Auth ----------
    @POST("/api/users/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("/api/users/me")
    suspend fun me(@Header("Authorization") bearer: String): MeResponse

    @GET("/api/menu")
    suspend fun menu(@Header("Authorization") bearer: String): MenuResponse

    // ---------- Users ----------
    @GET("/api/users")
    suspend fun getAllUsers(@Query("signed") signed: Boolean = true): List<User>

    @GET("/api/users/{id}")
    suspend fun getUser(@Path("id") userId: Int, @Query("signed") signed: Boolean = true): User

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

    @PUT("/api/users/updateUser/{id}")
    suspend fun updateUser(
        @Path("id") userId: Int,
        @Body fields: Map<String, @JvmSuppressWildcards Any?>
    ): Response<Unit>
}
