package com.codewithfk.repository

import com.codewithfk.database.Artists
import com.codewithfk.models.ArtistResponse
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

class ArtistRepository {
    fun getAllArtists(limit: Int, offset: Int): List<ArtistResponse> = transaction {
        Artists.selectAll()
            .limit(limit, offset.toLong())
            .map { row ->
                ArtistResponse(
                    id = row[Artists.id],
                    name = row[Artists.name],
                    bio = row[Artists.bio],
                    profilePicture = row[Artists.profilePicture],
                    createdAt = row[Artists.createdAt].toEpochSecond(ZoneOffset.UTC) * 1000,
                    updatedAt = row[Artists.updatedAt].toEpochSecond(ZoneOffset.UTC) * 1000
                )
            }
    }

    fun findArtistById(id: String): ArtistResponse? = transaction {
        Artists.select { Artists.id eq id }
            .firstOrNull()
            ?.let { row ->
                ArtistResponse(
                    id = row[Artists.id],
                    name = row[Artists.name],
                    bio = row[Artists.bio],
                    profilePicture = row[Artists.profilePicture],
                    createdAt = row[Artists.createdAt].toEpochSecond(ZoneOffset.UTC) * 1000,
                    updatedAt = row[Artists.updatedAt].toEpochSecond(ZoneOffset.UTC) * 1000
                )
            }
    }

    fun createArtist(
        name: String,
        bio: String?,
        profilePicture: String?
    ): ArtistResponse = transaction {
        val id = UUID.randomUUID().toString()
        val now = LocalDateTime.now()

        Artists.insert {
            it[Artists.id] = id
            it[Artists.name] = name
            it[Artists.bio] = bio
            it[Artists.profilePicture] = profilePicture
            it[Artists.createdAt] = now
            it[Artists.updatedAt] = now
        }

        ArtistResponse(
            id = id,
            name = name,
            bio = bio,
            profilePicture = profilePicture,
            createdAt = now.toEpochSecond(ZoneOffset.UTC) * 1000,
            updatedAt = now.toEpochSecond(ZoneOffset.UTC) * 1000
        )
    }

    fun updateArtist(
        id: String,
        name: String?,
        bio: String?,
        profilePicture: String?
    ): ArtistResponse? = transaction {
        val now = LocalDateTime.now()
        
        Artists.update({ Artists.id eq id }) { table ->
            name?.let { table[Artists.name] = it }
            bio?.let { table[Artists.bio] = it }
            profilePicture?.let { table[Artists.profilePicture] = it }
            table[Artists.updatedAt] = now
        }

        findArtistById(id)
    }

    fun deleteArtist(id: String): Boolean = transaction {
        Artists.deleteWhere { Artists.id eq id } > 0
    }
} 