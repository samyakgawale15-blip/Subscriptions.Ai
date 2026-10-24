package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// White Mode Theme for Subscriptions.AI
private val WhiteModeColorScheme = lightColorScheme(
  primary = TextPrimary,
  onPrimary = Color.White,
  primaryContainer = WhiteHover,
  onPrimaryContainer = TextPrimary,
  secondary = SoftSage,
  onSecondary = Color.White,
  secondaryContainer = SoftSageBg,
  onSecondaryContainer = SoftSage,
  tertiary = SoftAmber,
  onTertiary = Color.White,
  tertiaryContainer = SoftAmberBg,
  onTertiaryContainer = SoftAmber,
  background = WhiteBackground,
  onBackground = TextPrimary,
  surface = WhiteSurface,
  onSurface = TextPrimary,
  surfaceVariant = WhiteHover,
  onSurfaceVariant = TextSecondary,
  outline = WhiteBorder,
  outlineVariant = WhiteBorderSubtle,
  error = SoftRose,
  onError = Color.White,
  errorContainer = SoftRoseBg,
  onErrorContainer = SoftRose
)

@Composable
fun SubscriptionsAiTheme(
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = WhiteModeColorScheme,
    typography = Typography,
    content = content
  )
}
