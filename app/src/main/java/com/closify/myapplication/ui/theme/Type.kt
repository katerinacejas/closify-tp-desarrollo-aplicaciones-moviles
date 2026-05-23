package com.closify.myapplication.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.closify.myapplication.R

// ── Google Fonts provider ─────────────────────────────────────────────────────

private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage   = "com.google.android.gms",
    certificates      = R.array.com_google_android_gms_fonts_certs
)

private val poppins = GoogleFont("Poppins")

val PoppinsFontFamily = FontFamily(
    Font(googleFont = poppins, fontProvider = provider, weight = FontWeight.Normal),   // 400
    Font(googleFont = poppins, fontProvider = provider, weight = FontWeight.Medium),   // 500
    Font(googleFont = poppins, fontProvider = provider, weight = FontWeight.SemiBold), // 600
    Font(googleFont = poppins, fontProvider = provider, weight = FontWeight.Bold),     // 700
)

// ── Tipografía Closify ────────────────────────────────────────────────────────

val ClosifyTypography = Typography(

    // Títulos grandes de pantallas principales — Poppins SemiBold 600 / 28sp
    displaySmall = TextStyle(
        fontFamily   = PoppinsFontFamily,
        fontWeight   = FontWeight.SemiBold,
        fontSize     = 28.sp,
        lineHeight   = 36.sp,
        letterSpacing = 0.sp
    ),

    // Títulos de secciones dentro de una pantalla — Poppins SemiBold 600 / 22sp
    headlineSmall = TextStyle(
        fontFamily   = PoppinsFontFamily,
        fontWeight   = FontWeight.SemiBold,
        fontSize     = 22.sp,
        lineHeight   = 30.sp,
        letterSpacing = 0.sp
    ),

    // Títulos de cards y bloques — Poppins SemiBold 600 / 18sp
    titleLarge = TextStyle(
        fontFamily   = PoppinsFontFamily,
        fontWeight   = FontWeight.SemiBold,
        fontSize     = 18.sp,
        lineHeight   = 26.sp,
        letterSpacing = 0.sp
    ),

    // Texto común de la app — Poppins Regular 400 / 14sp
    bodyMedium = TextStyle(
        fontFamily   = PoppinsFontFamily,
        fontWeight   = FontWeight.Normal,
        fontSize     = 14.sp,
        lineHeight   = 22.sp,
        letterSpacing = 0.sp
    ),

    // Aclaraciones, subtítulos, fechas, texto secundario — Poppins Regular 400 / 12sp
    bodySmall = TextStyle(
        fontFamily   = PoppinsFontFamily,
        fontWeight   = FontWeight.Normal,
        fontSize     = 12.sp,
        lineHeight   = 18.sp,
        letterSpacing = 0.sp
    ),

    // Botones principales y secundarios — Poppins SemiBold 600 / 15sp
    labelLarge = TextStyle(
        fontFamily   = PoppinsFontFamily,
        fontWeight   = FontWeight.SemiBold,
        fontSize     = 15.sp,
        lineHeight   = 20.sp,
        letterSpacing = 0.sp
    ),

    // Chips, etiquetas y categorías — Poppins Medium 500 / 12sp
    labelSmall = TextStyle(
        fontFamily   = PoppinsFontFamily,
        fontWeight   = FontWeight.Medium,
        fontSize     = 12.sp,
        lineHeight   = 16.sp,
        letterSpacing = 0.sp
    )
)
