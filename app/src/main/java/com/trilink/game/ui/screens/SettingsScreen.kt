package com.trilink.game.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trilink.game.data.GameSettings
import com.trilink.game.data.Language
import com.trilink.game.data.O_COLOR_PRESETS
import com.trilink.game.data.THREAD_PRESETS
import com.trilink.game.data.TIME_LIMIT_PRESETS
import com.trilink.game.data.ThemeMode
import com.trilink.game.data.X_COLOR_PRESETS
import com.trilink.game.ui.theme.LocalStrings

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    settings: GameSettings,
    onUpdateAiTimeLimit: (Int) -> Unit,
    onUpdateAiThreads: (Int) -> Unit,
    onUpdateXColor: (Int) -> Unit,
    onUpdateOColor: (Int) -> Unit,
    onUpdateThemeMode: (ThemeMode) -> Unit,
    onUpdateDynamicColor: (Boolean) -> Unit,
    onUpdateLanguage: (Language) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val s = LocalStrings.current

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(s.settingsTitle) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←", style = MaterialTheme.typography.titleLarge)
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
        ) {
            // ── AI ──
            SectionHeader(s.aiSearch)
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val timeIndex = TIME_LIMIT_PRESETS.indexOf(settings.aiTimeLimitMs / 1000).coerceAtLeast(0)
                    Text(s.searchTime, style = MaterialTheme.typography.titleSmall)
                    Text("${settings.aiTimeLimitMs / 1000} ${s.seconds}",
                         style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Slider(
                        value = timeIndex.toFloat(),
                        onValueChange = { onUpdateAiTimeLimit(TIME_LIMIT_PRESETS[it.toInt()] * 1000) },
                        valueRange = 0f..(TIME_LIMIT_PRESETS.size - 1).toFloat(),
                        steps = TIME_LIMIT_PRESETS.size - 2,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(s.threads, style = MaterialTheme.typography.titleSmall)
                    Text(
                        if (settings.aiThreads == 0) "${s.threadsAuto} (${Runtime.getRuntime().availableProcessors()} ${s.threadsCores})"
                        else s.threadsN(settings.aiThreads),
                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Slider(
                        value = THREAD_PRESETS.indexOf(settings.aiThreads).coerceAtLeast(0).toFloat(),
                        onValueChange = { onUpdateAiThreads(THREAD_PRESETS[it.toInt()]) },
                        valueRange = 0f..(THREAD_PRESETS.size - 1).toFloat(),
                        steps = THREAD_PRESETS.size - 2,
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── 外观 ──
            SectionHeader(s.appearance)

            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(s.themeMode, style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ThemeMode.entries.forEach { mode ->
                            FilterChip(
                                selected = settings.themeMode == mode,
                                onClick = { onUpdateThemeMode(mode) },
                                label = {
                                    Text(when (mode) {
                                        ThemeMode.LIGHT -> s.themeLight
                                        ThemeMode.DARK -> s.themeDark
                                        ThemeMode.SYSTEM -> s.themeSystem
                                    })
                                },
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(s.dynamicColor, style = MaterialTheme.typography.titleSmall)
                        Text(s.dynamicColorDesc, style = MaterialTheme.typography.bodySmall,
                             color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(checked = settings.dynamicColor, onCheckedChange = onUpdateDynamicColor)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 语言
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(s.languageLabel, style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Language.entries.forEach { lang ->
                            FilterChip(
                                selected = settings.language == lang,
                                onClick = { onUpdateLanguage(lang) },
                                label = { Text(lang.label) },
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 棋子颜色
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(s.xColor, style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        X_COLOR_PRESETS.forEachIndexed { idx, preset ->
                            ColorDot(s.xColorNames[idx], preset.lightColor, settings.xColorIndex == idx) { onUpdateXColor(idx) }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(s.oColor, style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        O_COLOR_PRESETS.forEachIndexed { idx, preset ->
                            ColorDot(s.oColorNames[idx], preset.lightColor, settings.oColorIndex == idx) { onUpdateOColor(idx) }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── 关于 ──
            SectionHeader(s.about)
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    AboutRow(s.aboutApp, s.appTitle)
                    AboutRow(s.aboutVersion, "v1.1")
                    AboutRow(s.aboutLicense, "MIT")
                    AboutRow(s.aboutPlatform, "Android 8.0+")
                    AboutRow(s.aboutTech, "Kotlin · Compose · Material 3")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Text(s.aboutAiDesc, style = MaterialTheme.typography.bodySmall,
                         color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ─── 辅助组件 ──────────────────────────────────────────────────────────────────

@Composable
private fun SectionHeader(title: String) {
    Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(bottom = 8.dp))
}

@Composable
private fun ColorDot(name: String, color: androidx.compose.ui.graphics.Color, selected: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally,
           modifier = Modifier.clickable(onClick = onClick).padding(6.dp)) {
        Box(
            modifier = Modifier.size(28.dp).background(color, CircleShape)
                .then(if (selected) Modifier.border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                      else Modifier.border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape))
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(name, fontSize = 11.sp,
             fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
             color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun AboutRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}
