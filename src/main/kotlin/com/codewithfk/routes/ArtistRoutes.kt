package com.codewithfk.routes

import com.codewithfk.models.ArtistCreateRequest
import com.codewithfk.models.ArtistResponse
import com.codewithfk.models.ArtistUpdateRequest
import com.codewithfk.repository.ArtistRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.artistRoutes() {
    val repository = ArtistRepository()

    route("/artists") {
        get {
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 20
            val offset = call.request.queryParameters["offset"]?.toIntOrNull() ?: 0
            val artists = repository.getAllArtists(limit, offset)
            call.respond(artists)
        }

        post {
            val request = call.receive<ArtistCreateRequest>()
            val artist = repository.createArtist(request.name, request.bio, request.profilePicture)
            call.respond(HttpStatusCode.Created, artist)
        }

        get("/{id}") {
            val id = call.parameters["id"]
            val artist = repository.findArtistById(id.toString())
            if (artist != null) {
                call.respond(artist)
            } else {
                call.respond(HttpStatusCode.NotFound, "Artist not found")
            }
        }

        put("/{id}") {
            val id = call.parameters["id"]
            val request = call.receive<ArtistUpdateRequest>()
            val artist = repository.updateArtist(id.toString(), request.name, request.bio, request.profilePicture)
            if (artist != null) {
                call.respond(artist)
            } else {
                call.respond(HttpStatusCode.NotFound, "Artist not found")
            }
        }

        delete("/{id}") {
            val id = call.parameters["id"]
            val deleted = repository.deleteArtist(id.toString())
            if (deleted) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound, "Artist not found")
            }
        }
    }
} 