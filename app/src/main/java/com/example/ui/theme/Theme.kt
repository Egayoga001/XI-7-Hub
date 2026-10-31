package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = WoodyRed,
    onPrimary = Color.White,
    primaryContainer = WoodyYellow,
    onPrimaryContainer = ComicBorder,
    secondary = BuzzPurple,
    onSecondary = Color.White,
    secondaryContainer = AndySkyLight,
    onSecondaryContainer = ComicBorder,
    tertiary = BuzzGreen,
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFFC8F6C8),
    onTertiaryContainer = ComicBorder,
    background = AndySkyBlue,
    onBackground = ComicBorder,
    surface = ComicCardBg,
    onSurface = ComicBorder,
    surfaceVariant = Color(0xFFFFFFFF),
    onSurfaceVariant = ComicBorder,
    outline = ComicBorder
)

private val DarkColorScheme = darkColorScheme(
    primary = WoodyRed,
    onPrimary = Color.White,
    primaryContainer = WoodyYellow,
    onPrimaryContainer = ComicBorder,
    secondary = BuzzPurple,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF4A148C),
    onSecondaryContainer = Color(0xFFE1BEE7),
    tertiary = BuzzGreen,
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF1B5E20),
    onTertiaryContainer = Color(0xFFC8F6C8),
    background = Color(0xFF0F1E36),
    onBackground = Color(0xFFFFFFFF),
    surface = Color(0xFF1E293B),
    onSurface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFFE2E8F0),
    outline = BuzzGreen
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
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
