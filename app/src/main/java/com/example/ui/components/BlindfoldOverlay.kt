package com.example.ui.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberMagenta
import com.example.ui.theme.CyberTextMuted

@Composable
fun BlindfoldOverlay(
    onExitBlindfold: () -> Unit,
    onShiftLeft: () -> Unit,
    onShiftRight: () -> Unit,
    onJump: () -> Unit,
    onSlide: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("blindfold_overlay")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Accessible Title
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(
                    text = "BLINDFOLD CYBER RUNNER",
                    color = CyberMagenta,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Say \"PAUSE\" or shake phone to pause anytime!\nListen to audio cues: Left/Right ping & warning tones.",
                    color = CyberTextMuted,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }

            // High-Contrast Tactile Touch Zones
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Top: Jump Zone
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.5.dp, CyberCyan, RoundedCornerShape(12.dp))
                        .clickable { onJump() }
                        .testTag("tactile_jump_zone"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⬆️ TAP TOP: JUMP",
                        color = CyberCyan,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Middle: Left & Right Zones
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1.5f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.5.dp, CyberCyan, RoundedCornerShape(12.dp))
                            .clickable { onShiftLeft() }
                            .testTag("tactile_left_zone"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "⬅️ TAP LEFT\nLANE",
                            color = CyberCyan,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.5.dp, CyberMagenta, RoundedCornerShape(12.dp))
                            .clickable { onShiftRight() }
                            .testTag("tactile_right_zone"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "➡️ TAP RIGHT\nLANE",
                            color = CyberMagenta,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Bottom: Slide Zone
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.5.dp, CyberMagenta, RoundedCornerShape(12.dp))
                        .clickable { onSlide() }
                        .testTag("tactile_slide_zone"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⬇️ TAP BOTTOM: SLIDE / DUCK",
                        color = CyberMagenta,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Exit Button
            Button(
                onClick = onExitBlindfold,
                colors = ButtonDefaults.buttonColors(containerColor = CyberMagenta),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("exit_blindfold_button")
            ) {
                Text(
                    text = "Exit Blindfold Mode (Spectator)",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
