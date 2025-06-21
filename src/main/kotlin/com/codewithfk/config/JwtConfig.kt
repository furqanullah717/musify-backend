package com.codewithfk.config

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.JWTVerifier
import io.ktor.server.auth.jwt.*
import java.util.*

class JwtConfig {
    private val secret = AppConfig.JWT_SECRET
    private val validityInMs = AppConfig.JWT_EXPIRATION
    val issuer = AppConfig.JWT_ISSUER
    val audience = AppConfig.JWT_AUDIENCE
    val realm = AppConfig.JWT_REALM
    
    val verifier: JWTVerifier = JWT
        .require(Algorithm.HMAC256(secret))
        .withAudience(audience)
        .withIssuer(issuer)
        .build()
    
    fun makeToken(id: String, email: String): String = JWT.create()
        .withAudience(audience)
        .withIssuer(issuer)
        .withClaim("id", id)
        .withClaim("email", email)
        .withExpiresAt(Date(System.currentTimeMillis() + validityInMs))
        .sign(Algorithm.HMAC256(secret))
} 