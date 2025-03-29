package com.codewithfk.config

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.JWTVerifier
import io.ktor.server.auth.jwt.*
import java.util.*

object JwtConfig {
    fun makeJwtVerifier(): JWTVerifier = JWT
        .require(Algorithm.HMAC256(AppConfig.JWT_SECRET))
        .withAudience(AppConfig.JWT_AUDIENCE)
        .withIssuer(AppConfig.JWT_ISSUER)
        .build()

    fun makeJwtToken(userId: String): String = JWT.create()
        .withAudience(AppConfig.JWT_AUDIENCE)
        .withIssuer(AppConfig.JWT_ISSUER)
        .withClaim("userId", userId)
        .withExpiresAt(Date(System.currentTimeMillis() + AppConfig.JWT_EXPIRATION))
        .sign(Algorithm.HMAC256(AppConfig.JWT_SECRET))
} 