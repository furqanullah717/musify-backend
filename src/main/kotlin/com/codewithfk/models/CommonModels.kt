package com.codewithfk.models

import kotlinx.serialization.Serializable

@Serializable
data class PaginatedResponse<T>(
    val data: List<T>,
    val total: Int,
    val page: Int,
    val pageSize: Int,
    val totalPages: Int
)

@Serializable
data class ErrorResponse(
    val message: String,
    val code: Int? = null,
    val details: Map<String, String>? = null
) 