package com.codewithfk.database

import com.codewithfk.models.ArtistCreateRequest
import com.codewithfk.models.SongCreateRequest
import com.codewithfk.repository.ArtistRepository
import com.codewithfk.repository.PlaylistRepository
import com.codewithfk.repository.SongRepository
import com.codewithfk.repository.UserRepository
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.ZoneOffset

class DatabaseSeeder(
    private val artistRepository: ArtistRepository,
    private val songRepository: SongRepository,
    private val playlistRepository: PlaylistRepository? = null,
    private val userRepository: UserRepository? = null
) {
    private val localAudioUrl = "https://esljzyhhpgljrbwzxydw.supabase.co/storage/v1/object/public/food_hub_bucket//Egzod%20-%20Royalty.mp3"
    private val localCoverImage = "https://i.ibb.co/5WhKj22D/Rectangle-19.png"

    fun seed() {
        if (artistRepository.getAllArtists(1, 0).isEmpty()) {
            seedArtists()
        }
        if (songRepository.getAllSongs(1, 0).isEmpty()) {
            seedSongs()
        }
        
        // Only seed playlists if repository and users are available
        if (playlistRepository != null && userRepository != null) {
            seedPlaylists()
        }
    }

    private fun seedArtists() {
        val dummyArtists = listOf(
            ArtistCreateRequest(
                name = "Ed Sheeran",
                bio = "English singer-songwriter known for his soulful voice and acoustic guitar",
                profilePicture = "https://i.ibb.co/6cDp4swt/ed.png"
            ),
            ArtistCreateRequest(
                name = "Taylor Swift",
                bio = "American singer-songwriter known for her narrative songwriting",
                profilePicture = "https://i.ibb.co/zHjMH3XW/tylor.png"
            ),
            ArtistCreateRequest(
                name = "Arijit Singh",
                bio = "Indian playback singer known for his soulful voice",
                profilePicture = "https://i.ibb.co/Kpb97yfm/arjit.png"
            ),
            ArtistCreateRequest(
                name = "Ariana Grande",
                bio = "American singer known for her wide vocal range",
                profilePicture = "https://i.ibb.co/pvLw2xVS/ariana.png"
            ),
            ArtistCreateRequest(
                name = "Atif Aslam",
                bio = "Pakistani singer known for his powerful vocals",
                profilePicture = "https://i.ibb.co/XrVn17hY/atif.png"
            )
        )
        dummyArtists.forEach { artistRepository.createArtist(it.name, it.bio, it.profilePicture) }
    }

    private fun seedSongs() {
        val artists = artistRepository.getAllArtists(10, 0)
        if (artists.isNotEmpty()) {
            val dummySongs = listOf(
                SongCreateRequest(
                    title = "Shape of You",
                    artistId = artists[0].id,
                    duration = 235,
                    audioUrl = localAudioUrl,
                    coverImage = "https://i.ibb.co/DH1PYNdj/album.png",
                    genre = "Pop",
                    releaseDate = LocalDateTime.now().minusYears(6).toEpochSecond(ZoneOffset.UTC) * 1000
                ),
                SongCreateRequest(
                    title = "Anti-Hero",
                    artistId = artists[1].id,
                    duration = 200,
                    audioUrl = localAudioUrl,
                    coverImage = localCoverImage,
                    genre = "Pop",
                    releaseDate = LocalDateTime.now().minusYears(1).toEpochSecond(ZoneOffset.UTC) * 1000
                ),
                SongCreateRequest(
                    title = "Tum Hi Ho",
                    artistId = artists[2].id,
                    duration = 198,
                    audioUrl = localAudioUrl,
                    coverImage = "https://i.ibb.co/DH1PYNdj/album.png",
                    genre = "Bollywood",
                    releaseDate = LocalDateTime.now().minusYears(5).toEpochSecond(ZoneOffset.UTC) * 1000
                ),
                SongCreateRequest(
                    title = "7 rings",
                    artistId = artists[3].id,
                    duration = 178,
                    audioUrl = localAudioUrl,
                    coverImage = localCoverImage,
                    genre = "Pop",
                    releaseDate = LocalDateTime.now().minusYears(4).toEpochSecond(ZoneOffset.UTC) * 1000
                ),
                SongCreateRequest(
                    title = "Tere Sang Yaara",
                    artistId = artists[4].id,
                    duration = 200,
                    audioUrl = localAudioUrl,
                    coverImage = "https://i.ibb.co/DH1PYNdj/album.png",
                    genre = "Bollywood",
                    releaseDate = LocalDateTime.now().minusYears(3).toEpochSecond(ZoneOffset.UTC) * 1000
                )
            )
            dummySongs.forEach { 
                songRepository.createSong(
                    it.title,
                    it.artistId,
                    it.duration,
                    it.audioUrl,
                    it.coverImage,
                    it.genre,
                    LocalDateTime.ofEpochSecond(it.releaseDate!! / 1000, 0, ZoneOffset.UTC)
                )
            }
        }
    }
    
    private fun seedPlaylists() {
        // Get a user to associate playlists with
        val user = transaction {
            Users.select { Users.email eq "test@example.com" }.firstOrNull()?.let {
                it[Users.id]
            }
        } ?: return // If no user exists, skip playlist seeding
        
        // Get all songs for creating playlists
        val songs = songRepository.getAllSongs(10, 0)
        if (songs.isEmpty()) return
        
        // Check if we already have playlists for this user
        val existingPlaylistCount = transaction {
            Playlists.select { Playlists.userId eq user }.count()
        }
        
        if (existingPlaylistCount > 0) return
        
        // Create sample playlists
        val popSongs = songs.filter { it.genre == "Pop" }.map { it.id }
        val bollywoodSongs = songs.filter { it.genre == "Bollywood" }.map { it.id }
        
        // Pop Hits playlist
        if (popSongs.isNotEmpty()) {
            playlistRepository!!.createPlaylist(
                name = "Pop Hits",
                description = "A collection of popular pop songs",
                coverImage = "https://i.ibb.co/5WhKj22D/Rectangle-19.png",
                userId = user,
                songIds = popSongs
            )
        }
        
        // Bollywood playlist
        if (bollywoodSongs.isNotEmpty()) {
            playlistRepository!!.createPlaylist(
                name = "Bollywood Classics",
                description = "Best Bollywood songs of all time",
                coverImage = "https://i.ibb.co/DH1PYNdj/album.png",
                userId = user,
                songIds = bollywoodSongs
            )
        }
        
        // Mixed playlist with all songs
        if (songs.isNotEmpty()) {
            playlistRepository!!.createPlaylist(
                name = "My Favorites",
                description = "A mix of all my favorite songs",
                coverImage = null, // No cover image
                userId = user,
                songIds = songs.map { it.id }
            )
        }
    }
} 