package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = MinyooOrangePrimary,
    onPrimary = Color.White,
    primaryContainer = MinyooOrangeDark,
    onPrimaryContainer = Color.White,
    secondary = MinyooGreen,
    onSecondary = Color.White,
    background = MinyooBackgroundDark,
    onBackground = Color(0xFFF1F5F9),
    surface = MinyooSurfaceDark,
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = MinyooSurfaceDarkSecondary,
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = Color(0xFF475569)
)

private val LightColorScheme = lightColorScheme(
    primary = MinyooOrangePrimary,
    onPrimary = Color.White,
    primaryContainer = MinyooOrangeContainer,
    onPrimaryContainer = MinyooOrangeOnContainer,
    secondary = MinyooSlateMuted,
    onSecondary = Color.White,
    tertiary = MinyooGreen,
    onTertiary = Color.White,
    background = MinyooBackgroundLight,
    onBackground = MinyooCharcoal,
    surface = MinyooSurfaceLight,
    onSurface = MinyooCharcoal,
    surfaceVariant = MinyooSearchBg,
    onSurfaceVariant = MinyooSlateMuted,
    outline = MinyooBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
