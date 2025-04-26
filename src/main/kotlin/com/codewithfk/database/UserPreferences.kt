package com.codewithfk.database

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object UserPreferences : UUIDTable() {
    val userId = varchar("user_id", 36)
    val preferredGenre = varchar("preferred_genre", 50)
    val createdAt = datetime("created_at").default(LocalDateTime.now())
    val updatedAt = datetime("updated_at").default(LocalDateTime.now())
} 