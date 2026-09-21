package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
  primary = NeonIndigoLight,
  onPrimary = Slate950,
  primaryContainer = Slate800,
  onPrimaryContainer = NeonIndigoLight,
  secondary = NeonCyan,
  onSecondary = Slate950,
  secondaryContainer = Slate800,
  onSecondaryContainer = NeonCyan,
  tertiary = NeonCrimson,
  onTertiary = Color.White,
  background = Slate950,
  onBackground = Slate50,
  surface = Slate900,
  onSurface = Slate50,
  surfaceVariant = Slate800,
  onSurfaceVariant = Slate400,
  outline = Slate700,
  outlineVariant = Slate800,
  error = NeonRed,
  onError = Color.White,
  errorContainer = CrimsonFlash,
  onErrorContainer = NeonRed
)

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = DarkColorScheme,
    typography = Typography,
    content = content
  )
}
