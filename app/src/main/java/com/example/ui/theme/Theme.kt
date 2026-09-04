package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val SophisticatedDarkColorScheme =
  darkColorScheme(
    primary = DarkPrimaryLilac,
    onPrimary = DarkOnPrimaryPurple,
    primaryContainer = DarkPrimaryContainerPurple,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = Color(0xFFCCC2DC),
    onSecondary = Color(0xFF332D41),
    secondaryContainer = Color(0xFF4A4458),
    onSecondaryContainer = Color(0xFFE8DEF8),
    tertiary = Color(0xFFEFB8C8),
    onTertiary = Color(0xFF492532),
    tertiaryContainer = Color(0xFF633B48),
    onTertiaryContainer = Color(0xFFFFD8E4),
    background = DarkBg,
    onBackground = TextPrimary,
    surface = DarkBg,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceCard,
    onSurfaceVariant = TextSecondary,
    outline = Color(0xFF49454F),
    outlineVariant = BorderSubtle,
    error = StatusViolationRed,
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC)
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = SophisticatedDarkColorScheme,
    typography = Typography,
    content = content
  )
}

