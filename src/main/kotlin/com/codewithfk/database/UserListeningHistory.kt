package com.codewithfk.database

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object UserListeningHistory : UUIDTable() {
    val userId = varchar("user_id", 36)
    val songId = varchar("song_id", 36)
    val lastPlayed = datetime("last_played").default(LocalDateTime.now())
    val playCount = integer("play_count").default(1)
} 