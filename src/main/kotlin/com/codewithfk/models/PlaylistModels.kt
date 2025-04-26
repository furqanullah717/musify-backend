package com.codewithfk.models

import kotlinx.serialization.Serializable

@Serializable
data class PlaylistCreateRequest(
    val name: String,
    val description: String? = null,
    val coverImage: String? = null,
    val songIds: List<String> = emptyList()
)

@Serializable
data class PlaylistUpdateRequest(
    val name: String? = null,
    val description: String? = null,
    val coverImage: String? = null
)

@Serializable
data class PlaylistAddSongsRequest(
    val songIds: List<String>
)

@Serializable
data class PlaylistRemoveSongsRequest(
    val songIds: List<String>
)

@Serializable
data class PlaylistResponse(
    val id: String,
    val name: String,
    val description: String?,
    val coverImage: String?,
    val userId: String,
    val songs: List<SongResponse> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Serializable
data class PlaylistBriefResponse(
    val id: String,
    val name: String,
    val description: String?,
    val coverImage: String?,
    val userId: String,
    val songCount: Int,
    val createdAt: Long,
    val updatedAt: Long
) 