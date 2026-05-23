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

    error               = ErrorColor,           // #E57373
    onError             = SurfaceColor,         // #FFFFFF
)

// ── Esquema de colores oscuro ─────────────────────────────────────────────────

private val DarkColorScheme = darkColorScheme(
    primary             = LilaPrimary,          // #B9A7F7 — botones principales dark
    onPrimary           = DarkBackgroundColor,  // #17111F
    primaryContainer    = PrimaryDark,          // #7C3AED
    onPrimaryContainer  = DarkTextColor,        // #F8F1FF

    secondary           = RosaSecondary,        // #F8BBD0
    onSecondary         = DarkBackgroundColor,  // #17111F
    secondaryContainer  = RosaSecondary,
    onSecondaryContainer = DarkBackgroundColor,

    tertiary            = LavandaAccent,        // #D8B4FE
    onTertiary          = DarkBackgroundColor,  // #17111F

    background          = DarkBackgroundColor,  // #17111F — fondo dark
    onBackground        = DarkTextColor,        // #F8F1FF

    surface             = DarkSurfaceColor,     // #241A2E — cards dark
    onSurface           = DarkTextColor,        // #F8F1FF
    surfaceVariant      = DarkSurfaceColor,     // #241A2E
    onSurfaceVariant    = DarkTextColor,        // #F8F1FF

    error               = ErrorColor,           // #E57373
    onError             = SurfaceColor,         // #FFFFFF
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
