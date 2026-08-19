package com.example.musicplayer.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * A single selectable app theme. Each theme owns a full Material3 ColorScheme
 * plus a couple of extra accent tokens used for the player screen (waveform,
 * gradient backdrop behind album art, etc).
 */
data class AppTheme(
    val id: String,
    val displayName: String,
    val isDark: Boolean,
    val colorScheme: ColorScheme,
    val gradientTop: Color,
    val gradientBottom: Color,
)

private val amoledBlack = Color(0xFF000000)

object AppThemes {

    val Light = AppTheme(
        id = "light",
        displayName = "Light",
        isDark = false,
        colorScheme = lightColorScheme(
            primary = Color(0xFF6750A4),
            secondary = Color(0xFF625B71),
            tertiary = Color(0xFF7D5260),
            background = Color(0xFFFFFBFE),
            surface = Color(0xFFFFFBFE),
        ),
        gradientTop = Color(0xFFEDE7F6),
        gradientBottom = Color(0xFFFFFBFE),
    )

    val Dark = AppTheme(
        id = "dark",
        displayName = "Dark",
        isDark = true,
        colorScheme = darkColorScheme(
            primary = Color(0xFFD0BCFF),
            secondary = Color(0xFFCCC2DC),
            tertiary = Color(0xFFEFB8C8),
            background = Color(0xFF1C1B1F),
            surface = Color(0xFF1C1B1F),
        ),
        gradientTop = Color(0xFF2B2930),
        gradientBottom = Color(0xFF1C1B1F),
    )

    val Amoled = AppTheme(
        id = "amoled",
        displayName = "AMOLED Black",
        isDark = true,
        colorScheme = darkColorScheme(
            primary = Color(0xFF9C7CFF),
            secondary = Color(0xFF8E8E93),
            tertiary = Color(0xFFFF6FA8),
            background = amoledBlack,
            surface = amoledBlack,
            surfaceVariant = Color(0xFF0D0D0D),
        ),
        gradientTop = Color(0xFF0A0A0A),
        gradientBottom = amoledBlack,
    )

    val Sunset = AppTheme(
        id = "sunset",
        displayName = "Sunset",
        isDark = true,
        colorScheme = darkColorScheme(
            primary = Color(0xFFFF8A65),
            secondary = Color(0xFFFFAB91),
            tertiary = Color(0xFFFFD54F),
            background = Color(0xFF2A1A1F),
            surface = Color(0xFF33202A),
        ),
        gradientTop = Color(0xFF5C2A3B),
        gradientBottom = Color(0xFF2A1A1F),
    )

    val Ocean = AppTheme(
        id = "ocean",
        displayName = "Ocean",
        isDark = true,
        colorScheme = darkColorScheme(
            primary = Color(0xFF4FC3F7),
            secondary = Color(0xFF80DEEA),
            tertiary = Color(0xFF64FFDA),
            background = Color(0xFF0B1B2B),
            surface = Color(0xFF102A3E),
        ),
        gradientTop = Color(0xFF0F3A5A),
        gradientBottom = Color(0xFF0B1B2B),
    )

    val Forest = AppTheme(
        id = "forest",
        displayName = "Forest",
        isDark = true,
        colorScheme = darkColorScheme(
            primary = Color(0xFF81C784),
            secondary = Color(0xFFA5D6A7),
            tertiary = Color(0xFFDCE775),
            background = Color(0xFF122016),
            surface = Color(0xFF17281C),
        ),
        gradientTop = Color(0xFF1F4D2E),
        gradientBottom = Color(0xFF122016),
    )

    val Pastel = AppTheme(
        id = "pastel",
        displayName = "Pastel Pop",
        isDark = false,
        colorScheme = lightColorScheme(
            primary = Color(0xFFEC7FA9),
            secondary = Color(0xFFB39DDB),
            tertiary = Color(0xFF80CBC4),
            background = Color(0xFFFFF6FA),
            surface = Color(0xFFFFF0F6),
        ),
        gradientTop = Color(0xFFFFE0EF),
        gradientBottom = Color(0xFFFFF6FA),
    )

    val Mono = AppTheme(
        id = "mono",
        displayName = "Monochrome",
        isDark = true,
        colorScheme = darkColorScheme(
            primary = Color(0xFFE0E0E0),
            secondary = Color(0xFFBDBDBD),
            tertiary = Color(0xFF9E9E9E),
            background = Color(0xFF121212),
            surface = Color(0xFF1A1A1A),
        ),
        gradientTop = Color(0xFF242424),
        gradientBottom = Color(0xFF121212),
    )

    val all: List<AppTheme> = listOf(Light, Dark, Amoled, Sunset, Ocean, Forest, Pastel, Mono)

    fun byId(id: String): AppTheme = all.firstOrNull { it.id == id } ?: Dark
}
