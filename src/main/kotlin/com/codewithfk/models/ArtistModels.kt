package com.codewithfk.models

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class ArtistCreateRequest(
    val name: String,
    val bio: String? = null,
    val profilePicture: String? = null
)

@Serializable
data class ArtistUpdateRequest(
    val name: String? = null,
    val bio: String? = null,
    val profilePicture: String? = null
)

@Serializable
data class ArtistResponse(
    val id: String,
    val name: String,
    val bio: String?,
    val profilePicture: String?,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) 