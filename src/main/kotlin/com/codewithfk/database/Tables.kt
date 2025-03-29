package com.codewithfk.database

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object Users : Table() {
    val id: Column<String> = varchar("id", 36)
    val email: Column<String> = varchar("email", 255)
    val password: Column<String> = varchar("password", 255)
    val name: Column<String> = varchar("name", 255)
    val profilePicture: Column<String?> = varchar("profile_picture", 255).nullable()
    val createdAt: Column<LocalDateTime> = datetime("created_at")
    val updatedAt: Column<LocalDateTime> = datetime("updated_at")

    override val primaryKey = PrimaryKey(id)
    init {
        uniqueIndex("unique_email", email)
    }
}

object Artists : Table() {
    val id: Column<String> = varchar("id", 36)
    val name: Column<String> = varchar("name", 255)
    val bio: Column<String?> = text("bio").nullable()
    val profilePicture: Column<String?> = varchar("profile_picture", 255).nullable()
    val createdAt: Column<LocalDateTime> = datetime("created_at")
    val updatedAt: Column<LocalDateTime> = datetime("updated_at")

    override val primaryKey = PrimaryKey(id)
}

object Songs : Table() {
    val id: Column<String> = varchar("id", 36)
    val title: Column<String> = varchar("title", 255)
    val artistId: Column<String> = varchar("artist_id", 36)
    val duration: Column<Int> = integer("duration") // Duration in seconds
    val audioUrl: Column<String> = varchar("audio_url", 255)
    val coverImage: Column<String?> = varchar("cover_image", 255).nullable()
    val genre: Column<String?> = varchar("genre", 100).nullable()
    val releaseDate: Column<LocalDateTime?> = datetime("release_date").nullable()
    val createdAt: Column<LocalDateTime> = datetime("created_at")
    val updatedAt: Column<LocalDateTime> = datetime("updated_at")

    override val primaryKey = PrimaryKey(id)
    init {
        foreignKey(artistId to Artists.id)
    }
} 