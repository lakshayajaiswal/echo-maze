package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EchoRunnerDao {

    @Query("SELECT * FROM player_profiles WHERE playerName = :name LIMIT 1")
    fun getProfile(name: String): Flow<PlayerProfileEntity?>

    @Query("SELECT * FROM player_profiles ORDER BY totalDistanceMeters DESC")
    fun getAllProfiles(): Flow<List<PlayerProfileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: PlayerProfileEntity)

    @Update
    suspend fun updateProfile(profile: PlayerProfileEntity)

    @Query("SELECT * FROM run_records ORDER BY timestamp DESC LIMIT 50")
    fun getRecentRuns(): Flow<List<RunRecordEntity>>

    @Query("SELECT * FROM run_records WHERE playerName = :name ORDER BY timestamp DESC")
    fun getRunsForPlayer(name: String): Flow<List<RunRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRunRecord(run: RunRecordEntity): Long

    @Query("SELECT * FROM leaderboard_entries ORDER BY distanceKm DESC LIMIT 25")
    fun getLeaderboard(): Flow<List<LeaderboardEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeaderboardEntry(entry: LeaderboardEntryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeaderboardEntries(entries: List<LeaderboardEntryEntity>)

    @Query("DELETE FROM leaderboard_entries WHERE isUser = 1")
    suspend fun clearUserLeaderboardEntries()
}
