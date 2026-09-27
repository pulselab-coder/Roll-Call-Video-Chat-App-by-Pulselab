package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val RollCallDarkColorScheme = darkColorScheme(
    primary = ElectricCyan,
    onPrimary = ObsidianNight,
    primaryContainer = ElectricCyanDim,
    onPrimaryContainer = TextPrimaryLight,
    secondary = NeonCoral,
    onSecondary = Color.White,
    secondaryContainer = NeonCoralDark,
    onSecondaryContainer = TextPrimaryLight,
    tertiary = AmberCrown,
    onTertiary = ObsidianNight,
    background = ObsidianNight,
    onBackground = TextPrimaryLight,
    surface = DeepSlateSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = ElevatedCardSurface,
    onSurfaceVariant = TextSecondaryMuted,
    outline = GlassBorderColor,
    error = CrimsonPanic,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = RollCallDarkColorScheme,
        typography = Typography,
        content = content
    )
}
