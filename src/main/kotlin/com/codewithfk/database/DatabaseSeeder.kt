package com.codewithfk.database

import com.codewithfk.models.ArtistCreateRequest
import com.codewithfk.models.SongCreateRequest
import com.codewithfk.repository.ArtistRepository
import com.codewithfk.repository.SongRepository
import java.time.LocalDateTime
import java.time.ZoneOffset

class DatabaseSeeder(
    private val artistRepository: ArtistRepository,
    private val songRepository: SongRepository
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
} 