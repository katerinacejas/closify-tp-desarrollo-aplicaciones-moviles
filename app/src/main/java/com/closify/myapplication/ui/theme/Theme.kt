package com.closify.myapplication.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// ── Esquema de colores claro ──────────────────────────────────────────────────

private val LightColorScheme = lightColorScheme(
    primary             = PrimaryDark,          // #7C3AED — botón principal
    onPrimary           = SurfaceColor,         // #FFFFFF — texto sobre botón principal
    primaryContainer    = LilaPrimary,          // #B9A7F7 — chips/filtros seleccionados
    onPrimaryContainer  = TextPrimary,          // #2E2438

    secondary           = RosaSecondary,        // #F8BBD0 — botón secundario
    onSecondary         = TextPrimary,          // #2E2438
    secondaryContainer  = RosaSecondary,
    onSecondaryContainer = TextPrimary,

    tertiary            = LavandaAccent,        // #D8B4FE — accent / chips
    onTertiary          = TextPrimary,          // #2E2438

    background          = BackgroundColor,      // #FFF7FB — fondo general
    onBackground        = TextPrimary,          // #2E2438

    surface             = SurfaceColor,         // #FFFFFF — cards
    onSurface           = TextPrimary,          // #2E2438
    surfaceVariant      = SurfaceVariantColor,  // #F4ECFF — cards suaves / secciones
    onSurfaceVariant    = TextSecondary,        // #7B6B8F
    outline             = RosaSecondary,
    outlineVariant      = LavandaAccent,

    error               = ErrorColor,           // #E57373
    onError             = SurfaceColor,         // #FFFFFF
)

// ── Esquema de colores oscuro ─────────────────────────────────────────────────

private val DarkColorScheme = darkColorScheme(
    primary             = DarkPrimaryStrong,
    onPrimary           = DarkTextPrimary,
    primaryContainer    = DarkPrimary,
    onPrimaryContainer  = DarkBackgroundColor,

    secondary           = DarkSecondary,
    onSecondary         = DarkBackgroundColor,
    secondaryContainer  = DarkSecondary,
    onSecondaryContainer = DarkBackgroundColor,

    tertiary            = DarkAccent,
    onTertiary          = DarkBackgroundColor,

    background          = DarkBackgroundColor,
    onBackground        = DarkTextPrimary,
    surface             = DarkSurfaceColor,
    onSurface           = DarkTextPrimary,
    surfaceVariant      = DarkSurfaceVariantColor,
    onSurfaceVariant    = DarkTextSecondary,
    outline             = DarkOutlineColor,
    outlineVariant      = DarkAccent,

    error               = DarkErrorColor,
    onError             = DarkBackgroundColor,
)

// ── Tema principal de la app ──────────────────────────────────────────────────

@Composable
fun ClosifyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography  = ClosifyTypography,
        content     = content
    )
}
