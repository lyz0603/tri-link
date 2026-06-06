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

// ─── 棋子颜色上下文（供 BoardGrid 等组件在动态主题下获取正确的颜色）───

data class PieceColors(
    val xPiece: Color,
    val oPiece: Color,
    val xBackground: Color,
    val oBackground: Color,
    val dotColor: Color,
)

val LightPieceColors = PieceColors(
    xPiece = PieceX,
    oPiece = PieceO,
    xBackground = PieceXLight,
    oBackground = PieceOLight,
    dotColor = DotGray,
)

val DarkPieceColors = PieceColors(
    xPiece = Color(0xFF93C5FD),   // 浅蓝
    oPiece = Color(0xFFFCA5A5),   // 浅红
    xBackground = PieceXDark,
    oBackground = PieceODark,
    dotColor = Color(0xFF6B7280),
)

val LocalPieceColors = staticCompositionLocalOf { LightPieceColors }

// ─── 回退配色（Android 12 以下无 dynamic color 时使用）───

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
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> FallbackDarkScheme
        else -> FallbackLightScheme
    }

    val pieceColors = if (darkTheme) DarkPieceColors else LightPieceColors

    CompositionLocalProvider(LocalPieceColors provides pieceColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content,
        )
    }
}
