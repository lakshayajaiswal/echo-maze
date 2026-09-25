package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioEngine
import com.example.audio.SpeechSynthesizer
import com.example.data.local.EchoRunnerDatabase
import com.example.data.local.EchoRunnerRepository
import com.example.data.local.LeaderboardEntryEntity
import com.example.data.local.PlayerProfileEntity
import com.example.data.local.RunRecordEntity
import com.example.model.ActiveObstacle
import com.example.model.GameSettings
import com.example.model.LevelCatalog
import com.example.model.LevelDefinition
import com.example.model.ObstacleType
import com.example.sensor.HapticController
import com.example.sensor.ShakeDetector
import com.example.sensor.TiltDetector
import com.example.sensor.VoiceCommandManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

data class GameUIState(
    val isGameStarted: Boolean = false,
    val playerName: String = "Harini",
    val currentLevelKey: String = "EASY_0",
    val currentLevel: LevelDefinition = LevelCatalog.getByKey("EASY_0"),
    val speed: Float = 22f,
    val distanceRanMeters: Float = 0f,
    val remainingMeters: Float = 800f,
    val isPaused: Boolean = false,
    val isBlindfoldMode: Boolean = false,
    val lane: Int = 0,
    val currentLaneX: Float = 0f,
    val isJumping: Boolean = false,
    val jumpY: Float = 0f,
    val isSliding: Boolean = false,
    val slideTimer: Float = 0f,
    val runAnimTime: Float = 0f,
    val obstacles: List<ActiveObstacle> = emptyList(),
    val voiceBanner: String = "Say 'PAUSE' or Shake phone to pause the game!",
    val crashesCount: Int = 0,
    val dodgedCount: Int = 0,
    val showPitchDeck: Boolean = false,
    val showSettings: Boolean = false,
    val showLeaderboard: Boolean = false,
    val showVictoryDialog: Boolean = false,
    val victoryMessage: String = "",
    val settings: GameSettings = GameSettings(),
    val isVoiceListening: Boolean = false,
    val isShakeActive: Boolean = true,
    val isTiltActive: Boolean = false
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: EchoRunnerRepository
    private val audioEngine = AudioEngine()
    private val speechSynthesizer = SpeechSynthesizer(application)
    private val hapticController = HapticController(application)

    private val _uiState = MutableStateFlow(GameUIState())
    val uiState: StateFlow<GameUIState> = _uiState.asStateFlow()

    val leaderboard: StateFlow<List<LeaderboardEntryEntity>>
    val recentRuns: StateFlow<List<RunRecordEntity>>

    // Hardware sensors
    private val shakeDetector: ShakeDetector
    private val tiltDetector: TiltDetector
    private val voiceCommandManager: VoiceCommandManager

    private var jumpVY = 0f
    private var nextSpawnZ = 90f
    private var lastMilestoneMeters = 0f

    init {
        val database = EchoRunnerDatabase.getDatabase(application)
        repository = EchoRunnerRepository(database.echoRunnerDao())

        leaderboard = repository.getLeaderboard()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        recentRuns = repository.getRecentRuns()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        // Setup Shake Detector
        shakeDetector = ShakeDetector(application) {
            onShakeDetected()
        }

        // Setup Tilt Detector
        tiltDetector = TiltDetector(
            context = application,
            onRollLeft = { shiftLane(-1) },
            onRollRight = { shiftLane(1) },
            onRollCenter = { /* Keep lane */ },
            onPitchForward = { triggerSlide() },
            onPitchBack = { triggerJump() }
        )

        // Setup Voice Command Manager
        voiceCommandManager = VoiceCommandManager(application) { command ->
            handleVoiceCommand(command)
        }

        // Update audio volume from settings
        audioEngine.setVolume(_uiState.value.settings.masterVolume)
        speechSynthesizer.setSpeechRate(_uiState.value.settings.speechRate)
    }

    fun startGame(name: String) {
        val cleanName = name.trim().ifEmpty { "Harini" }
        _uiState.update {
            it.copy(
                isGameStarted = true,
                playerName = cleanName,
                isPaused = false
            )
        }

        shakeDetector.isEnabled = _uiState.value.settings.shakePauseEnabled
        shakeDetector.start()

        if (_uiState.value.settings.tiltSteeringEnabled) {
            tiltDetector.isEnabled = true
            tiltDetector.start()
        }

        if (_uiState.value.settings.voiceCommandsEnabled) {
            voiceCommandManager.startListening()
            _uiState.update { it.copy(isVoiceListening = true) }
        }

        loadLevel("EASY_0")
        speakVoice("Welcome $cleanName! Say 'PAUSE' or shake phone to pause anytime. Run!", priority = true)
    }

    fun togglePauseGame() {
        if (_uiState.value.isPaused) {
            resumeGame()
        } else {
            pauseGame()
        }
    }

    fun pauseGame() {
        if (_uiState.value.isPaused || !_uiState.value.isGameStarted) return
        _uiState.update { it.copy(isPaused = true) }
        audioEngine.playPauseTone()
        hapticController.pause()
        speakVoice("Game Paused, ${_uiState.value.playerName}! Say RESUME or shake phone to continue!", priority = true)
    }

    fun resumeGame() {
        if (!_uiState.value.isPaused) return
        _uiState.update { it.copy(isPaused = false) }
        audioEngine.playResumeTone()
        hapticController.pause()
        speakVoice("Resuming run, ${_uiState.value.playerName}! Run!", priority = true)
    }

    private fun onShakeDetected() {
        if (!_uiState.value.isGameStarted) return
        togglePauseGame()
    }

    private fun handleVoiceCommand(command: String) {
        when (command) {
            "PAUSE" -> pauseGame()
            "RESUME" -> resumeGame()
            "LEFT" -> if (!_uiState.value.isPaused) shiftLane(-1)
            "RIGHT" -> if (!_uiState.value.isPaused) shiftLane(1)
            "JUMP" -> if (!_uiState.value.isPaused) triggerJump()
            "SLIDE" -> if (!_uiState.value.isPaused) triggerSlide()
        }
    }

    fun shiftLane(dir: Int) {
        if (_uiState.value.isPaused || !_uiState.value.isGameStarted) return
        val current = _uiState.value.lane
        val targetLane = (current + dir).coerceIn(-1, 1)
        if (targetLane != current) {
            _uiState.update { it.copy(lane = targetLane) }
            audioEngine.playSpatialLanePing(targetLane)
            hapticController.laneShift()
            val laneName = when (targetLane) {
                -1 -> "Left"
                1 -> "Right"
                else -> "Center"
            }
            speakVoice("Lane $laneName")
        }
    }

    fun triggerJump() {
        if (_uiState.value.isPaused || !_uiState.value.isGameStarted) return
        if (!_uiState.value.isJumping) {
            jumpVY = 12f
            _uiState.update { it.copy(isJumping = true) }
            audioEngine.playJumpSound()
            hapticController.jump()
        }
    }

    fun triggerSlide() {
        if (_uiState.value.isPaused || !_uiState.value.isGameStarted) return
        if (!_uiState.value.isSliding) {
            _uiState.update { it.copy(isSliding = true, slideTimer = 0.75f) }
            audioEngine.playSlideSound()
            hapticController.slide()
        }
    }

    fun toggleBlindfoldMode() {
        val nextMode = !_uiState.value.isBlindfoldMode
        _uiState.update { it.copy(isBlindfoldMode = nextMode) }
        if (nextMode) {
            speakVoice("Blindfold Mode Activated for ${_uiState.value.playerName}! Listen to sound cues!", priority = true)
        } else {
            speakVoice("Spectator View Active.", priority = true)
        }
    }

    fun loadLevel(levelKey: String) {
        val level = LevelCatalog.getByKey(levelKey)
        nextSpawnZ = 90f
        lastMilestoneMeters = 0f
        jumpVY = 0f

        _uiState.update {
            it.copy(
                currentLevelKey = levelKey,
                currentLevel = level,
                speed = level.speed,
                distanceRanMeters = 0f,
                remainingMeters = level.targetMeters,
                obstacles = emptyList(),
                isJumping = false,
                jumpY = 0f,
                isSliding = false,
                slideTimer = 0f,
                lane = 0,
                currentLaneX = 0f,
                showVictoryDialog = false
            )
        }
        spawnInitialObstacles(level)
    }

    private fun spawnInitialObstacles(level: LevelDefinition) {
        val initialList = mutableListOf<ActiveObstacle>()
        var z = 70f
        for (i in 0 until 4) {
            val lane = listOf(-1, 0, 1).random()
            val type = ObstacleType.values().random()
            initialList.add(ActiveObstacle(id = Random.nextLong(), lane = lane, z = z, type = type))
            z += level.obstacleGap + Random.nextFloat() * 20f
        }
        nextSpawnZ = z
        _uiState.update { it.copy(obstacles = initialList) }
    }

    private fun spawnObstacle(level: LevelDefinition, currentList: MutableList<ActiveObstacle>) {
        val lane = listOf(-1, 0, 1).random()
        val type = ObstacleType.values().random()
        currentList.add(ActiveObstacle(id = Random.nextLong(), lane = lane, z = nextSpawnZ, type = type))
        nextSpawnZ += level.obstacleGap + Random.nextFloat() * 25f
    }

    fun onGameTick(dt: Float) {
        val state = _uiState.value
        if (!state.isGameStarted || state.isPaused) return

        val level = state.currentLevel
        val newSpeed = level.speed + (state.distanceRanMeters / 220f) * 1.0f
        val newDistanceRan = state.distanceRanMeters + newSpeed * dt
        val newRemaining = max(0f, level.targetMeters - newDistanceRan)
        val newAnimTime = state.runAnimTime + dt * 14f

        // Milestone audio notification every 300 meters
        if (newDistanceRan - lastMilestoneMeters >= 300f) {
            lastMilestoneMeters = (newDistanceRan / 300f).toInt() * 300f
            val metersRounded = newDistanceRan.toInt()
            speakVoice("$metersRounded meters completed!")
        }

        // Checkpoint completion
        if (newRemaining <= 0f) {
            onLevelVictory(level, newDistanceRan)
            return
        }

        // Smooth lane interpolation
        val targetX = state.lane.toFloat()
        val interpolatedX = state.currentLaneX + (targetX - state.currentLaneX) * (14f * dt)

        // Jump physics
        var newJumpY = state.jumpY
        var newIsJumping = state.isJumping
        if (newIsJumping) {
            newJumpY += jumpVY * dt
            jumpVY -= 35f * dt
            if (newJumpY <= 0f) {
                newJumpY = 0f
                newIsJumping = false
            }
        }

        // Slide timer
        var newSlideTimer = state.slideTimer
        var newIsSliding = state.isSliding
        if (newIsSliding) {
            newSlideTimer -= dt
            if (newSlideTimer <= 0f) {
                newIsSliding = false
            }
        }

        // Obstacle processing
        val updatedObstacles = state.obstacles.map { it.copy() }.toMutableList()
        val warningLeadZ = newSpeed * state.settings.warningLeadTimeSec

        var crashes = state.crashesCount
        var dodged = state.dodgedCount
        var penaltySpeed = newSpeed

        val iterator = updatedObstacles.iterator()
        while (iterator.hasNext()) {
            val obs = iterator.next()
            obs.z -= newSpeed * dt

            // Pre-warning audio trigger
            if (obs.z <= warningLeadZ + 12f && !obs.announced) {
                obs.announced = true
                audioEngine.playPreObstacleWarningTone(obs.lane, obs.type)
                announceObstacleSpeech(obs, level)
            }

            // Hit detection zone
            if (obs.z in -2f..3f) {
                if (abs(obs.lane - state.lane) < 0.6f) {
                    var isHit = false
                    when (obs.type) {
                        ObstacleType.LOW_BARRIER -> if (newJumpY < 0.75f) isHit = true
                        ObstacleType.HIGH_BEAM -> if (!newIsSliding) isHit = true
                        ObstacleType.WALL -> isHit = true
                    }

                    if (isHit) {
                        crashes++
                        onObstacleCrash(obs, level)
                        penaltySpeed = max(18f, penaltySpeed - 9f)
                        iterator.remove()
                        continue
                    }
                }
            }

            // Successfully passed obstacle
            if (obs.z < -6f) {
                dodged++
                iterator.remove()
            }
        }

        // Refill obstacles if low
        while (updatedObstacles.size < 5) {
            spawnObstacle(level, updatedObstacles)
        }

        _uiState.update {
            it.copy(
                speed = penaltySpeed,
                distanceRanMeters = newDistanceRan,
                remainingMeters = newRemaining,
                runAnimTime = newAnimTime,
                currentLaneX = interpolatedX,
                isJumping = newIsJumping,
                jumpY = newJumpY,
                isSliding = newIsSliding,
                slideTimer = newSlideTimer,
                obstacles = updatedObstacles,
                crashesCount = crashes,
                dodgedCount = dodged
            )
        }
    }

    private fun announceObstacleSpeech(obs: ActiveObstacle, level: LevelDefinition) {
        val laneText = when (obs.lane) {
            -1 -> "Left Lane"
            1 -> "Right Lane"
            else -> "Center Lane"
        }
        val name = _uiState.value.playerName
        if (obs.lane == _uiState.value.lane) {
            when (obs.type) {
                ObstacleType.LOW_BARRIER -> speakVoice("$name! ${level.lowObs.name} in $laneText! Jump!", priority = true)
                ObstacleType.HIGH_BEAM -> speakVoice("$name! ${level.highObs.name} in $laneText! Slide!", priority = true)
                ObstacleType.WALL -> speakVoice("$name! ${level.wallObs.name} in $laneText! Shift!", priority = true)
            }
        }
    }

    private fun onObstacleCrash(obs: ActiveObstacle, level: LevelDefinition) {
        audioEngine.playCrashSound()
        hapticController.crash()
        val obsName = when (obs.type) {
            ObstacleType.LOW_BARRIER -> level.lowObs.name
            ObstacleType.HIGH_BEAM -> level.highObs.name
            ObstacleType.WALL -> level.wallObs.name
        }
        speakVoice("${_uiState.value.playerName}! Impact with $obsName! Speed reduced!", priority = true)
    }

    private fun onLevelVictory(level: LevelDefinition, distanceRan: Float) {
        audioEngine.playVictoryFanfare()
        speakVoice("🎉 Checkpoint Reached! ${_uiState.value.playerName}, ${level.name} Completed!", priority = true)

        viewModelScope.launch {
            // Persist run in Room database
            repository.recordRun(
                RunRecordEntity(
                    playerName = _uiState.value.playerName,
                    levelKey = level.key,
                    levelName = level.name,
                    distanceMeters = distanceRan,
                    targetMeters = level.targetMeters,
                    obstaclesDodged = _uiState.value.dodgedCount,
                    crashes = _uiState.value.crashesCount,
                    completed = true
                )
            )
            repository.updatePlayerLeaderboardScore(
                playerName = _uiState.value.playerName,
                distanceKm = distanceRan / 1000f,
                levelName = level.name
            )
        }

        val allLevels = LevelCatalog.LEVELS
        val currentIndex = allLevels.indexOfFirst { it.key == level.key }
        val nextLevel = if (currentIndex in 0 until allLevels.size - 1) {
            allLevels[currentIndex + 1]
        } else {
            allLevels.first()
        }

        _uiState.update {
            it.copy(
                isPaused = true,
                showVictoryDialog = true,
                victoryMessage = "Checkpoint Reached: ${level.name} Complete!\nNext: ${nextLevel.name}"
            )
        }
    }

    fun advanceToNextLevel() {
        val allLevels = LevelCatalog.LEVELS
        val currentIndex = allLevels.indexOfFirst { it.key == _uiState.value.currentLevelKey }
        val nextKey = if (currentIndex in 0 until allLevels.size - 1) {
            allLevels[currentIndex + 1].key
        } else {
            allLevels.first().key
        }
        loadLevel(nextKey)
        resumeGame()
    }

    fun calibrateTilt() {
        tiltDetector.calibrateZeroTilt()
        speakVoice("${_uiState.value.playerName}, zero tilt calibrated.")
    }

    fun resetCurrentLevel() {
        loadLevel(_uiState.value.currentLevelKey)
        speakVoice("${_uiState.value.playerName}, level restarted!")
    }

    fun updateSettings(newSettings: GameSettings) {
        _uiState.update { it.copy(settings = newSettings) }
        audioEngine.setVolume(newSettings.masterVolume)
        speechSynthesizer.setSpeechRate(newSettings.speechRate)

        shakeDetector.isEnabled = newSettings.shakePauseEnabled
        tiltDetector.isEnabled = newSettings.tiltSteeringEnabled
        if (newSettings.tiltSteeringEnabled) {
            tiltDetector.start()
        } else {
            tiltDetector.stop()
        }

        if (newSettings.voiceCommandsEnabled && !_uiState.value.isVoiceListening) {
            voiceCommandManager.startListening()
            _uiState.update { it.copy(isVoiceListening = true) }
        } else if (!newSettings.voiceCommandsEnabled && _uiState.value.isVoiceListening) {
            voiceCommandManager.stopListening()
            _uiState.update { it.copy(isVoiceListening = false) }
        }
    }

    fun speakVoice(text: String, priority: Boolean = false) {
        _uiState.update { it.copy(voiceBanner = "🗣️ \"$text\"") }
        speechSynthesizer.speak(text, priority)
    }

    fun openPitchDeck() = _uiState.update { it.copy(showPitchDeck = true) }
    fun closePitchDeck() = _uiState.update { it.copy(showPitchDeck = false) }

    fun openSettings() = _uiState.update { it.copy(showSettings = true) }
    fun closeSettings() = _uiState.update { it.copy(showSettings = false) }

    fun openLeaderboard() = _uiState.update { it.copy(showLeaderboard = true) }
    fun closeLeaderboard() = _uiState.update { it.copy(showLeaderboard = false) }

    fun dismissVictoryDialog() = _uiState.update { it.copy(showVictoryDialog = false) }

    override fun onCleared() {
        super.onCleared()
        shakeDetector.stop()
        tiltDetector.stop()
        voiceCommandManager.stopListening()
        speechSynthesizer.shutdown()
    }
}
