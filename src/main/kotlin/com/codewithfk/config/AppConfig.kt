package com.codewithfk.config

import io.github.cdimascio.dotenv.dotenv

object AppConfig {
    private val env = dotenv {
        ignoreIfMissing = true
        filename = ".env"
    }

    // JWT Configuration
    val JWT_SECRET = env["JWT_SECRET"] ?: "your-super-secret-key-change-in-production"
    val JWT_ISSUER = env["JWT_ISSUER"] ?: "music-app"
    val JWT_AUDIENCE = env["JWT_AUDIENCE"] ?: "music-app-users"
    val JWT_REALM = env["JWT_REALM"] ?: "music-app"
    val JWT_EXPIRATION = env["JWT_EXPIRATION"]?.toLong() ?: 86400000L // 24 hours

    // Database Configuration
    val DB_HOST = env["DB_HOST"] ?: "localhost"
    val DB_PORT = env["DB_PORT"]?.toInt() ?: 3306
    val DB_NAME = env["DB_NAME"] ?: "music_app"
    val DB_USER = env["DB_USER"] ?: "root"
    val DB_PASSWORD = env["DB_PASSWORD"] ?: "root"

    // Server Configuration
    val SERVER_HOST = env["SERVER_HOST"] ?: "0.0.0.0"
    val SERVER_PORT = env["SERVER_PORT"]?.toInt() ?: 8080
} 