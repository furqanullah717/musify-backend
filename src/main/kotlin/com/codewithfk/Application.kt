package com.codewithfk

import com.codewithfk.config.JwtConfig
import com.codewithfk.database.DatabaseConfig
import com.codewithfk.database.DatabaseSeeder
import com.codewithfk.database.PlaylistSongs
import com.codewithfk.database.Playlists
import com.codewithfk.models.UserPrincipal
import com.codewithfk.plugins.configureCORS
import com.codewithfk.repository.ArtistRepository
import com.codewithfk.repository.HomeRepository
import com.codewithfk.repository.PlaylistRepository
import com.codewithfk.repository.SongRepository
import com.codewithfk.repository.UserRepository
import com.codewithfk.routes.artistRoutes
import com.codewithfk.routes.authRoutes
import com.codewithfk.routes.homeRoutes
import com.codewithfk.routes.playlistRoutes
import com.codewithfk.routes.songRoutes
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    // Initialize the database
    val dbConfig = DatabaseConfig()
    dbConfig.init()

    // Create tables
    transaction {
        SchemaUtils.create(
            com.codewithfk.database.Users,
            com.codewithfk.database.Artists,
            com.codewithfk.database.Songs,
            Playlists,
            PlaylistSongs,
            com.codewithfk.database.UserPreferences,
            com.codewithfk.database.UserListeningHistory
        )
    }

    // Initialize repositories
    val userRepository = UserRepository()
    val artistRepository = ArtistRepository()
    val songRepository = SongRepository(artistRepository)
    val homeRepository = HomeRepository(artistRepository=artistRepository, songRepository = songRepository)
    val playlistRepository = PlaylistRepository(songRepository)

    // Seed the database with sample data
    val seeder = DatabaseSeeder(artistRepository, songRepository, playlistRepository, userRepository)
    seeder.seed()

    // JWT Configuration
    val jwtConfig = JwtConfig()
    
    // Configure authentication
    install(Authentication) {
        jwt {
            verifier(jwtConfig.verifier)
            validate { credential ->
                if (credential.payload.audience.contains(jwtConfig.audience)) {
                    val id = credential.payload.getClaim("id").asString()
                    if (id != null) {
                        UserPrincipal(id)
                    } else {
                        null
                    }
                } else {
                    null
                }
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }
        }
    }

    configureSecurity()
    configureFrameworks()
    configureSerialization()
    configureHTTP()
    configureRouting()
    configureCORS()

    // Configure Routing
    routing {
        authRoutes(userRepository, jwtConfig)
        artistRoutes()
        songRoutes()
        homeRoutes()
        playlistRoutes(playlistRepository)
    }
} 