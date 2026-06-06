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
            themeMode = ThemeMode.valueOf(prefs.getString("theme_mode", "SYSTEM") ?: "SYSTEM"),
            dynamicColor = prefs.getBoolean("dynamic_color", true),
        )
    }

    fun updateAiTimeLimit(ms: Int) {
        prefs.edit().putInt("ai_time_limit_ms", ms).apply()
        _settings.value = _settings.value.copy(aiTimeLimitMs = ms)
    }

    fun updateAiThreads(threads: Int) {
        prefs.edit().putInt("ai_threads", threads).apply()
        _settings.value = _settings.value.copy(aiThreads = threads)
    }

    fun updateXColor(index: Int) {
        prefs.edit().putInt("x_color_index", index).apply()
        _settings.value = _settings.value.copy(xColorIndex = index)
    }

    fun updateOColor(index: Int) {
        prefs.edit().putInt("o_color_index", index).apply()
        _settings.value = _settings.value.copy(oColorIndex = index)
    }

    fun updateThemeMode(mode: ThemeMode) {
        prefs.edit().putString("theme_mode", mode.name).apply()
        _settings.value = _settings.value.copy(themeMode = mode)
    }

    fun updateDynamicColor(enabled: Boolean) {
        prefs.edit().putBoolean("dynamic_color", enabled).apply()
        _settings.value = _settings.value.copy(dynamicColor = enabled)
    }
}
