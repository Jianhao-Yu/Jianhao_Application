package com.example.jianhao_application.api

import retrofit2.Response
import retrofit2.http.*

data class UserResponse(
    val id: String,
    val token: String
)

data class TodoItem(
    val id: String,
    val name: String,
    var isCompleted: Boolean
)

data class RegisterPayload(
    val name: String,
    val email: String,
    val password: String
)


data class LoginPayload(
    val email: String,
    val password: String
)


data class TodoRequest(
    val name: String,
    val isCompleted: Boolean
)

interface ApiService {
    @POST("/api/users/register")
    suspend fun registerUser(@Body request: RegisterPayload): Response<UserResponse>

    @POST("/api/users/login")
    suspend fun loginUser(@Body request: LoginPayload): Response<UserResponse>

    @GET("/api/users/{user_id}/todos")
    suspend fun getTodos(
        @Path("user_id") userId: String,
        @Query("apikey") apiKey: String
    ): Response<List<TodoItem>>

    @POST("/api/users/{user_id}/todos")
    suspend fun createTodo(
        @Path("user_id") userId: String,
        @Query("apikey") apiKey: String,
        @Body request: TodoRequest
    ): Response<TodoItem>

    @PUT("/api/users/{user_id}/todos/{id}")
    suspend fun updateTodo(
        @Path("user_id") userId: String,
        @Path("id") todoId: String,
        @Query("apikey") apiKey: String,
        @Body request: TodoRequest
    ): Response<TodoItem>
}
