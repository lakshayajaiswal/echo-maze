package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.BlindfoldOverlay
import com.example.ui.components.CyberRunnerCanvas
import com.example.ui.components.GameFooter
import com.example.ui.components.GameHeader
import com.example.ui.components.GameHudOverlay
import com.example.ui.components.LeaderboardSheet
import com.example.ui.components.PauseOverlay
import com.example.ui.components.PitchDeckModal
import com.example.ui.components.SensorStatusBar
import com.example.ui.components.SettingsDialog
import com.example.ui.components.StartLoginDialog
import com.example.ui.components.VictoryCheckpointDialog
import com.example.ui.components.VoiceBanner
import com.example.ui.theme.CyberBg
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberGold
import com.example.ui.theme.CyberMagenta
import com.example.ui.theme.CyberPanel
import com.example.ui.theme.CyberPanelBorder
import com.example.ui.theme.CyberTextMain
import com.example.ui.theme.CyberTextMuted
import com.example.viewmodel.GameViewModel

@Composable
fun EchoRunnerScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val leaderboard by viewModel.leaderboard.collectAsStateWithLifecycle()
    val recentRuns by viewModel.recentRuns.collectAsStateWithLifecycle()

    // 60 FPS Game Loop
    LaunchedEffect(state.isGameStarted, state.isPaused) {
        if (!state.isGameStarted || state.isPaused) return@LaunchedEffect

        var lastNanos = 0L
        while (true) {
            withFrameNanos { nowNanos ->
                if (lastNanos != 0L) {
                    val dt = ((nowNanos - lastNanos) / 1_000_000_000f).coerceIn(0.001f, 0.05f)
                    viewModel.onGameTick(dt)
                }
                lastNanos = nowNanos
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize().background(CyberBg),
        topBar = {
            GameHeader(
                state = state,
                onTogglePause = { viewModel.togglePauseGame() },
                onToggleBlindfold = { viewModel.toggleBlindfoldMode() },
                onOpenPitch = { viewModel.openPitchDeck() },
                onOpenLeaderboard = { viewModel.openLeaderboard() },
                onOpenSettings = { viewModel.openSettings() }
            )
        },
        bottomBar = {
            if (!state.isBlindfoldMode) {
                GameFooter(
                    currentLevel = state.currentLevel,
                    onSelectLevel = { viewModel.loadLevel(it) },
                    onCalibrate = { viewModel.calibrateTilt() },
                    onRestart = { viewModel.resetCurrentLevel() }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(if (state.isBlindfoldMode) Color.Black else CyberBg)
        ) {
            // Main 3D Canvas
            CyberRunnerCanvas(
                currentLaneX = state.currentLaneX,
                isJumping = state.isJumping,
                jumpY = state.jumpY,
                isSliding = state.isSliding,
                runAnimTime = state.runAnimTime,
                obstacles = state.obstacles,
                currentLevel = state.currentLevel,
                isBlindfoldMode = state.isBlindfoldMode,
                onSwipeLeft = { viewModel.shiftLane(-1) },
                onSwipeRight = { viewModel.shiftLane(1) },
                onSwipeUp = { viewModel.triggerJump() },
                onSwipeDown = { viewModel.triggerSlide() },
                modifier = Modifier.fillMaxSize()
            )

            // Spectator HUD Elements (only visible when not in pure blindfold mode)
            if (!state.isBlindfoldMode) {
                // Top HUD: Player name, Sub-level, Checkpoint
                GameHudOverlay(
                    state = state,
                    modifier = Modifier.align(Alignment.TopCenter)
                )

                // Spoken Voice Announcement Banner
                VoiceBanner(
                    text = state.voiceBanner,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 58.dp)
                )

                // Mini Judges Leaderboard floating card (top-right under HUD)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 100.dp, end = 12.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(CyberPanel.copy(alpha = 0.90f))
                        .border(1.dp, CyberPanelBorder, RoundedCornerShape(10.dp))
                        .clickable { viewModel.openLeaderboard() }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .testTag("judges_mini_hud")
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "JUDGES LEADERBOARD",
                            color = CyberTextMuted,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "1. ${state.playerName} (You)",
                                color = CyberCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            val kmRan = state.distanceRanMeters / 1000f
                            Text(
                                text = String.format("%.2f km", kmRan),
                                color = CyberGold,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("2. Judge Alex", color = CyberTextMuted, fontSize = 9.sp)
                            Text("1.40 km", color = CyberTextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("3. Judge Maya", color = CyberTextMuted, fontSize = 9.sp)
                            Text("0.95 km", color = CyberTextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                }

                // Sensor Status Indicators (bottom center)
                SensorStatusBar(
                    shakeActive = state.settings.shakePauseEnabled,
                    voiceActive = state.isVoiceListening,
                    tiltActive = state.settings.tiltSteeringEnabled,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp)
                )
            }

            // Pause Overlay Modal
            AnimatedVisibility(
                visible = state.isPaused && !state.showVictoryDialog,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                PauseOverlay(
                    isVoiceListening = state.isVoiceListening,
                    lastVoiceTranscript = state.lastVoiceTranscript,
                    onResume = { viewModel.resumeGame() }
                )
            }

            // Blindfold Accessible Touch Quad Overlay
            if (state.isBlindfoldMode) {
                BlindfoldOverlay(
                    onExitBlindfold = { viewModel.toggleBlindfoldMode() },
                    onShiftLeft = { viewModel.shiftLane(-1) },
                    onShiftRight = { viewModel.shiftLane(1) },
                    onJump = { viewModel.triggerJump() },
                    onSlide = { viewModel.triggerSlide() }
                )
            }

            // Start & Voice Login Dialog
            if (!state.isGameStarted) {
                StartLoginDialog(
                    initialName = state.playerName,
                    onStartGame = { name -> viewModel.startGame(name) }
                )
            }

            // Hackathon Pitch Deck Dialog
            if (state.showPitchDeck) {
                PitchDeckModal(
                    onDismiss = { viewModel.closePitchDeck() }
                )
            }

            // Leaderboard & Runs Dialog
            if (state.showLeaderboard) {
                LeaderboardSheet(
                    leaderboardEntries = leaderboard,
                    recentRuns = recentRuns,
                    currentPlayerName = state.playerName,
                    onDismiss = { viewModel.closeLeaderboard() }
                )
            }

            // Settings & Calibration Dialog
            if (state.showSettings) {
                SettingsDialog(
                    currentSettings = state.settings,
                    onSaveSettings = { viewModel.updateSettings(it) },
                    onCalibrateTilt = { viewModel.calibrateTilt() },
                    onDismiss = { viewModel.closeSettings() }
                )
            }

            // Victory Checkpoint Dialog
            if (state.showVictoryDialog) {
                VictoryCheckpointDialog(
                    message = state.victoryMessage,
                    onNextLevel = { viewModel.advanceToNextLevel() },
                    onDismiss = { viewModel.dismissVictoryDialog() }
                )
            }
        }
    }
}
