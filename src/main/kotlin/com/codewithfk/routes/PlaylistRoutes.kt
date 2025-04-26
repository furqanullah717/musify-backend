package com.codewithfk.routes

import com.codewithfk.models.*
import com.codewithfk.repository.PlaylistRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.LocalDateTime
import java.util.*

fun Route.playlistRoutes(playlistRepository: PlaylistRepository) {
    authenticate {
        // Get all playlists for the authenticated user
        get("/playlists") {
            val userId = call.principal<UserPrincipal>()?.id
                ?: return@get call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Unauthorized"))

            val limit = call.parameters["limit"]?.toIntOrNull() ?: 20
            val offset = call.parameters["offset"]?.toIntOrNull() ?: 0
            
            val playlists = playlistRepository.getPlaylistsByUser(userId, limit, offset)
            call.respond(playlists)
        }

        // Get a specific playlist by ID with all its songs
        get("/playlists/{id}") {
            val userId = call.principal<UserPrincipal>()?.id
                ?: return@get call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Unauthorized"))
            
            val playlistId = call.parameters["id"]
                ?: return@get call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing playlist ID"))

            val playlist = playlistRepository.getPlaylistById(playlistId, userId)
                ?: return@get call.respond(
                    HttpStatusCode.NotFound,
                    ErrorResponse("Playlist not found or you don't have access to it")
                )

            call.respond(playlist)
        }

        // Create a new playlist
        post("/playlists") {
            val userId = call.principal<UserPrincipal>()?.id
                ?: return@post call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Unauthorized"))
            
            val request = call.receive<PlaylistCreateRequest>()
            
            val playlist = playlistRepository.createPlaylist(
                name = request.name,
                description = request.description,
                coverImage = request.coverImage,
                userId = userId,
                songIds = request.songIds
            )
            
            call.respond(HttpStatusCode.Created, playlist)
        }

        // Update a playlist
        put("/playlists/{id}") {
            val userId = call.principal<UserPrincipal>()?.id
                ?: return@put call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Unauthorized"))
            
            val playlistId = call.parameters["id"]
                ?: return@put call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing playlist ID"))
            
            val request = call.receive<PlaylistUpdateRequest>()
            
            val updatedPlaylist = playlistRepository.updatePlaylist(
                id = playlistId,
                userId = userId,
                name = request.name,
                description = request.description,
                coverImage = request.coverImage
            ) ?: return@put call.respond(
                HttpStatusCode.NotFound,
                ErrorResponse("Playlist not found or you don't have access to it")
            )
            
            call.respond(updatedPlaylist)
        }

        // Delete a playlist
        delete("/playlists/{id}") {
            val userId = call.principal<UserPrincipal>()?.id
                ?: return@delete call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Unauthorized"))
            
            val playlistId = call.parameters["id"]
                ?: return@delete call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing playlist ID"))
            
            val deleted = playlistRepository.deletePlaylist(playlistId, userId)
            
            if (deleted) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(
                    HttpStatusCode.NotFound,
                    ErrorResponse("Playlist not found or you don't have access to it")
                )
            }
        }

        // Add songs to a playlist
        post("/playlists/{id}/songs") {
            val userId = call.principal<UserPrincipal>()?.id
                ?: return@post call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Unauthorized"))
            
            val playlistId = call.parameters["id"]
                ?: return@post call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing playlist ID"))
            
            val request = call.receive<PlaylistAddSongsRequest>()
            
            if (request.songIds.isEmpty()) {
                return@post call.respond(HttpStatusCode.BadRequest, ErrorResponse("No songs provided"))
            }
            
            val songsAdded = playlistRepository.addSongsToPlaylist(playlistId, userId, request.songIds)
            
            if (songsAdded >= 0) {
                call.respond(HttpStatusCode.OK, mapOf("songsAdded" to songsAdded))
            } else {
                call.respond(
                    HttpStatusCode.NotFound,
                    ErrorResponse("Playlist not found or you don't have access to it")
                )
            }
        }

        // Remove songs from a playlist
        delete("/playlists/{id}/songs") {
            val userId = call.principal<UserPrincipal>()?.id
                ?: return@delete call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Unauthorized"))
            
            val playlistId = call.parameters["id"]
                ?: return@delete call.respond(HttpStatusCode.BadRequest, ErrorResponse("Missing playlist ID"))
            
            val request = call.receive<PlaylistRemoveSongsRequest>()
            
            if (request.songIds.isEmpty()) {
                return@delete call.respond(HttpStatusCode.BadRequest, ErrorResponse("No songs provided"))
            }
            
            val songsRemoved = playlistRepository.removeSongsFromPlaylist(playlistId, userId, request.songIds)
            
            if (songsRemoved >= 0) {
                call.respond(HttpStatusCode.OK, mapOf("songsRemoved" to songsRemoved))
            } else {
                call.respond(
                    HttpStatusCode.NotFound,
                    ErrorResponse("Playlist not found or you don't have access to it")
                )
            }
        }
    }
}

private fun LocalDateTime.toEpochMilli(): Long {
    return this.toEpochSecond(java.time.ZoneOffset.UTC) * 1000
} 