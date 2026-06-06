package com.trilink.game.data

import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.StateFlow

/** 主题模式 */
enum class ThemeMode { LIGHT, DARK, SYSTEM }

/** 棋子颜色预设 */
data class PieceColorPreset(
    val name: String,
    val lightColor: Color,
    val lightBg: Color,
    val darkColor: Color,
    val darkBg: Color,
)

/** X 棋子颜色预设 */
val X_COLOR_PRESETS = listOf(
    PieceColorPreset("蓝", Color(0xFF1A56DB), Color(0xFFDBEAFE), Color(0xFF93C5FD), Color(0xFF1E3A5F)),
    PieceColorPreset("绿", Color(0xFF16A34A), Color(0xFFDCFCE7), Color(0xFF86EFAC), Color(0xFF1A3D2A)),
    PieceColorPreset("紫", Color(0xFF7C3AED), Color(0xFFEDE9FE), Color(0xFFC4B5FD), Color(0xFF2D1B5A)),
    PieceColorPreset("橙", Color(0xFFEA580C), Color(0xFFFFF7ED), Color(0xFFFDBA74), Color(0xFF3D1F08)),
)

/** O 棋子颜色预设 */
val O_COLOR_PRESETS = listOf(
    PieceColorPreset("红", Color(0xFFDC2626), Color(0xFFFEE2E2), Color(0xFFFCA5A5), Color(0xFF3B1A1A)),
    PieceColorPreset("琥珀", Color(0xFFD97706), Color(0xFFFEF3C7), Color(0xFFFCD34D), Color(0xFF3D2808)),
    PieceColorPreset("青", Color(0xFF0891B2), Color(0xFFECFEFF), Color(0xFF67E8F9), Color(0xFF1A2D33)),
    PieceColorPreset("粉", Color(0xFFDB2777), Color(0xFFFCE7F3), Color(0xFFFDA4AF), Color(0xFF3B1A2A)),
)

/** 搜索时限预设（秒） */
val TIME_LIMIT_PRESETS = listOf(1, 3, 5, 10, 15, 30)

/** 线程数预设 */
val THREAD_PRESETS = listOf(0, 1, 2, 4, 8)  // 0 = 自动

/** 应用设置 */
data class GameSettings(
    val aiTimeLimitMs: Int = 3000,
    val aiThreads: Int = 0,
    val xColorIndex: Int = 0,
    val oColorIndex: Int = 0,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColor: Boolean = true,
    val language: Language = Language.ZH,
)
