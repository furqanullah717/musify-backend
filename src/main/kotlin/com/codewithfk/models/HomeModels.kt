package com.codewithfk.models

import kotlinx.serialization.Serializable

@Serializable
data class HomeResponse(
    val continueListening: List<SongResponse>,
    val topMixes: List<AlbumResponse>,
    val recommendedSongs: List<SongResponse>
)

@Serializable
data class AlbumResponse(
    val id: String,
    val title: String,
    val artist: ArtistResponse,
    val coverImage: String,
    val songs: List<SongResponse>,
    val genre: String,
    val releaseDate: Long
) 