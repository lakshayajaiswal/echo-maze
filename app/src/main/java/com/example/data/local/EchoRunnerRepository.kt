package com.example.data.local

import kotlinx.coroutines.flow.Flow

class EchoRunnerRepository(private val dao: EchoRunnerDao) {

    fun getProfile(playerName: String): Flow<PlayerProfileEntity?> {
        return dao.getProfile(playerName)
    }

    fun getAllProfiles(): Flow<List<PlayerProfileEntity>> {
        return dao.getAllProfiles()
    }

    suspend fun saveProfile(profile: PlayerProfileEntity) {
        dao.insertProfile(profile)
    }

    fun getRecentRuns(): Flow<List<RunRecordEntity>> {
        return dao.getRecentRuns()
    }

    fun getRunsForPlayer(playerName: String): Flow<List<RunRecordEntity>> {
        return dao.getRunsForPlayer(playerName)
    }

    suspend fun recordRun(run: RunRecordEntity): Long {
        return dao.insertRunRecord(run)
    }

    fun getLeaderboard(): Flow<List<LeaderboardEntryEntity>> {
        return dao.getLeaderboard()
    }

    suspend fun updatePlayerLeaderboardScore(playerName: String, distanceKm: Float, levelName: String) {
        dao.clearUserLeaderboardEntries()
        dao.insertLeaderboardEntry(
            LeaderboardEntryEntity(
                rank = 1,
                name = playerName,
                distanceKm = distanceKm,
                levelName = levelName,
                isUser = true
            )
        )
    }
}
