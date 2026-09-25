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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.CyberBg
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberGold
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.CyberMagenta
import com.example.ui.theme.CyberPanelBorder
import com.example.ui.theme.CyberTextMain
import com.example.ui.theme.CyberTextMuted

data class PitchSlide(
    val title: String,
    val subtitle: String,
    val badge: String,
    val points: List<Pair<String, String>>
)

val PITCH_SLIDES = listOf(
    PitchSlide(
        title = "The Accessibility Void",
        subtitle = "Fast-Paced Gaming Excludes 2.2 Billion Visually Impaired People",
        badge = "PROBLEM & MISSION",
        points = listOf(
            "Action Barriers" to "Traditional runner & reflex games are 100% visual, leaving visually impaired and blind gamers locked out of mobile action titles.",
            "High Latency Controls" to "Existing audio games are often slow text adventures or turn-based menus rather than exhilarating adrenaline experiences.",
            "Our Mission" to "EchoRunner re-engineers mobile action by combining spatial 3D audio telemetry, voice command recognition, and physical shake pauses into one unified escape experience."
        )
    ),
    PitchSlide(
        title = "Voice & Physical Shake Pause",
        subtitle = "Zero-Friction Physical Telemetry for Instant Accessibility",
        badge = "INNOVATION #1",
        points = listOf(
            "Shake-to-Pause Sensor" to "Detects sudden acceleration vector spikes (>22 m/s²). Gamers can simply shake their phone to pause or resume without looking at touch coordinates.",
            "Continuous Voice Commands" to "Microphone speech processing listens for 'PAUSE', 'STOP', 'RESUME', 'JUMP', and 'SLIDE' for complete hands-free navigation.",
            "Visual & Audio Synchronization" to "Whenever a pause occurs, TTS immediately announces state to headphones and displays golden glowing pause overlays."
        )
    ),
    PitchSlide(
        title = "Spatial Binaural Audio & TTS",
        subtitle = "Acoustic Depth Mapping Without Requiring Sight",
        badge = "INNOVATION #2",
        points = listOf(
            "Stereo Panned Beeps" to "Lane changes and approaching hazards generate binaural panned frequencies (Left: 380Hz, Right: 580Hz) creating an intuitive 3D soundstage.",
            "Frequency Signature Cues" to "Low Barriers emit high 780Hz pings (jump cue), High Beams emit 320Hz low hums (slide cue), and Wall Barriers emit 540Hz two-tone alerts.",
            "Real Android Text-to-Speech" to "Dynamic vocal co-pilot announces: 'Harini! Traffic Cone in Left Lane! Jump!' allowing 100% eyes-free gameplay in Blindfold Mode."
        )
    ),
    PitchSlide(
        title = "Native Android Architecture",
        subtitle = "Full-Stack Native Kotlin, Jetpack Compose & Room",
        badge = "TECHNICAL STACK",
        points = listOf(
            "Jetpack Compose Canvas" to "60 FPS hardware-accelerated 3D perspective renderer with animated cyber doll runner, neon scanlines, and scaling depth.",
            "Android Hardware Sensors" to "SensorManager accelerometer integration with zero-tilt auto-calibration and dynamic vibrator haptic pulses.",
            "Room Database Persistence" to "Local SQLite database storing player profiles, run telemetry (meters, obstacles dodged, crashes), and the Judges Leaderboard."
        )
    ),
    PitchSlide(
        title = "Roadmap & Inclusive Future",
        subtitle = "From Hackathon Prototype to Global Accessibility Standard",
        badge = "VISION & HORIZONS",
        points = listOf(
            "Tactile Haptic Vest Sync" to "Bluetooth LE integration with wearable haptic rumble vests for directional torso feedback.",
            "Community Soundstage Maker" to "Empowering blind creators to design audio escape courses and voice packs.",
            "Esports for All" to "Hosting mixed sighted and blindfold competitive tournaments with live spectator mode casting."
        )
    )
)

@Composable
fun PitchDeckModal(
    onDismiss: () -> Unit
) {
    var currentSlideIndex by remember { mutableIntStateOf(0) }
    val slide = PITCH_SLIDES[currentSlideIndex]

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CyberCardBg),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, CyberPanelBorder, RoundedCornerShape(16.dp))
                .testTag("pitch_deck_modal")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Top bar with close button & slide count
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyberMagenta.copy(alpha = 0.2f))
                            .border(1.dp, CyberMagenta, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = slide.badge,
                            color = CyberMagenta,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "Slide ${currentSlideIndex + 1} of ${PITCH_SLIDES.size}",
                        color = CyberTextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp).testTag("close_pitch_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = CyberTextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Slide Title & Subtitle
                Text(
                    text = slide.title,
                    color = CyberCyan,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = slide.subtitle,
                    color = CyberGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
                )

                // Slide bullet points
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    slide.points.forEach { (heading, desc) ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(CyberBg.copy(alpha = 0.8f))
                                .border(1.dp, CyberPanelBorder.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text(
                                    text = "🔹 $heading",
                                    color = CyberCyan,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = desc,
                                    color = CyberTextMain,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Navigation Controls (Prev / Next)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { if (currentSlideIndex > 0) currentSlideIndex-- },
                        enabled = currentSlideIndex > 0,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyberCyan.copy(alpha = 0.2f),
                            disabledContainerColor = Color(0xFF1E293B)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("prev_slide_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Previous",
                            tint = if (currentSlideIndex > 0) CyberCyan else CyberTextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = " Prev",
                            color = if (currentSlideIndex > 0) CyberCyan else CyberTextMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Dot indicators
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PITCH_SLIDES.indices.forEach { idx ->
                            Box(
                                modifier = Modifier
                                    .size(if (idx == currentSlideIndex) 10.dp else 6.dp)
                                    .clip(CircleShape)
                                    .background(if (idx == currentSlideIndex) CyberCyan else CyberTextMuted.copy(alpha = 0.4f))
                            )
                        }
                    }

                    Button(
                        onClick = {
                            if (currentSlideIndex < PITCH_SLIDES.size - 1) {
                                currentSlideIndex++
                            } else {
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("next_slide_button")
                    ) {
                        Text(
                            text = if (currentSlideIndex < PITCH_SLIDES.size - 1) "Next " else "Finish ",
                            color = Color.Black,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Next",
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
