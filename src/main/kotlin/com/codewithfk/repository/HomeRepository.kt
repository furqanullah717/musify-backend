package com.codewithfk.repository

import com.codewithfk.models.AlbumResponse
import com.codewithfk.models.SongResponse
import com.codewithfk.database.Songs
import com.codewithfk.database.Artists
import com.codewithfk.models.ArtistResponse
import com.codewithfk.models.HomeResponse
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

class HomeRepository(
    private val songRepository: SongRepository,
    private val artistRepository: ArtistRepository
) {
    fun getHomeData(): HomeResponse = transaction {
        // Get continue listening (recently played songs)
        val continueListening = getContinueListening()

        // Get top mixes (albums)
        val topMixes = getTopMixes()

        // Get recommended songs based on recent listening
        val recommendedSongs = getRecommendedSongs()

        HomeResponse(
            continueListening = continueListening,
            topMixes = topMixes,
            recommendedSongs = recommendedSongs
        )
    }

    private fun getContinueListening(): List<SongResponse> = transaction {
        // For now, return the most recent songs
        Songs.selectAll()
            .orderBy(Songs.createdAt, SortOrder.DESC)
            .limit(5)
            .map { row ->
                val artist = Artists.select { Artists.id eq row[Songs.artistId] }
                    .map { artistRow ->
                        ArtistResponse(
                            id = artistRow[Artists.id].toString(),
                            name = artistRow[Artists.name],
                            bio = artistRow[Artists.bio],
                            profilePicture = artistRow[Artists.profilePicture],
                            createdAt = artistRow[Artists.createdAt].toEpochSecond(ZoneOffset.UTC) * 1000,
                            updatedAt = artistRow[Artists.updatedAt].toEpochSecond(ZoneOffset.UTC) * 1000
                        )
                    }
                    .first()

                SongResponse(
                    id = row[Songs.id].toString(),
                    title = row[Songs.title],
                    artist = artist,
                    duration = row[Songs.duration],
                    audioUrl = row[Songs.audioUrl],
                    coverImage = row[Songs.coverImage],
                    genre = row[Songs.genre],
                    releaseDate = row[Songs.releaseDate]!!.toEpochSecond(ZoneOffset.UTC).times(1000),
                    createdAt = row[Songs.createdAt].toEpochSecond(ZoneOffset.UTC) * 1000,
                    updatedAt = row[Songs.updatedAt].toEpochSecond(ZoneOffset.UTC) * 1000
                )
            }
    }

    private fun getTopMixes(): List<AlbumResponse> = transaction {
        // For now, create albums based on artists
        Artists.selectAll()
            .limit(5)
            .map { artistRow ->
                val artist = ArtistResponse(
                    id = artistRow[Artists.id].toString(),
                    name = artistRow[Artists.name],
                    bio = artistRow[Artists.bio],
                    profilePicture = artistRow[Artists.profilePicture],
                    createdAt = artistRow[Artists.createdAt].toEpochSecond(ZoneOffset.UTC) * 1000,
                    updatedAt = artistRow[Artists.updatedAt].toEpochSecond(ZoneOffset.UTC) * 1000
                )

                val songs = Songs.select { Songs.artistId eq artistRow[Artists.id] }
                    .map { songRow ->
                        SongResponse(
                            id = songRow[Songs.id].toString(),
                            title = songRow[Songs.title],
                            artist = artist,
                            duration = songRow[Songs.duration],
                            audioUrl = songRow[Songs.audioUrl],
                            coverImage = songRow[Songs.coverImage],
                            genre = songRow[Songs.genre],
                            releaseDate = songRow[Songs.releaseDate]!!.toEpochSecond(ZoneOffset.UTC).times(1000),
                            createdAt = songRow[Songs.createdAt].toEpochSecond(ZoneOffset.UTC) * 1000,
                            updatedAt = songRow[Songs.updatedAt].toEpochSecond(ZoneOffset.UTC) * 1000
                        )
                    }

                AlbumResponse(
                    id = UUID.randomUUID().toString(),
                    title = "${artist.name}'s Greatest Hits",
                    artist = artist,
                    coverImage = "https://ibb.co/mC5NySSs",
                    songs = songs,
                    genre = songs.firstOrNull()?.genre ?: "Pop",
                    releaseDate = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) * 1000
                )
            }
    }

    private fun getRecommendedSongs(): List<SongResponse> = transaction {
        // For now, return songs from different genres
        Songs.selectAll()
            .orderBy(Songs.createdAt, SortOrder.DESC)
            .limit(10)
            .map { row ->
                val artist = Artists.select { Artists.id eq row[Songs.artistId] }
                    .map { artistRow ->
                        ArtistResponse(
                            id = artistRow[Artists.id].toString(),
                            name = artistRow[Artists.name],
                            bio = artistRow[Artists.bio],
                            profilePicture = artistRow[Artists.profilePicture],
                            createdAt = artistRow[Artists.createdAt].toEpochSecond(ZoneOffset.UTC) * 1000,
                            updatedAt = artistRow[Artists.updatedAt].toEpochSecond(ZoneOffset.UTC) * 1000
                        )
                    }
                    .first()

                SongResponse(
                    id = row[Songs.id].toString(),
                    title = row[Songs.title],
                    artist = artist,
                    duration = row[Songs.duration],
                    audioUrl = row[Songs.audioUrl],
                    coverImage = row[Songs.coverImage],
                    genre = row[Songs.genre],
                    releaseDate = row[Songs.releaseDate]!!.toEpochSecond(ZoneOffset.UTC).times(1000),
                    createdAt = row[Songs.createdAt].toEpochSecond(ZoneOffset.UTC) * 1000,
                    updatedAt = row[Songs.updatedAt].toEpochSecond(ZoneOffset.UTC) * 1000
                )
            }
    }
} 