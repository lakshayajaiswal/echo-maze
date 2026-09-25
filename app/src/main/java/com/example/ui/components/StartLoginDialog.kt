package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.CyberBg
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.CyberMagenta
import com.example.ui.theme.CyberPanelBorder
import com.example.ui.theme.CyberTextMain
import com.example.ui.theme.CyberTextMuted
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun StartLoginDialog(
    initialName: String,
    onStartGame: (String) -> Unit
) {
    var playerName by remember { mutableStateOf(initialName) }
    var isRecording by remember { mutableStateOf(false) }
    var isVoiceVerified by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Dialog(onDismissRequest = {}) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CyberCardBg),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, CyberPanelBorder, RoundedCornerShape(16.dp))
                .testTag("start_login_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(CyberMagenta)
                            .border(1.5.dp, CyberCyan, CircleShape)
                    )
                    Text(
                        text = "EchoRunner",
                        color = CyberCyan,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }

                Text(
                    text = "🎤 Voice & Shake Pause/Resume Accessible Escape",
                    color = CyberMagenta,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Enter your runner callsign and calibrate your voice note. You can SHAKE your phone or say 'PAUSE' / 'RESUME' anytime!",
                    color = CyberTextMuted,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )

                // Callsign input
                OutlinedTextField(
                    value = playerName,
                    onValueChange = { playerName = it },
                    label = { Text("Player Callsign") },
                    placeholder = { Text("e.g. Harini") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = CyberPanelBorder,
                        focusedLabelColor = CyberCyan,
                        cursorColor = CyberCyan,
                        focusedTextColor = CyberTextMain,
                        unfocusedTextColor = CyberTextMain
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("player_name_input")
                )

                // Voice Note Calibration Button
                Button(
                    onClick = {
                        if (!isRecording && !isVoiceVerified) {
                            isRecording = true
                            scope.launch {
                                delay(1800)
                                isRecording = false
                                isVoiceVerified = true
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isVoiceVerified) Color(0xFF163229) else if (isRecording) Color(0xFF421221) else Color(0xFF1E293B)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("record_voice_login_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (isRecording) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = CyberMagenta,
                                strokeWidth = 2.dp
                            )
                            Text(
                                text = "🔴 Recording Voice Note... Speak Now!",
                                color = CyberMagenta,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        } else if (isVoiceVerified) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Verified",
                                tint = CyberGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "✅ Voice Profile Verified!",
                                color = CyberGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Record Voice",
                                tint = CyberCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "🎤 Tap to Calibrate Voice Login Note",
                                color = CyberCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Launch Game Button
                Button(
                    onClick = { onStartGame(playerName) },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("start_game_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Start",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "🚀 Launch EchoRunner",
                            color = Color.Black,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}
