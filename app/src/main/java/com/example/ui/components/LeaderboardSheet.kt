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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.LeaderboardEntryEntity
import com.example.data.local.RunRecordEntity
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

@Composable
fun LeaderboardSheet(
    leaderboardEntries: List<LeaderboardEntryEntity>,
    recentRuns: List<RunRecordEntity>,
    currentPlayerName: String,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CyberCardBg),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, CyberPanelBorder, RoundedCornerShape(16.dp))
                .testTag("leaderboard_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Header
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
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Trophy",
                            tint = CyberGold,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Leaderboard & Stats",
                            color = CyberCyan,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp).testTag("close_leaderboard_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = CyberTextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = CyberPanel,
                    contentColor = CyberCyan,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = CyberCyan
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                "Judges Rank",
                                color = if (selectedTab == 0) CyberCyan else CyberTextMuted,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                "Run History",
                                color = if (selectedTab == 1) CyberCyan else CyberTextMuted,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (selectedTab == 0) {
                    // Judges Leaderboard Tab
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(leaderboardEntries) { entry ->
                            val isMe = entry.isUser || entry.name.equals(currentPlayerName, ignoreCase = true)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isMe) CyberMagenta.copy(alpha = 0.2f) else CyberBg)
                                    .border(
                                        width = 1.dp,
                                        color = if (isMe) CyberMagenta else CyberPanelBorder.copy(alpha = 0.4f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        text = "${entry.rank}.",
                                        color = if (entry.rank <= 3) CyberGold else CyberTextMuted,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 14.sp
                                    )
                                    Column {
                                        Text(
                                            text = if (isMe) "${entry.name} (You)" else entry.name,
                                            color = if (isMe) CyberCyan else CyberTextMain,
                                            fontWeight = if (isMe) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = entry.levelName,
                                            color = CyberTextMuted,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                Text(
                                    text = String.format("%.2f km", entry.distanceKm),
                                    color = if (isMe) CyberGold else CyberCyan,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                } else {
                    // Recent Runs Tab
                    if (recentRuns.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No run history yet!\nComplete checkpoints to record your score.",
                                color = CyberTextMuted,
                                fontSize = 13.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(recentRuns) { run ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(CyberBg)
                                        .border(1.dp, CyberPanelBorder.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = run.levelName,
                                            color = CyberCyan,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = String.format("%.2f km", run.distanceMeters / 1000f),
                                            color = CyberGold,
                                            fontWeight = FontWeight.Black,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 13.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Dodged: ${run.obstaclesDodged} • Impacts: ${run.crashes}",
                                            color = CyberTextMuted,
                                            fontSize = 11.sp
                                        )
                                        Text(
                                            text = if (run.completed) "✅ COMPLETED" else "FAILED",
                                            color = if (run.completed) CyberGreen else CyberMagenta,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
