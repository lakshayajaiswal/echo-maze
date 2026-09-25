package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EchoRunnerColorScheme = darkColorScheme(
    primary = CyberCyan,
    onPrimary = Color.Black,
    primaryContainer = CyberPanel,
    onPrimaryContainer = CyberCyan,
    secondary = CyberMagenta,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF2A0818),
    onSecondaryContainer = CyberMagenta,
    tertiary = CyberGold,
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF2B2205),
    onTertiaryContainer = CyberGold,
    background = CyberBg,
    onBackground = CyberTextMain,
    surface = CyberPanel,
    onSurface = CyberTextMain,
    surfaceVariant = CyberCardBg,
    onSurfaceVariant = CyberTextMuted,
    outline = CyberPanelBorder
)

@Composable
fun EchoRunnerTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = EchoRunnerColorScheme,
        typography = Typography,
        content = content
    )
}
