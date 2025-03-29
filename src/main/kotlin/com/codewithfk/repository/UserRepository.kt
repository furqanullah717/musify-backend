package com.codewithfk.repository

import com.codewithfk.database.Users
import com.codewithfk.models.User
import com.codewithfk.models.UserResponse
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

class UserRepository {
    private fun User.toResponse() = UserResponse(
        id = id,
        email = email,
        name = name,
        profilePicture = profilePicture,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    fun createUser(email: String, password: String, name: String): UserResponse = transaction {
        val id = UUID.randomUUID().toString()
        val now = LocalDateTime.now()
        
        Users.insert {
            it[Users.id] = id
            it[Users.email] = email
            it[Users.password] = password
            it[Users.name] = name
            it[Users.createdAt] = now
            it[Users.updatedAt] = now
        }

        Users.select { Users.id eq id }
            .map { row ->
                User(
                    id = row[Users.id],
                    email = row[Users.email],
                    password = row[Users.password],
                    name = row[Users.name],
                    profilePicture = row[Users.profilePicture],
                    createdAt = row[Users.createdAt].toEpochSecond(ZoneOffset.UTC) * 1000,
                    updatedAt = row[Users.updatedAt].toEpochSecond(ZoneOffset.UTC) * 1000
                )
            }
            .first()
            .toResponse()
    }

    fun findUserByEmail(email: String): User? = transaction {
        Users.select { Users.email eq email }
            .map { row ->
                User(
                    id = row[Users.id],
                    email = row[Users.email],
                    password = row[Users.password],
                    name = row[Users.name],
                    profilePicture = row[Users.profilePicture],
                    createdAt = row[Users.createdAt].toEpochSecond(ZoneOffset.UTC) * 1000,
                    updatedAt = row[Users.updatedAt].toEpochSecond(ZoneOffset.UTC) * 1000
                )
            }
            .firstOrNull()
    }

    fun findUserById(id: String): User? = transaction {
        Users.select { Users.id eq id }
            .map { row ->
                User(
                    id = row[Users.id],
                    email = row[Users.email],
                    password = row[Users.password],
                    name = row[Users.name],
                    profilePicture = row[Users.profilePicture],
                    createdAt = row[Users.createdAt].toEpochSecond(ZoneOffset.UTC) * 1000,
                    updatedAt = row[Users.updatedAt].toEpochSecond(ZoneOffset.UTC) * 1000
                )
            }
            .firstOrNull()
    }

    fun updateUser(id: String, name: String? = null, profilePicture: String? = null): UserResponse? = transaction {
        val now = LocalDateTime.now()
        
        Users.update({ Users.id eq id }) {
            if (name != null) it[Users.name] = name
            if (profilePicture != null) it[Users.profilePicture] = profilePicture
            it[Users.updatedAt] = now
        }

        findUserById(id)?.toResponse()
    }
} 