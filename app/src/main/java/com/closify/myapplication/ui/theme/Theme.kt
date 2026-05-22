package com.closify.myapplication.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = PrimaryDark,
    onPrimary = SurfaceColor,
    primaryContainer = LilaPrimary,
    onPrimaryContainer = TextPrimary,
    secondary = RosaSecondary,
    onSecondary = TextPrimary,
    tertiary = LavandaAccent,
    onTertiary = TextPrimary,
    background = BackgroundColor,
    onBackground = TextPrimary,
    surface = SurfaceColor,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariantColor,
    onSurfaceVariant = TextSecondary,
    error = ErrorColor,
    onError = SurfaceColor,
)

private val DarkColorScheme = darkColorScheme(
    primary = LilaPrimary,
    onPrimary = DarkBackgroundColor,
    primaryContainer = PrimaryDark,
    onPrimaryContainer = DarkTextColor,
    secondary = RosaSecondary,
    onSecondary = DarkBackgroundColor,
    tertiary = LavandaAccent,
    onTertiary = DarkBackgroundColor,
    background = DarkBackgroundColor,
    onBackground = DarkTextColor,
    surface = DarkSurfaceColor,
    onSurface = DarkTextColor,
    surfaceVariant = DarkSurfaceColor,
    onSurfaceVariant = DarkTextColor,
    error = ErrorColor,
    onError = SurfaceColor,
)

@Composable
fun ClosifyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ClosifyTypography,
        content = content
    )
}
