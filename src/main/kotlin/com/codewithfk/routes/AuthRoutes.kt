package com.codewithfk.routes

import com.codewithfk.config.JwtConfig
import com.codewithfk.models.*
import com.codewithfk.repository.UserRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.mindrot.jbcrypt.BCrypt

fun Route.authRoutes(userRepository: UserRepository, jwtConfig: JwtConfig) {
    route("/auth") {
        post("/register") {
            val request = call.receive<UserCreateRequest>()

            // Check if user already exists
            val existingUser = userRepository.findUserByEmail(request.email)
            if (existingUser != null) {
                call.respond(
                    HttpStatusCode.Conflict,
                    ErrorResponse("User with this email already exists")
                )
                return@post
            }

            // Hash password and create user
            val hashedPassword = BCrypt.hashpw(request.password, BCrypt.gensalt())
            val user = userRepository.createUser(
                email = request.email,
                password = hashedPassword,
                name = request.name
            )

            val token = jwtConfig.makeToken(user.id, user.email)
            call.respond(HttpStatusCode.Created, AuthResponse(token, user))
        }

        post("/login") {
            val request = call.receive<UserLoginRequest>()

            // Find user by email
            val user = userRepository.findUserByEmail(request.email)
            if (user == null) {
                call.respond(
                    HttpStatusCode.Unauthorized,
                    ErrorResponse("Invalid email or password")
                )
                return@post
            }

            // Verify password
            if (!BCrypt.checkpw(request.password, user.password)) {
                call.respond(
                    HttpStatusCode.Unauthorized,
                    ErrorResponse("Invalid email or password")
                )
                return@post
            }

            val token = jwtConfig.makeToken(user.id, user.email)
            call.respond(
                HttpStatusCode.OK, AuthResponse(
                    token, UserResponse(
                        id = user.id,
                        email = user.email,
                        name = user.name,
                        profilePicture = user.profilePicture,
                        createdAt = user.createdAt,
                        updatedAt = user.updatedAt
                    )
                )
            )
        }
    }
}