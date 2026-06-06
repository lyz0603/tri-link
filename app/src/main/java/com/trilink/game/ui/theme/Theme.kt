package com.trilink.game.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
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
import com.trilink.game.data.Language
import com.trilink.game.data.Strings
import com.trilink.game.data.ThemeMode
import com.trilink.game.data.X_COLOR_PRESETS
import com.trilink.game.data.O_COLOR_PRESETS
import com.trilink.game.data.ZhStrings
import com.trilink.game.data.getStrings

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

val LocalStrings = staticCompositionLocalOf<Strings> { ZhStrings }

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

/** 解析 hex 字符串 (#RRGGBB 或 RRGGBB) 为 Color，失败返回 null */
fun parseHex(hex: String): Color? {
    val s = hex.trim().removePrefix("#")
    if (s.length != 6) return null
    return try {
        Color(("FF$s").toLong(16))
    } catch (_: Exception) {
        null
    }
}

/** 根据 seed hex 生成自定义配色方案 */
fun seedColorScheme(seedHex: String, dark: Boolean): ColorScheme? {
    val seed = parseHex(seedHex) ?: return null
    return if (dark) darkColorScheme(primary = seed)
    else lightColorScheme(primary = seed)
}

@Composable
fun TrilinkTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    dynamicColor: Boolean = true,
    xColorIndex: Int = 0,
    oColorIndex: Int = 0,
    customXColorHex: String = "",
    customOColorHex: String = "",
    customThemeSeedHex: String = "",
    language: Language = Language.ZH,
    content: @Composable () -> Unit,
) {
    val strings = getStrings(language)
    val systemDark = isSystemInDarkTheme()
    val darkTheme = when (themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> systemDark
    }

    // 配色方案：自定义种子 > 动态色 > 回退
    val colorScheme = when {
        customThemeSeedHex.isNotBlank() -> seedColorScheme(customThemeSeedHex, darkTheme)
            ?: if (darkTheme) FallbackDarkScheme else FallbackLightScheme
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> FallbackDarkScheme
        else -> FallbackLightScheme
    }

    // X/O 棋子颜色：index < 0 用自定义 hex，否则取预设
    val xPieceColor = if (xColorIndex < 0) parseHex(customXColorHex) else null
    val oPieceColor = if (oColorIndex < 0) parseHex(customOColorHex) else null
    val xPreset = X_COLOR_PRESETS.getOrElse(xColorIndex) { X_COLOR_PRESETS[0] }
    val oPreset = O_COLOR_PRESETS.getOrElse(oColorIndex) { O_COLOR_PRESETS[0] }

    val pieceColors = if (darkTheme) {
        PieceColors(
            xPiece = xPieceColor ?: xPreset.darkColor,
            oPiece = oPieceColor ?: oPreset.darkColor,
            xBackground = xPieceColor?.copy(alpha = 0.15f) ?: xPreset.darkBg,
            oBackground = oPieceColor?.copy(alpha = 0.15f) ?: oPreset.darkBg,
            dotColor = Color(0xFF6B7280),
        )
    } else {
        PieceColors(
            xPiece = xPieceColor ?: xPreset.lightColor,
            oPiece = oPieceColor ?: oPreset.lightColor,
            xBackground = xPieceColor?.copy(alpha = 0.12f) ?: xPreset.lightBg,
            oBackground = oPieceColor?.copy(alpha = 0.12f) ?: oPreset.lightBg,
            dotColor = DotGray,
        )
    }

    CompositionLocalProvider(
        LocalPieceColors provides pieceColors,
        LocalStrings provides strings,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content,
        )
    }
}
