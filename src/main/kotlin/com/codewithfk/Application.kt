package com.codewithfk

import com.codewithfk.config.AppConfig
import com.codewithfk.config.JwtConfig
import com.codewithfk.database.DatabaseConfig
import com.codewithfk.plugins.configureCORS
import com.codewithfk.routes.artistRoutes
import com.codewithfk.routes.authRoutes
import com.codewithfk.routes.songRoutes
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.routing.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    // Initialize Database
    DatabaseConfig.init()

    // Configure JWT Authentication
    install(Authentication) {
        jwt {
            verifier(JwtConfig.makeJwtVerifier())
            validate { credential ->
                if (credential.payload.audience.contains(AppConfig.JWT_AUDIENCE)) {
                    JWTPrincipal(credential.payload)
                } else null
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
        authRoutes()
        artistRoutes()
        songRoutes()
    }
} 