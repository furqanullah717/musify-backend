package com.codewithfk.routes

import com.codewithfk.models.HomeResponse
import com.codewithfk.repository.ArtistRepository
import com.codewithfk.repository.HomeRepository
import com.codewithfk.repository.SongRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.homeRoutes() {
    val artistRepository = ArtistRepository()
    val songRepository = SongRepository(artistRepository)
    val repository = HomeRepository(songRepository, artistRepository)

    route("/home") {
        get {
            val homeData = repository.getHomeData()
            call.respond(homeData)
        }
    }
} 