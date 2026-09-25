package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import com.example.model.ActiveObstacle
import com.example.model.LevelDefinition
import com.example.model.ObstacleType
import com.example.ui.theme.CyberBg
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberGold
import com.example.ui.theme.CyberMagenta
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.sin

@Composable
fun CyberRunnerCanvas(
    currentLaneX: Float,
    isJumping: Boolean,
    jumpY: Float,
    isSliding: Boolean,
    runAnimTime: Float,
    obstacles: List<ActiveObstacle>,
    currentLevel: LevelDefinition,
    isBlindfoldMode: Boolean,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    onSwipeUp: () -> Unit,
    onSwipeDown: () -> Unit,
    modifier: Modifier = Modifier
) {
    var dragStartX by remember { mutableFloatStateOf(0f) }
    var dragStartY by remember { mutableFloatStateOf(0f) }
    var totalDragX by remember { mutableFloatStateOf(0f) }
    var totalDragY by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = modifier
            .testTag("game_canvas_container")
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { offset ->
                        val width = size.width
                        if (offset.x < width * 0.35f) {
                            onSwipeLeft()
                        } else if (offset.x > width * 0.65f) {
                            onSwipeRight()
                        } else {
                            if (offset.y < size.height * 0.5f) {
                                onSwipeUp()
                            } else {
                                onSwipeDown()
                            }
                        }
                    }
                )
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        dragStartX = offset.x
                        dragStartY = offset.y
                        totalDragX = 0f
                        totalDragY = 0f
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        totalDragX += dragAmount.x
                        totalDragY += dragAmount.y
                    },
                    onDragEnd = {
                        val distance = hypot(totalDragX, totalDragY)
                        if (distance > 35f) {
                            if (abs(totalDragX) > abs(totalDragY)) {
                                if (totalDragX < 0) onSwipeLeft() else onSwipeRight()
                            } else {
                                if (totalDragY < 0) onSwipeUp() else onSwipeDown()
                            }
                        }
                    }
                )
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize().testTag("cyber_runner_canvas")) {
            val w = size.width
            val h = size.height

            // In Blindfold Mode, draw pure pitch black for sensory accessibility
            if (isBlindfoldMode) {
                drawRect(color = Color.Black, size = size)
                return@Canvas
            }

            // Cyberpunk Dark Background
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(CyberBg, Color(0xFF070D1E), Color(0xFF030712))
                ),
                size = size
            )

            val horizonY = h * 0.36f
            val horizonX = w * 0.5f

            // Perspective Grid Lane Lines (converging at horizon)
            val laneMultipliers = listOf(-1.5f, -0.5f, 0.5f, 1.5f)
            laneMultipliers.forEach { multiplier ->
                drawLine(
                    color = CyberCyan.copy(alpha = 0.45f),
                    start = Offset(horizonX, horizonY),
                    end = Offset(w * 0.5f + multiplier * (w * 0.30f), h),
                    strokeWidth = 2.5f
                )
            }

            // Moving Horizontal Perspective Speed Lines
            val speedOffset = (runAnimTime * 4.5f) % 20f
            var zLine = 8f
            while (zLine < 120f) {
                val currentZ = zLine - speedOffset
                if (currentZ > 1f) {
                    val scale = 1f / (currentZ * 0.028f)
                    val y = horizonY + (h - horizonY) * (1f - 1f / scale)
                    val alpha = (scale * 0.22f).coerceIn(0.05f, 0.65f)
                    drawLine(
                        color = CyberMagenta.copy(alpha = alpha),
                        start = Offset(w * 0.05f, y),
                        end = Offset(w * 0.95f, y),
                        strokeWidth = (1.5f * scale).coerceIn(1f, 4f)
                    )
                }
                zLine += 14f
            }

            // Render 3D Obstacles sorted by depth descending
            val sortedObstacles = obstacles.sortedByDescending { it.z }
            for (obs in sortedObstacles) {
                if (obs.z <= 0.5f || obs.z > 95f) continue

                val scale = 1f / (obs.z * 0.025f)
                val y = horizonY + (h - horizonY) * (1f - 1f / scale)
                val laneWidth = (w * 0.25f) * scale
                val x = horizonX + obs.lane * laneWidth - (laneWidth / 2f)
                val obsH = 42f * scale

                val obsColor = when (obs.type) {
                    ObstacleType.LOW_BARRIER -> Color(currentLevel.lowObs.colorHex)
                    ObstacleType.HIGH_BEAM -> Color(currentLevel.highObs.colorHex)
                    ObstacleType.WALL -> Color(currentLevel.wallObs.colorHex)
                }

                when (obs.type) {
                    ObstacleType.LOW_BARRIER -> {
                        // Ground obstacle (e.g. Traffic Cone, Electric Spike)
                        drawRect(
                            color = obsColor,
                            topLeft = Offset(x, y - obsH * 0.55f),
                            size = Size(laneWidth, obsH * 0.55f)
                        )
                        drawRect(
                            color = Color.White,
                            topLeft = Offset(x, y - obsH * 0.55f),
                            size = Size(laneWidth, obsH * 0.55f),
                            style = Stroke(width = (2f * scale).coerceIn(1f, 4f))
                        )
                    }
                    ObstacleType.HIGH_BEAM -> {
                        // High Beam (e.g. Plasma Beam, Neon Curb)
                        drawRect(
                            color = obsColor.copy(alpha = 0.85f),
                            topLeft = Offset(x, y - obsH * 1.6f),
                            size = Size(laneWidth, obsH * 0.6f)
                        )
                        drawRect(
                            color = Color.White,
                            topLeft = Offset(x, y - obsH * 1.6f),
                            size = Size(laneWidth, obsH * 0.6f),
                            style = Stroke(width = (2.5f * scale).coerceIn(1f, 5f))
                        )
                    }
                    ObstacleType.WALL -> {
                        // Tall Wall Barrier
                        drawRect(
                            color = obsColor,
                            topLeft = Offset(x, y - obsH * 1.4f),
                            size = Size(laneWidth, obsH * 1.4f)
                        )
                        drawRect(
                            color = Color.White,
                            topLeft = Offset(x, y - obsH * 1.4f),
                            size = Size(laneWidth, obsH * 1.4f),
                            style = Stroke(width = (3f * scale).coerceIn(1.5f, 6f))
                        )
                    }
                }
            }

            // Draw Cyber Doll Runner
            val playerX = w * 0.5f + currentLaneX * (w * 0.25f)
            val playerY = h * 0.84f - jumpY * 22f

            drawCyberDoll(
                x = playerX,
                y = playerY,
                isJumping = isJumping,
                isSliding = isSliding,
                runAnimTime = runAnimTime
            )
        }
    }
}

private fun DrawScope.drawCyberDoll(
    x: Float,
    y: Float,
    isJumping: Boolean,
    isSliding: Boolean,
    runAnimTime: Float
) {
    val runCycle = sin(runAnimTime) * 12f

    if (isSliding) {
        // Sliding posture: sleek cyber speed board + crouching torso
        // Cyber Glider Board
        drawRoundRect(
            color = CyberCyan,
            topLeft = Offset(x - 30f, y + 8f),
            size = Size(60f, 10f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
        )
        // Lowered Cyber Torso
        drawRect(
            color = CyberMagenta,
            topLeft = Offset(x - 24f, y - 14f),
            size = Size(48f, 20f)
        )
        // Head & Visor
        drawCircle(
            color = Color.White,
            radius = 12f,
            center = Offset(x + 16f, y - 10f)
        )
        drawRect(
            color = CyberCyan,
            topLeft = Offset(x + 14f, y - 14f),
            size = Size(14f, 6f)
        )
    } else {
        // Jumping booster rings
        if (isJumping) {
            drawCircle(color = CyberCyan, radius = 8f, center = Offset(x - 12f, y + 26f))
            drawCircle(color = CyberCyan, radius = 8f, center = Offset(x + 12f, y + 26f))
            drawCircle(color = CyberGold, radius = 4f, center = Offset(x - 12f, y + 32f))
            drawCircle(color = CyberGold, radius = 4f, center = Offset(x + 12f, y + 32f))
        }

        // Running Legs
        val legLeftY = if (isJumping) 18f else 26f + runCycle * 0.6f
        val legRightY = if (isJumping) 18f else 26f - runCycle * 0.6f

        drawLine(
            color = CyberCyan,
            start = Offset(x - 8f, y + 8f),
            end = Offset(x - 8f, y + legLeftY),
            strokeWidth = 7f
        )
        drawLine(
            color = CyberCyan,
            start = Offset(x + 8f, y + 8f),
            end = Offset(x + 8f, y + legRightY),
            strokeWidth = 7f
        )

        // Torso Armor
        drawRect(
            color = CyberMagenta,
            topLeft = Offset(x - 14f, y - 22f),
            size = Size(28f, 32f)
        )

        // Arms
        val armLeftOffset = runCycle * 0.5f
        drawLine(
            color = Color.White,
            start = Offset(x - 14f, y - 16f),
            end = Offset(x - 24f, y - 6f - armLeftOffset),
            strokeWidth = 5f
        )
        drawLine(
            color = Color.White,
            start = Offset(x + 14f, y - 16f),
            end = Offset(x + 24f, y - 6f + armLeftOffset),
            strokeWidth = 5f
        )

        // Cyber Helmet
        drawCircle(
            color = Color.White,
            radius = 15f,
            center = Offset(x, y - 36f)
        )

        // Glowing Cyan Visor
        drawRect(
            color = CyberCyan,
            topLeft = Offset(x - 10f, y - 40f),
            size = Size(20f, 7f)
        )

        // Visor Mohawk / Antenna
        val crownPath = Path().apply {
            moveTo(x - 6f, y - 48f)
            lineTo(x + 6f, y - 48f)
            lineTo(x, y - 56f)
            close()
        }
        drawPath(path = crownPath, color = CyberMagenta)
    }
}
