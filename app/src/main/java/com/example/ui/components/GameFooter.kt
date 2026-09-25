package com.example.ui.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.model.LevelCatalog
import com.example.model.LevelDefinition
import com.example.ui.theme.CyberBg
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberGold
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.CyberPanel
import com.example.ui.theme.CyberPanelBorder
import com.example.ui.theme.CyberTextMain
import com.example.ui.theme.CyberTextMuted

@Composable
fun GameFooter(
    currentLevel: LevelDefinition,
    onSelectLevel: (String) -> Unit,
    onCalibrate: () -> Unit,
    onRestart: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(CyberPanel.copy(alpha = 0.95f))
            .border(width = 1.dp, color = CyberPanelBorder)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Level Selector Dropdown
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(CyberBg)
                    .border(1.dp, CyberPanelBorder, RoundedCornerShape(8.dp))
                    .clickable { expanded = true }
                    .padding(horizontal = 10.dp, vertical = 7.dp)
                    .testTag("level_selector_dropdown")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = currentLevel.name,
                        color = CyberCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Expand levels",
                        tint = CyberCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(CyberCardBg).border(1.dp, CyberPanelBorder)
                ) {
                    LevelCatalog.LEVELS.forEach { level ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(
                                        text = level.name,
                                        color = if (level.key == currentLevel.key) CyberCyan else CyberTextMain,
                                        fontWeight = if (level.key == currentLevel.key) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = "${level.lowObs.name} • ${level.highObs.name} • ${level.wallObs.name}",
                                        color = CyberTextMuted,
                                        fontSize = 10.sp
                                    )
                                }
                            },
                            onClick = {
                                onSelectLevel(level.key)
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Calibrate Button
            Button(
                onClick = onCalibrate,
                colors = ButtonDefaults.buttonColors(containerColor = CyberBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberPanelBorder),
                shape = RoundedCornerShape(8.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                modifier = Modifier.testTag("footer_calibrate_button")
            ) {
                Text(
                    text = "🎯 Calibrate",
                    color = CyberTextMain,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Restart Button
            Button(
                onClick = onRestart,
                colors = ButtonDefaults.buttonColors(containerColor = CyberBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberPanelBorder),
                shape = RoundedCornerShape(8.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                modifier = Modifier.testTag("footer_restart_button")
            ) {
                Text(
                    text = "🔄 Restart",
                    color = CyberTextMain,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Helper instruction text
        Text(
            text = "💻 Swipe/Tap: Left / Jump / Slide / Right | 📱 Phone: Tilt, Shake & Say \"PAUSE\"!",
            color = CyberTextMuted,
            fontSize = 10.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun VictoryCheckpointDialog(
    message: String,
    onNextLevel: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CyberCardBg),
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, CyberGold, RoundedCornerShape(16.dp))
                .testTag("victory_checkpoint_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "🎉 CHECKPOINT REACHED!",
                    color = CyberGold,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = message,
                    color = CyberTextMain,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )

                Button(
                    onClick = onNextLevel,
                    colors = ButtonDefaults.buttonColors(containerColor = CyberGreen),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("next_level_button")
                ) {
                    Text(
                        text = "🚀 Advance to Next Sub-Level",
                        color = Color.Black,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
