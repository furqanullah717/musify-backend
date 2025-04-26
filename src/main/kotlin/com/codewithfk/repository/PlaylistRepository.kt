package com.codewithfk.repository

import com.codewithfk.database.Artists
import com.codewithfk.database.PlaylistSongs
import com.codewithfk.database.Playlists
import com.codewithfk.database.Songs
import com.codewithfk.models.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

class PlaylistRepository(private val songRepository: SongRepository) {
    
    fun getPlaylistsByUser(userId: String, limit: Int, offset: Int): List<PlaylistBriefResponse> = transaction {
        Playlists.select { Playlists.userId eq userId }
            .limit(limit, offset.toLong())
            .map { row ->
                val playlistId = row[Playlists.id]
                val songCount = PlaylistSongs
                    .select { PlaylistSongs.playlistId eq playlistId }
                    .count()
                    
                PlaylistBriefResponse(
                    id = playlistId,
                    name = row[Playlists.name],
                    description = row[Playlists.description],
                    coverImage = row[Playlists.coverImage],
                    userId = row[Playlists.userId],
                    songCount = songCount.toInt(),
                    createdAt = row[Playlists.createdAt].toEpochSecond(ZoneOffset.UTC) * 1000,
                    updatedAt = row[Playlists.updatedAt].toEpochSecond(ZoneOffset.UTC) * 1000
                )
            }
    }
    
    fun getPlaylistById(id: String, userId: String): PlaylistResponse? = transaction {
        val playlistRow = Playlists
            .select { (Playlists.id eq id) and (Playlists.userId eq userId) }
            .singleOrNull() ?: return@transaction null
        
        // Get all songs in this playlist
        val songs = (PlaylistSongs innerJoin Songs innerJoin Artists)
            .select { PlaylistSongs.playlistId eq id }
            .orderBy(PlaylistSongs.addedAt)
            .map { row ->
                SongResponse(
                    id = row[Songs.id],
                    title = row[Songs.title],
                    artist = ArtistResponse(
                        id = row[Artists.id],
                        name = row[Artists.name],
                        bio = row[Artists.bio],
                        profilePicture = row[Artists.profilePicture],
                        createdAt = row[Artists.createdAt].toEpochSecond(ZoneOffset.UTC) * 1000,
                        updatedAt = row[Artists.updatedAt].toEpochSecond(ZoneOffset.UTC) * 1000
                    ),
                    duration = row[Songs.duration],
                    audioUrl = row[Songs.audioUrl],
                    coverImage = row[Songs.coverImage],
                    genre = row[Songs.genre],
                    releaseDate = row[Songs.releaseDate]?.toEpochSecond(ZoneOffset.UTC)?.times(1000) ?: 0,
                    createdAt = row[Songs.createdAt].toEpochSecond(ZoneOffset.UTC) * 1000,
                    updatedAt = row[Songs.updatedAt].toEpochSecond(ZoneOffset.UTC) * 1000
                )
            }
        
        PlaylistResponse(
            id = playlistRow[Playlists.id],
            name = playlistRow[Playlists.name],
            description = playlistRow[Playlists.description],
            coverImage = playlistRow[Playlists.coverImage],
            userId = playlistRow[Playlists.userId],
            songs = songs,
            createdAt = playlistRow[Playlists.createdAt].toEpochSecond(ZoneOffset.UTC) * 1000,
            updatedAt = playlistRow[Playlists.updatedAt].toEpochSecond(ZoneOffset.UTC) * 1000
        )
    }
    
    fun createPlaylist(
        name: String,
        description: String?,
        coverImage: String?,
        userId: String,
        songIds: List<String> = emptyList()
    ): PlaylistResponse = transaction {
        val id = UUID.randomUUID().toString()
        val now = LocalDateTime.now()
        
        Playlists.insert {
            it[Playlists.id] = id
            it[Playlists.name] = name
            it[Playlists.description] = description
            it[Playlists.coverImage] = coverImage
            it[Playlists.userId] = userId
            it[createdAt] = now
            it[updatedAt] = now
        }
        
        val validSongIds = if (songIds.isNotEmpty()) {
            Songs.select { Songs.id inList songIds }
                .map { it[Songs.id] }
        } else {
            emptyList()
        }
        
        // Add songs to playlist
        validSongIds.forEach { songId ->
            PlaylistSongs.insert {
                it[PlaylistSongs.id] = UUID.randomUUID().toString()
                it[playlistId] = id
                it[PlaylistSongs.songId] = songId
                it[addedAt] = now
            }
        }
        
        // Get songs for response
        val songs = if (validSongIds.isEmpty()) {
            emptyList()
        } else {
            validSongIds.mapNotNull { songId ->
                songRepository.findSongById(songId)
            }
        }
        
        PlaylistResponse(
            id = id,
            name = name,
            description = description,
            coverImage = coverImage,
            userId = userId,
            songs = songs,
            createdAt = now.toEpochSecond(ZoneOffset.UTC) * 1000,
            updatedAt = now.toEpochSecond(ZoneOffset.UTC) * 1000
        )
    }
    
    fun updatePlaylist(
        id: String,
        userId: String,
        name: String?,
        description: String?,
        coverImage: String?
    ): PlaylistResponse? = transaction {
        val exists = Playlists
            .select { (Playlists.id eq id) and (Playlists.userId eq userId) }
            .singleOrNull() ?: return@transaction null
        
        val now = LocalDateTime.now()

        Playlists.update({ (Playlists.id eq id) and (Playlists.userId eq userId) }) { stmt ->
            name?.let { stmt[Playlists.name] = it }
            description?.let { stmt[Playlists.description] = it }
            coverImage?.let { stmt[Playlists.coverImage] = it }
            stmt[updatedAt] = now
        }
        
        getPlaylistById(id, userId)
    }
    
    fun deletePlaylist(id: String, userId: String): Boolean = transaction {
        val exists = Playlists
            .select { (Playlists.id eq id) and (Playlists.userId eq userId) }
            .count() > 0
        
        if (!exists) return@transaction false
        
        // First delete all playlist songs
        PlaylistSongs.deleteWhere { playlistId eq id }
        
        // Then delete the playlist
        Playlists.deleteWhere { (Playlists.id eq id) and (Playlists.userId eq userId) } > 0
    }
    
    fun addSongsToPlaylist(
        playlistId: String,
        userId: String,
        songIds: List<String>
    ): Int = transaction {
        val exists = Playlists
            .select { (Playlists.id eq playlistId) and (Playlists.userId eq userId) }
            .count() > 0
        
        if (!exists) return@transaction 0
        
        val now = LocalDateTime.now()
        
        // Get list of songs that already exist in the playlist
        val existingSongIds = PlaylistSongs
            .select { PlaylistSongs.playlistId eq playlistId }
            .map { it[PlaylistSongs.songId] }
        
        // Add only songs that don't already exist in the playlist
        val songsToAdd = songIds.filter { it !in existingSongIds }
        
        // Verify all songs exist before adding them
        val validSongIds = Songs
            .select { Songs.id inList songsToAdd }
            .map { it[Songs.id] }
        
        validSongIds.forEach { songId ->
            PlaylistSongs.insert {
                it[id] = UUID.randomUUID().toString()
                it[PlaylistSongs.playlistId] = playlistId
                it[PlaylistSongs.songId] = songId
                it[addedAt] = now
            }
        }
        
        // Update the playlist's updated_at timestamp
        Playlists.update({ Playlists.id eq playlistId }) {
            it[updatedAt] = now
        }
        
        validSongIds.size
    }
    
    fun removeSongsFromPlaylist(
        playlistId: String,
        userId: String,
        songIds: List<String>
    ): Int = transaction {
        val exists = Playlists
            .select { (Playlists.id eq playlistId) and (Playlists.userId eq userId) }
            .count() > 0
        
        if (!exists) return@transaction 0
        
        // Remove songs
        val removed = PlaylistSongs.deleteWhere { 
            (PlaylistSongs.playlistId eq playlistId) and (PlaylistSongs.songId inList songIds)
        }
        
        // Update the playlist's updated_at timestamp
        Playlists.update({ Playlists.id eq playlistId }) {
            it[updatedAt] = LocalDateTime.now()
        }
        
        removed
    }
} 