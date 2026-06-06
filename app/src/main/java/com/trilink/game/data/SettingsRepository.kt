package com.trilink.game.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepository(context: Context) {

    private val prefs = context.getSharedPreferences("trilink_settings", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(load())
    val settings: StateFlow<GameSettings> = _settings.asStateFlow()

    private fun load(): GameSettings {
        return GameSettings(
            aiTimeLimitMs = prefs.getInt("ai_time_limit_ms", 3000),
            aiThreads = prefs.getInt("ai_threads", 0),
            xColorIndex = prefs.getInt("x_color_index", 0),
            oColorIndex = prefs.getInt("o_color_index", 0),
            customXColorHex = prefs.getString("custom_x_color", "") ?: "",
            customOColorHex = prefs.getString("custom_o_color", "") ?: "",
            themeMode = ThemeMode.valueOf(prefs.getString("theme_mode", "SYSTEM") ?: "SYSTEM"),
            dynamicColor = prefs.getBoolean("dynamic_color", true),
            customThemeSeedHex = prefs.getString("custom_theme_seed", "") ?: "",
            aiMode = AIMode.valueOf(prefs.getString("ai_mode", "ALPHA_BETA") ?: "ALPHA_BETA"),
            language = Language.valueOf(prefs.getString("language", "ZH") ?: "ZH"),
        )
    }

    fun updateAiTimeLimit(ms: Int) { save("ai_time_limit_ms", ms) { copy(aiTimeLimitMs = ms) } }
    fun updateAiThreads(threads: Int) { save("ai_threads", threads) { copy(aiThreads = threads) } }
    fun updateXColor(index: Int) { save("x_color_index", index) { copy(xColorIndex = index) } }
    fun updateOColor(index: Int) { save("o_color_index", index) { copy(oColorIndex = index) } }
    fun updateCustomXColor(hex: String) { saveStr("custom_x_color", hex) { copy(customXColorHex = hex) } }
    fun updateCustomOColor(hex: String) { saveStr("custom_o_color", hex) { copy(customOColorHex = hex) } }
    fun updateThemeMode(mode: ThemeMode) { saveStr("theme_mode", mode.name) { copy(themeMode = mode) } }
    fun updateDynamicColor(enabled: Boolean) { save("dynamic_color", enabled) { copy(dynamicColor = enabled) } }
    fun updateCustomThemeSeed(hex: String) { saveStr("custom_theme_seed", hex) { copy(customThemeSeedHex = hex) } }
    fun updateAiMode(mode: AIMode) { saveStr("ai_mode", mode.name) { copy(aiMode = mode) } }
    fun updateLanguage(lang: Language) { saveStr("language", lang.name) { copy(language = lang) } }

    private inline fun save(key: String, value: Int, crossinline update: GameSettings.() -> GameSettings) {
        prefs.edit().putInt(key, value).apply()
        _settings.value = _settings.value.update()
    }

    private inline fun save(key: String, value: Boolean, crossinline update: GameSettings.() -> GameSettings) {
        prefs.edit().putBoolean(key, value).apply()
        _settings.value = _settings.value.update()
    }

    private inline fun saveStr(key: String, value: String, crossinline update: GameSettings.() -> GameSettings) {
        prefs.edit().putString(key, value).apply()
        _settings.value = _settings.value.update()
    }
}
