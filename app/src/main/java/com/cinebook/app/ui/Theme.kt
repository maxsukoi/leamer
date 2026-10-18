package com.cinebook.app.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Accent = Color(0xFFFF4D5A)
val Gold = Color(0xFFFFC857)

private val CineColors = darkColorScheme(
    primary = Accent,
    onPrimary = Color.White,
    secondary = Gold,
    onSecondary = Color(0xFF1A1A1A),
    background = Color(0xFF0E0E14),
    onBackground = Color(0xFFF2F2F7),
    surface = Color(0xFF1B1B26),
    onSurface = Color(0xFFF2F2F7),
    surfaceVariant = Color(0xFF2A2A38),
    onSurfaceVariant = Color(0xFFB9B9C9),
    outline = Color(0xFF4A4A5C)
)

@Composable
fun CineBookTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = CineColors, content = content)
}
