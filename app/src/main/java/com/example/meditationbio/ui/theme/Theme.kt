package com.example.meditationbio.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColors = darkColorScheme(
    primary = MdThemeDarkPrimary,
    secondary = MdThemeDarkSecondary,
    tertiary = MdThemeDarkTertiary,
    background = MdThemeDarkBackground,
    surface = MdThemeDarkSurface,
    surfaceVariant = MdThemeDarkSurfaceVariant,
    onPrimary = MdThemeDarkOnPrimary,
    onBackground = MdThemeDarkOnBackground,
    onSurface = MdThemeDarkOnSurface,
    onSurfaceVariant = MdThemeDarkOnSurfaceVariant,
    outline = MdThemeDarkOutline
)

@Composable
fun MeditationBioTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColors,
        typography = Typography,
        content = content
    )
}