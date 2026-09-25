package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LevelCatalog
import com.example.ui.theme.CyberBg
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberGold
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.CyberMagenta
import com.example.ui.theme.CyberPanel
import com.example.ui.theme.CyberPanelBorder
import com.example.ui.theme.CyberTextMain
import com.example.ui.theme.CyberTextMuted
import com.example.viewmodel.GameUIState

@Composable
fun GameHeader(
    state: GameUIState,
    onTogglePause: () -> Unit,
    onToggleBlindfold: () -> Unit,
    onOpenPitch: () -> Unit,
    onOpenLeaderboard: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(CyberPanel.copy(alpha = 0.95f))
            .border(width = 1.dp, color = CyberPanelBorder)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Logo & Title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(CyberMagenta)
                    .border(2.dp, CyberCyan, CircleShape)
            )

            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "EchoRunner",
                        color = CyberCyan,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyberMagenta.copy(alpha = 0.2f))
                            .border(1.dp, CyberMagenta.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "VOICE & SHAKE PAUSE",
                            color = CyberMagenta,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Action Controls
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pause / Resume Button
            Box(
                modifier = Modifier
                    .testTag("header_pause_button")
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (state.isPaused) CyberGreen else CyberCyan)
                    .clickable { onTogglePause() }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (state.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = if (state.isPaused) "Resume" else "Pause",
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (state.isPaused) "Resume" else "Pause",
                        color = Color.Black,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Pitch Deck Button
            IconButton(
                onClick = onOpenPitch,
                modifier = Modifier.size(34.dp).testTag("pitch_deck_button")
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "Pitch Deck",
                    tint = CyberGold,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Blindfold / Spectator Toggle Button
            IconButton(
                onClick = onToggleBlindfold,
                modifier = Modifier.size(34.dp).testTag("blindfold_toggle_button")
            ) {
                Icon(
                    imageVector = if (state.isBlindfoldMode) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = "Toggle Blindfold Mode",
                    tint = if (state.isBlindfoldMode) CyberMagenta else CyberCyan,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Settings Button
            IconButton(
                onClick = onOpenSettings,
                modifier = Modifier.size(34.dp).testTag("settings_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = CyberTextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun GameHudOverlay(
    state: GameUIState,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        // Player Info Card
        HudCard(
            label = "PLAYER",
            value = state.playerName.uppercase(),
            valueColor = CyberMagenta,
            modifier = Modifier.weight(1f).padding(end = 4.dp)
        )

        // Sub-level Card
        HudCard(
            label = "SUB-LEVEL",
            value = state.currentLevel.tier + " • " + state.currentLevel.key.takeLast(1),
            valueColor = CyberCyan,
            modifier = Modifier.weight(1.1f).padding(horizontal = 4.dp)
        )

        // Checkpoint Distance Remaining
        val remainingKm = (state.remainingMeters / 1000f)
        HudCard(
            label = "CHECKPOINT",
            value = String.format("%.2f km", remainingKm),
            valueColor = CyberGold,
            modifier = Modifier.weight(1f).padding(start = 4.dp)
        )
    }
}

@Composable
fun HudCard(
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(CyberPanel.copy(alpha = 0.90f))
            .border(1.dp, CyberPanelBorder, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            color = CyberTextMuted,
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.5.sp
        )
        Text(
            text = value,
            color = valueColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace,
            maxLines = 1
        )
    }
}

@Composable
fun VoiceBanner(
    text: String,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "banner_scale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(20.dp))
            .background(CyberMagenta.copy(alpha = 0.25f))
            .border(1.5.dp, CyberMagenta, RoundedCornerShape(20.dp))
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .testTag("voice_announcement_banner")
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun SensorStatusBar(
    shakeActive: Boolean,
    voiceActive: Boolean,
    tiltActive: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(CyberPanel.copy(alpha = 0.88f))
            .border(1.dp, CyberPanelBorder, RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 5.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SensorStatusItem(
            name = "Shake Pause",
            isActive = shakeActive,
            icon = Icons.Default.Vibration
        )
        SensorStatusItem(
            name = "Voice 'PAUSE'",
            isActive = voiceActive,
            icon = Icons.Default.Mic
        )
        SensorStatusItem(
            name = "Tilt",
            isActive = tiltActive,
            icon = Icons.Default.Refresh
        )
    }
}

@Composable
fun SensorStatusItem(
    name: String,
    isActive: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(if (isActive) CyberGreen else CyberTextMuted)
        )
        Text(
            text = name,
            color = if (isActive) CyberTextMain else CyberTextMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
