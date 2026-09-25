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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.GameSettings
import com.example.ui.theme.CyberBg
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberGold
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.CyberMagenta
import com.example.ui.theme.CyberPanelBorder
import com.example.ui.theme.CyberTextMain
import com.example.ui.theme.CyberTextMuted

@Composable
fun SettingsDialog(
    currentSettings: GameSettings,
    onSaveSettings: (GameSettings) -> Unit,
    onCalibrateTilt: () -> Unit,
    onDismiss: () -> Unit
) {
    var volume by remember { mutableFloatStateOf(currentSettings.masterVolume) }
    var speechRate by remember { mutableFloatStateOf(currentSettings.speechRate) }
    var leadTime by remember { mutableFloatStateOf(currentSettings.warningLeadTimeSec) }
    var shakeEnabled by remember { mutableStateOf(currentSettings.shakePauseEnabled) }
    var voiceEnabled by remember { mutableStateOf(currentSettings.voiceCommandsEnabled) }
    var tiltEnabled by remember { mutableStateOf(currentSettings.tiltSteeringEnabled) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CyberCardBg),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, CyberPanelBorder, RoundedCornerShape(16.dp))
                .testTag("settings_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = CyberCyan,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Audio & Sensor Calibration",
                            color = CyberCyan,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp).testTag("close_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = CyberTextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Volume Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Master Volume", color = CyberTextMain, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("${(volume * 100).toInt()}%", color = CyberCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = volume,
                        onValueChange = { volume = it },
                        valueRange = 0f..1f,
                        colors = SliderDefaults.colors(
                            thumbColor = CyberCyan,
                            activeTrackColor = CyberCyan,
                            inactiveTrackColor = CyberBg
                        )
                    )
                }

                // Speech Rate Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Voice AI Speech Rate", color = CyberTextMain, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(String.format("%.2fx", speechRate), color = CyberGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = speechRate,
                        onValueChange = { speechRate = it },
                        valueRange = 0.8f..2.0f,
                        colors = SliderDefaults.colors(
                            thumbColor = CyberGold,
                            activeTrackColor = CyberGold,
                            inactiveTrackColor = CyberBg
                        )
                    )
                }

                // Warning Lead Time Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Audio Warning Lead Time", color = CyberTextMain, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(String.format("%.2f sec", leadTime), color = CyberMagenta, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = leadTime,
                        onValueChange = { leadTime = it },
                        valueRange = 0.5f..1.5f,
                        colors = SliderDefaults.colors(
                            thumbColor = CyberMagenta,
                            activeTrackColor = CyberMagenta,
                            inactiveTrackColor = CyberBg
                        )
                    )
                }

                // Shake-to-Pause Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Shake Phone to Pause", color = CyberTextMain, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("Device accelerometer trigger", color = CyberTextMuted, fontSize = 11.sp)
                    }
                    Switch(
                        checked = shakeEnabled,
                        onCheckedChange = { shakeEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = CyberGreen,
                            checkedTrackColor = CyberGreen.copy(alpha = 0.4f)
                        )
                    )
                }

                // Voice Commands Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Voice 'PAUSE' / 'RESUME'", color = CyberTextMain, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("Microphone speech recognition", color = CyberTextMuted, fontSize = 11.sp)
                    }
                    Switch(
                        checked = voiceEnabled,
                        onCheckedChange = { voiceEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = CyberCyan,
                            checkedTrackColor = CyberCyan.copy(alpha = 0.4f)
                        )
                    )
                }

                // Tilt Steering Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Tilt Steering Controls", color = CyberTextMain, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("Tilt left/right to change lane", color = CyberTextMuted, fontSize = 11.sp)
                    }
                    Switch(
                        checked = tiltEnabled,
                        onCheckedChange = { tiltEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = CyberGold,
                            checkedTrackColor = CyberGold.copy(alpha = 0.4f)
                        )
                    )
                }

                // Calibrate Zero Tilt Button
                Button(
                    onClick = onCalibrateTilt,
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("calibrate_tilt_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Calibrate",
                            tint = CyberCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Text("🎯 Calibrate Zero Tilt Level", color = CyberCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Save Button
                Button(
                    onClick = {
                        onSaveSettings(
                            GameSettings(
                                masterVolume = volume,
                                speechRate = speechRate,
                                warningLeadTimeSec = leadTime,
                                shakePauseEnabled = shakeEnabled,
                                voiceCommandsEnabled = voiceEnabled,
                                tiltSteeringEnabled = tiltEnabled
                            )
                        )
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("save_settings_button")
                ) {
                    Text("Apply & Save Settings", color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
