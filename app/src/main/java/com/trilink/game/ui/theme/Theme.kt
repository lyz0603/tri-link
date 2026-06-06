package com.trilink.game.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.trilink.game.data.ThemeMode
import com.trilink.game.data.X_COLOR_PRESETS
import com.trilink.game.data.O_COLOR_PRESETS

// ─── 棋子颜色上下文 ────────────────────────────────────────────────────────────

data class PieceColors(
    val xPiece: Color,
    val oPiece: Color,
    val xBackground: Color,
    val oBackground: Color,
    val dotColor: Color,
)

val LocalPieceColors = staticCompositionLocalOf {
    PieceColors(
        xPiece = PieceX, oPiece = PieceO,
        xBackground = PieceXLight, oBackground = PieceOLight,
        dotColor = DotGray,
    )
}

// ─── 回退配色 ──────────────────────────────────────────────────────────────────

private val FallbackLightScheme = lightColorScheme(
    primary = Color(0xFF3B5CB8),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFDBE1FA),
    onPrimaryContainer = Color(0xFF001452),
    secondary = Color(0xFF585E71),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFDCE2F9),
    onSecondaryContainer = Color(0xFF151B2C),
    tertiary = Color(0xFF735572),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFED7FA),
    onTertiaryContainer = Color(0xFF2A132C),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    surface = Color(0xFFFAF8FF),
    onSurface = Color(0xFF1A1C20),
    surfaceVariant = Color(0xFFE1E2EC),
    onSurfaceVariant = Color(0xFF444750),
    outline = Color(0xFF74777F),
    outlineVariant = Color(0xFFC4C6D0),
)

private val FallbackDarkScheme = darkColorScheme(
    primary = Color(0xFFB4C5FF),
    onPrimary = Color(0xFF002867),
    primaryContainer = Color(0xFF22448F),
    onPrimaryContainer = Color(0xFFDBE1FA),
    secondary = Color(0xFFC0C6DD),
    onSecondary = Color(0xFF2A3042),
    secondaryContainer = Color(0xFF404659),
    onSecondaryContainer = Color(0xFFDCE2F9),
    tertiary = Color(0xFFE1BBDE),
    onTertiary = Color(0xFF412742),
    tertiaryContainer = Color(0xFF5A3E59),
    onTertiaryContainer = Color(0xFFFED7FA),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    surface = Color(0xFF111318),
    onSurface = Color(0xFFE2E2E9),
    surfaceVariant = Color(0xFF444750),
    onSurfaceVariant = Color(0xFFC4C6D0),
    outline = Color(0xFF8E9099),
    outlineVariant = Color(0xFF444750),
)

@Composable
fun TrilinkTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    dynamicColor: Boolean = true,
    xColorIndex: Int = 0,
    oColorIndex: Int = 0,
    content: @Composable () -> Unit,
) {
    val systemDark = isSystemInDarkTheme()
    val darkTheme = when (themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> systemDark
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> FallbackDarkScheme
        else -> FallbackLightScheme
    }

    val xPreset = X_COLOR_PRESETS.getOrElse(xColorIndex) { X_COLOR_PRESETS[0] }
    val oPreset = O_COLOR_PRESETS.getOrElse(oColorIndex) { O_COLOR_PRESETS[0] }

    val pieceColors = if (darkTheme) {
        PieceColors(
            xPiece = xPreset.darkColor, oPiece = oPreset.darkColor,
            xBackground = xPreset.darkBg, oBackground = oPreset.darkBg,
            dotColor = Color(0xFF6B7280),
        )
    } else {
        PieceColors(
            xPiece = xPreset.lightColor, oPiece = oPreset.lightColor,
            xBackground = xPreset.lightBg, oBackground = oPreset.lightBg,
            dotColor = DotGray,
        )
    }

    CompositionLocalProvider(LocalPieceColors provides pieceColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content,
        )
    }
}
