package com.codewithfk.repository

import com.codewithfk.database.Songs
import com.codewithfk.models.SongResponse
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
class SongRepository(private val artistRepository: ArtistRepository) {
    fun getAllSongs(limit: Int, offset: Int): List<SongResponse> = transaction {
        Songs.selectAll()
            .limit(limit, offset.toLong())
            .map { row ->
                val artist = artistRepository.findArtistById(row[Songs.artistId])
                    ?: throw IllegalStateException("Artist not found for song ${row[Songs.id]}")
                
                SongResponse(
                    id = row[Songs.id],
                    title = row[Songs.title],
                    artist = artist,
                    duration = row[Songs.duration],
                    audioUrl = row[Songs.audioUrl],
                    coverImage = row[Songs.coverImage],
                    genre = row[Songs.genre],
                    releaseDate = row[Songs.releaseDate]?.toEpochSecond(ZoneOffset.UTC)?.times(1000) ?: 0,
                    createdAt = row[Songs.createdAt].toEpochSecond(ZoneOffset.UTC) * 1000,
                    updatedAt = row[Songs.updatedAt].toEpochSecond(ZoneOffset.UTC) * 1000
                )
            }
    }

    fun findSongById(id: String): SongResponse? = transaction {
        Songs.select { Songs.id eq id }
            .firstOrNull()
            ?.let { row ->
                val artist = artistRepository.findArtistById(row[Songs.artistId])
                    ?: throw IllegalStateException("Artist not found for song ${row[Songs.id]}")
                
                SongResponse(
                    id = row[Songs.id],
                    title = row[Songs.title],
                    artist = artist,
                    duration = row[Songs.duration],
                    audioUrl = row[Songs.audioUrl],
                    coverImage = row[Songs.coverImage],
                    genre = row[Songs.genre],
                    releaseDate = row[Songs.releaseDate]?.toEpochSecond(ZoneOffset.UTC)?.times(1000) ?: 0,
                    createdAt = row[Songs.createdAt].toEpochSecond(ZoneOffset.UTC) * 1000,
                    updatedAt = row[Songs.updatedAt].toEpochSecond(ZoneOffset.UTC) * 1000
                )
            }
    }

    fun getSongsByArtist(artistId: String, limit: Int, offset: Int): List<SongResponse> = transaction {
        Songs.select { Songs.artistId eq artistId }
            .limit(limit, offset.toLong())
            .map { row ->
                val artist = artistRepository.findArtistById(row[Songs.artistId])
                    ?: throw IllegalStateException("Artist not found for song ${row[Songs.id]}")
                
                SongResponse(
                    id = row[Songs.id],
                    title = row[Songs.title],
                    artist = artist,
                    duration = row[Songs.duration],
                    audioUrl = row[Songs.audioUrl],
                    coverImage = row[Songs.coverImage],
                    genre = row[Songs.genre],
                    releaseDate = row[Songs.releaseDate]?.toEpochSecond(ZoneOffset.UTC)?.times(1000) ?: 0,
                    createdAt = row[Songs.createdAt].toEpochSecond(ZoneOffset.UTC) * 1000,
                    updatedAt = row[Songs.updatedAt].toEpochSecond(ZoneOffset.UTC) * 1000
                )
            }
    }

    fun createSong(
        title: String,
        artistId: String,
        duration: Int,
        audioUrl: String,
        coverImage: String?,
        genre: String?,
        releaseDate: LocalDateTime?
    ): SongResponse = transaction {
        // Verify artist exists
        val artist = artistRepository.findArtistById(artistId)
            ?: throw IllegalArgumentException("Artist not found with ID: $artistId")

        val id = UUID.randomUUID().toString()
        val now = LocalDateTime.now()

        Songs.insert {
            it[Songs.id] = id
            it[Songs.title] = title
            it[Songs.artistId] = artistId
            it[Songs.duration] = duration
            it[Songs.audioUrl] = audioUrl
            it[Songs.coverImage] = coverImage
            it[Songs.genre] = genre
            it[Songs.releaseDate] = releaseDate
            it[Songs.createdAt] = now
            it[Songs.updatedAt] = now
        }

        SongResponse(
            id = id,
            title = title,
            artist = artist,
            duration = duration,
            audioUrl = audioUrl,
            coverImage = coverImage,
            genre = genre,
            releaseDate = releaseDate?.toEpochSecond(ZoneOffset.UTC)?.times(1000) ?: 0,
            createdAt = now.toEpochSecond(ZoneOffset.UTC) * 1000,
            updatedAt = now.toEpochSecond(ZoneOffset.UTC) * 1000
        )
    }

    fun updateSong(
        id: String,
        title: String?,
        duration: Int?,
        audioUrl: String?,
        coverImage: String?,
        genre: String?,
        releaseDate: LocalDateTime?
    ): SongResponse? = transaction {
        val now = LocalDateTime.now()
        val updateCount = Songs.update({ Songs.id eq id }) {table->
            title?.let { table[Songs.title] = it }
            duration?.let { table[Songs.duration] = it }
            audioUrl?.let { table[Songs.audioUrl] = it }
            coverImage?.let { table[Songs.coverImage] = it }
            genre?.let { table[Songs.genre] = it }
            releaseDate?.let { table[Songs.releaseDate] = it }
            table[Songs.updatedAt] = now
        }

        if (updateCount > 0) {
            findSongById(id)
        } else {
            null
        }
    }

    fun deleteSong(id: String): Boolean = transaction {
        Songs.deleteWhere { Songs.id eq id } > 0
    }
} 