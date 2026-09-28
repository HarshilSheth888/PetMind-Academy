package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
  primary = TealDarkPrimary,
  onPrimary = TealDarkOnPrimary,
  primaryContainer = TealDarkPrimaryContainer,
  onPrimaryContainer = TealDarkOnPrimaryContainer,
  secondary = AmberDarkSecondary,
  onSecondary = AmberDarkOnSecondary,
  secondaryContainer = AmberDarkSecondaryContainer,
  onSecondaryContainer = AmberDarkOnSecondaryContainer,
  tertiary = HoneyDarkTertiary,
  onTertiary = HoneyDarkOnTertiary,
  tertiaryContainer = HoneyDarkTertiaryContainer,
  onTertiaryContainer = HoneyDarkOnTertiaryContainer,
  background = CanvasBackgroundDark,
  onBackground = CanvasOnBackgroundDark,
  surface = SurfaceDark,
  onSurface = SurfaceOnDark,
  surfaceVariant = SurfaceVariantDark,
  onSurfaceVariant = SurfaceVariantOnDark,
  outline = OutlineDark,
  outlineVariant = OutlineVariantDark,
)

private val LightColorScheme = lightColorScheme(
  primary = TealPrimary,
  onPrimary = TealOnPrimary,
  primaryContainer = TealPrimaryContainer,
  onPrimaryContainer = TealOnPrimaryContainer,
  secondary = AmberSecondary,
  onSecondary = AmberOnSecondary,
  secondaryContainer = AmberSecondaryContainer,
  onSecondaryContainer = AmberOnSecondaryContainer,
  tertiary = HoneyTertiary,
  onTertiary = HoneyOnTertiary,
  tertiaryContainer = HoneyTertiaryContainer,
  onTertiaryContainer = HoneyOnTertiaryContainer,
  background = CanvasBackgroundLight,
  onBackground = CanvasOnBackgroundLight,
  surface = SurfaceLight,
  onSurface = SurfaceOnLight,
  surfaceVariant = SurfaceVariantLight,
  onSurfaceVariant = SurfaceVariantOnLight,
  outline = OutlineLight,
  outlineVariant = OutlineVariantLight
)

enum class ThemeMode {
  SYSTEM, LIGHT, DARK
}

@Composable
fun PetMindTheme(
  themeMode: ThemeMode = ThemeMode.SYSTEM,
  dynamicColor: Boolean = false, // Keep branded colors consistent
  content: @Composable () -> Unit
) {
  val darkTheme = when (themeMode) {
    ThemeMode.SYSTEM -> isSystemInDarkTheme()
    ThemeMode.LIGHT -> false
    ThemeMode.DARK -> true
  }

  val colorScheme = when {
    dynamicColor && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
