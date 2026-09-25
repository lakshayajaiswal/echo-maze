package com.example.model

enum class ObstacleType(val displayName: String) {
    LOW_BARRIER("Low Barrier (Jump)"),
    HIGH_BEAM("High Beam (Slide)"),
    WALL("Barrier Wall (Shift)")
}

data class ObstacleDefinition(
    val name: String,
    val colorHex: Long
)

data class LevelDefinition(
    val key: String,
    val tier: String,
    val name: String,
    val targetMeters: Float,
    val speed: Float,
    val obstacleGap: Float,
    val lowObs: ObstacleDefinition,
    val highObs: ObstacleDefinition,
    val wallObs: ObstacleDefinition
)

data class ActiveObstacle(
    val id: Long,
    val lane: Int, // -1: Left, 0: Center, 1: Right
    var z: Float,
    val type: ObstacleType,
    var announced: Boolean = false
)

data class GameSettings(
    val warningLeadTimeSec: Float = 0.85f,
    val speechRate: Float = 1.35f,
    val masterVolume: Float = 0.8f,
    val tiltSteeringEnabled: Boolean = false,
    val shakePauseEnabled: Boolean = true,
    val voiceCommandsEnabled: Boolean = true
)

object LevelCatalog {
    val LEVELS = listOf(
        // Easy Tier
        LevelDefinition(
            key = "EASY_0",
            tier = "EASY",
            name = "Easy 1: Neon Boulevard",
            targetMeters = 800f,
            speed = 22f,
            obstacleGap = 60f,
            lowObs = ObstacleDefinition("Traffic Cone", 0xFFFFAA00),
            highObs = ObstacleDefinition("Neon Curb", 0xFF00F3FF),
            wallObs = ObstacleDefinition("Subway Barrier", 0xFFFF0055)
        ),
        LevelDefinition(
            key = "EASY_1",
            tier = "EASY",
            name = "Easy 2: Quiet Tracks",
            targetMeters = 1000f,
            speed = 25f,
            obstacleGap = 55f,
            lowObs = ObstacleDefinition("Steam Pipe", 0xFFE2E8F0),
            highObs = ObstacleDefinition("Signal Beam", 0xFFFBBF24),
            wallObs = ObstacleDefinition("Rail Fence", 0xFFFF0055)
        ),
        LevelDefinition(
            key = "EASY_2",
            tier = "EASY",
            name = "Easy 3: Sunset Express",
            targetMeters = 1200f,
            speed = 28f,
            obstacleGap = 50f,
            lowObs = ObstacleDefinition("Solar Panel", 0xFF10B981),
            highObs = ObstacleDefinition("Wind Arch", 0xFF00F3FF),
            wallObs = ObstacleDefinition("Security Gate", 0xFFFF0055)
        ),
        // Medium Tier
        LevelDefinition(
            key = "MEDIUM_0",
            tier = "MEDIUM",
            name = "Medium 1: Cyber Highway",
            targetMeters = 1400f,
            speed = 34f,
            obstacleGap = 45f,
            lowObs = ObstacleDefinition("Laser Tripwire", 0xFFFF0055),
            highObs = ObstacleDefinition("Plasma Beam", 0xFFFBBF24),
            wallObs = ObstacleDefinition("Cyber Wall", 0xFF00F3FF)
        ),
        LevelDefinition(
            key = "MEDIUM_1",
            tier = "MEDIUM",
            name = "Medium 2: Pulsing Tunnel",
            targetMeters = 1600f,
            speed = 38f,
            obstacleGap = 40f,
            lowObs = ObstacleDefinition("Electric Spike", 0xFFFBBF24),
            highObs = ObstacleDefinition("Hover Drone", 0xFF00F3FF),
            wallObs = ObstacleDefinition("Forcefield Gate", 0xFFFF0055)
        ),
        LevelDefinition(
            key = "MEDIUM_2",
            tier = "MEDIUM",
            name = "Medium 3: High Voltage",
            targetMeters = 1800f,
            speed = 42f,
            obstacleGap = 36f,
            lowObs = ObstacleDefinition("Voltage Coil", 0xFFFBBF24),
            highObs = ObstacleDefinition("Overhead Wire", 0xFF00F3FF),
            wallObs = ObstacleDefinition("Transformer Block", 0xFFFF0055)
        ),
        // Hard Tier
        LevelDefinition(
            key = "HARD_0",
            tier = "HARD",
            name = "Hard 1: Danger Zone",
            targetMeters = 2000f,
            speed = 48f,
            obstacleGap = 30f,
            lowObs = ObstacleDefinition("Sonic Blast Ring", 0xFFFF0055),
            highObs = ObstacleDefinition("Lava Beam", 0xFFFFAA00),
            wallObs = ObstacleDefinition("Rift Block", 0xFF00F3FF)
        ),
        LevelDefinition(
            key = "HARD_1",
            tier = "HARD",
            name = "Hard 2: Shadow Abyss",
            targetMeters = 2500f,
            speed = 54f,
            obstacleGap = 26f,
            lowObs = ObstacleDefinition("Shadow Spike", 0xFFFF0055),
            highObs = ObstacleDefinition("Void Portal Arch", 0xFFFBBF24),
            wallObs = ObstacleDefinition("Abyss Wall", 0xFF00F3FF)
        ),
        LevelDefinition(
            key = "HARD_2",
            tier = "HARD",
            name = "Hard 3: Velocity Collapse",
            targetMeters = 3000f,
            speed = 60f,
            obstacleGap = 22f,
            lowObs = ObstacleDefinition("Nanite Trap", 0xFFFF0055),
            highObs = ObstacleDefinition("Singularity Beam", 0xFFFBBF24),
            wallObs = ObstacleDefinition("Quantum Barrier", 0xFF00F3FF)
        )
    )

    fun getByKey(key: String): LevelDefinition {
        return LEVELS.find { it.key == key } ?: LEVELS.first()
    }
}
