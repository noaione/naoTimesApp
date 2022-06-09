package me.naoti.panelapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import me.naoti.panelapp.R

val InterFamily = FontFamily(
    Font(R.font.inter_thin, FontWeight.Thin),
    Font(R.font.inter_extra_light, FontWeight.ExtraLight),
    Font(R.font.inter_light, FontWeight.Light),
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
    Font(R.font.inter_bold, FontWeight.Bold),
    Font(R.font.inter_extra_bold, FontWeight.ExtraBold),
    Font(R.font.inter_black, FontWeight.Black)
)

val AppTypography = Typography(
    labelLarge = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.10000000149011612.sp,
        lineHeight = 20.sp,
        fontSize = 14.sp
    ),
    labelMedium = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.5.sp,
        lineHeight = 16.sp,
        fontSize = 12.sp
    ),
    labelSmall = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.5.sp,
        lineHeight = 16.sp,
        fontSize = 11.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.W400,
        letterSpacing = 0.5.sp,
        lineHeight = 24.sp,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.W400,
        letterSpacing = 0.25.sp,
        lineHeight = 20.sp,
        fontSize = 14.sp
    ),
    bodySmall = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.W400,
        letterSpacing = 0.4000000059604645.sp,
        lineHeight = 16.sp,
        fontSize = 12.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.W400,
        letterSpacing = 0.sp,
        lineHeight = 40.sp,
        fontSize = 32.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.W400,
        letterSpacing = 0.sp,
        lineHeight = 36.sp,
        fontSize = 28.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.W400,
        letterSpacing = 0.sp,
        lineHeight = 32.sp,
        fontSize = 24.sp
    ),
    displayLarge = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.W400,
        letterSpacing = (-0.25).sp,
        lineHeight = 64.sp,
        fontSize = 57.sp
    ),
    displayMedium = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.W400,
        letterSpacing = 0.sp,
        lineHeight = 52.sp,
        fontSize = 45.sp
    ),
    displaySmall = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.W400,
        letterSpacing = 0.sp,
        lineHeight = 44.sp,
        fontSize = 36.sp
    ),
    titleLarge = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.W400,
        letterSpacing = 0.sp,
        lineHeight = 28.sp,
        fontSize = 22.sp
    ),
    titleMedium = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.15000000596046448.sp,
        lineHeight = 24.sp,
        fontSize = 16.sp
    ),
    titleSmall = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.10000000149011612.sp,
        lineHeight = 20.sp,
        fontSize = 14.sp
    ),
)