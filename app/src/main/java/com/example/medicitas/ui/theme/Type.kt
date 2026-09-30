package com.example.medicitas.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

private val Fuente = FontFamily.SansSerif

val Typography = Typography(
    headlineLarge = TextStyle(fontFamily = Fuente, fontWeight = FontWeight.Bold, fontSize = 28.sp, lineHeight = 36.sp, letterSpacing = (-0.02).em),
    headlineMedium = TextStyle(fontFamily = Fuente, fontWeight = FontWeight.SemiBold, fontSize = 22.sp, lineHeight = 28.sp, letterSpacing = (-0.01).em),
    headlineSmall = TextStyle(fontFamily = Fuente, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, lineHeight = 24.sp),
    titleLarge = TextStyle(fontFamily = Fuente, fontWeight = FontWeight.SemiBold, fontSize = 20.sp, lineHeight = 26.sp),
    titleMedium = TextStyle(fontFamily = Fuente, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 22.sp),
    titleSmall = TextStyle(fontFamily = Fuente, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp),
    bodyLarge = TextStyle(fontFamily = Fuente, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontFamily = Fuente, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall = TextStyle(fontFamily = Fuente, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 16.sp),
    labelLarge = TextStyle(fontFamily = Fuente, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.01.em),
    labelMedium = TextStyle(fontFamily = Fuente, fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.02.em),
    labelSmall = TextStyle(fontFamily = Fuente, fontWeight = FontWeight.Bold, fontSize = 10.sp, lineHeight = 14.sp, letterSpacing = 0.05.em)
)

// Cifras clínicas con números tabulares
val MetricaGrande = TextStyle(fontFamily = Fuente, fontWeight = FontWeight.Bold, fontSize = 24.sp, lineHeight = 28.sp, fontFeatureSettings = "tnum")
val MetricaMedia = TextStyle(fontFamily = Fuente, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, lineHeight = 22.sp, fontFeatureSettings = "tnum")
