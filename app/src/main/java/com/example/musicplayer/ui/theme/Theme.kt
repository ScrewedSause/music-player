package com.example.musicplayer.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val AppTypography = Typography(
    headlineSmall = TextStyle(fontWeight = FontWeight.Bold, fontSize = 22.sp),
    titleLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 20.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 16.sp),
    bodyLarge = TextStyle(fontSize = 15.sp),
    bodyMedium = TextStyle(fontSize = 13.sp),
    labelSmall = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Medium),
)

/** Exposes the currently selected [AppTheme] object (not just its ColorScheme)
 *  so screens can reach the extra gradient tokens. */
val LocalAppTheme = compositionLocalOf { AppThemes.Dark }

@Composable
fun MusicPlayerTheme(
    appTheme: AppTheme = if (isSystemInDarkTheme()) AppThemes.Dark else AppThemes.Light,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalAppTheme provides appTheme) {
        MaterialTheme(
            colorScheme = appTheme.colorScheme,
            typography = AppTypography,
            content = content,
        )
    }
}
