package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_profiles")
data class PlayerProfileEntity(
    @PrimaryKey val playerName: String,
    val totalDistanceMeters: Float = 0f,
    val maxSingleRunMeters: Float = 0f,
    val highestLevelKey: String = "EASY_0",
    val completedRuns: Int = 0,
    val lastPlayedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "run_records")
data class RunRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val playerName: String,
    val levelKey: String,
    val levelName: String,
    val distanceMeters: Float,
    val targetMeters: Float,
    val obstaclesDodged: Int,
    val crashes: Int,
    val completed: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "leaderboard_entries")
data class LeaderboardEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val rank: Int,
    val name: String,
    val distanceKm: Float,
    val levelName: String,
    val isUser: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
