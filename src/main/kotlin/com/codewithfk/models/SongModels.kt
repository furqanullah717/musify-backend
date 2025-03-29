package com.codewithfk.models

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class SongCreateRequest(
    val title: String,
    val artistId: String,
    val duration: Int,
    val audioUrl: String,
    val coverImage: String? = null,
    val genre: String? = null,
    val releaseDate: Long? = null // Epoch milliseconds
)

@Serializable
data class SongUpdateRequest(
    val title: String? = null,
    val duration: Int? = null,
    val audioUrl: String? = null,
    val coverImage: String? = null,
    val genre: String? = null,
    val releaseDate: Long? = null // Epoch milliseconds
)

@Serializable
data class SongResponse(
    val id: String,
    val title: String,
    val artist: ArtistResponse,
    val duration: Int,
    val audioUrl: String,
    val coverImage: String?,
    val genre: String?,
    val releaseDate: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) 