package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        PlayerProfileEntity::class,
        RunRecordEntity::class,
        LeaderboardEntryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class EchoRunnerDatabase : RoomDatabase() {
    abstract fun echoRunnerDao(): EchoRunnerDao

    companion object {
        @Volatile
        private var INSTANCE: EchoRunnerDatabase? = null

        fun getDatabase(context: Context): EchoRunnerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EchoRunnerDatabase::class.java,
                    "echorunner_database"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Pre-populate simulated Judges & Hackathon leaderboards
                            CoroutineScope(Dispatchers.IO).launch {
                                val dao = getDatabase(context).echoRunnerDao()
                                dao.insertLeaderboardEntries(
                                    listOf(
                                        LeaderboardEntryEntity(rank = 1, name = "Judge Alex", distanceKm = 1.40f, levelName = "Medium 1: Cyber Highway"),
                                        LeaderboardEntryEntity(rank = 2, name = "Judge Maya", distanceKm = 0.95f, levelName = "Easy 3: Sunset Express"),
                                        LeaderboardEntryEntity(rank = 3, name = "Harini", distanceKm = 0.80f, levelName = "Easy 1: Neon Boulevard", isUser = true),
                                        LeaderboardEntryEntity(rank = 4, name = "Judge Liam", distanceKm = 0.72f, levelName = "Easy 2: Quiet Tracks"),
                                        LeaderboardEntryEntity(rank = 5, name = "CyberSpectator", distanceKm = 0.50f, levelName = "Easy 1: Neon Boulevard")
                                    )
                                )
                                dao.insertProfile(
                                    PlayerProfileEntity(
                                        playerName = "Harini",
                                        totalDistanceMeters = 800f,
                                        maxSingleRunMeters = 800f,
                                        highestLevelKey = "EASY_0",
                                        completedRuns = 1
                                    )
                                )
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
