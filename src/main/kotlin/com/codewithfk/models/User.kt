package com.codewithfk.models

import io.ktor.server.auth.*
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val email: String,
    val password: String,
    val name: String,
    val profilePicture: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Serializable
data class UserResponse(
    val id: String,
    val email: String,
    val name: String,
    val profilePicture: String? = null,
    val createdAt: Long,
    val updatedAt: Long
)

@Serializable
data class UserCreateRequest(
    val email: String,
    val password: String,
    val name: String
)

@Serializable
data class UserLoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class AuthResponse(
    val token: String,
    val user: UserResponse
)

class UserPrincipal(val id: String) : Principal 