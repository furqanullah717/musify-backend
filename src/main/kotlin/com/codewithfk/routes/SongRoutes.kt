package com.codewithfk.routes

import com.codewithfk.models.SongCreateRequest
import com.codewithfk.models.SongResponse
import com.codewithfk.models.SongUpdateRequest
import com.codewithfk.repository.ArtistRepository
import com.codewithfk.repository.SongRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.LocalDateTime
import java.util.*

fun Route.songRoutes() {
    val artistRepository = ArtistRepository()
    val repository = SongRepository(artistRepository)

    route("/songs") {
        get {
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 20
            val offset = call.request.queryParameters["offset"]?.toIntOrNull() ?: 0
            val songs = repository.getAllSongs(limit, offset)
            call.respond(songs)
        }

        post {
            val request = call.receive<SongCreateRequest>()
            val song = repository.createSong(
                request.title,
                request.artistId,
                request.duration,
                request.audioUrl,
                request.coverImage,
                request.genre,
                request.releaseDate?.let { LocalDateTime.ofEpochSecond(it, 0, null) }
            )
            call.respond(HttpStatusCode.Created, song)
        }

        get("/{id}") {
            val id = call.parameters["id"]
            val song = repository.findSongById(id.toString())
            if (song != null) {
                call.respond(song)
            } else {
                call.respond(HttpStatusCode.NotFound, "Song not found")
            }
        }

        put("/{id}") {
            val id = call.parameters["id"]

            val request = call.receive<SongUpdateRequest>()
            val song = repository.updateSong(
                id.toString(),
                request.title,
                request.duration,
                request.audioUrl,
                request.coverImage,
                request.genre,
                request.releaseDate?.let { LocalDateTime.ofEpochSecond(it, 0, null) }
            )
            if (song != null) {
                call.respond(song)
            } else {
                call.respond(HttpStatusCode.NotFound, "Song not found")
            }
        }

        delete("/{id}") {
            val id = call.parameters["id"].toString()
            val deleted = repository.deleteSong(id)
            if (deleted) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound, "Song not found")
            }
        }

        get("/artist/{artistId}") {
            val artistId = call.parameters["artistId"].toString()
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 20
            val offset = call.request.queryParameters["offset"]?.toIntOrNull() ?: 0
            val songs = repository.getSongsByArtist(artistId, limit, offset)
            call.respond(songs)
        }
    }
} 