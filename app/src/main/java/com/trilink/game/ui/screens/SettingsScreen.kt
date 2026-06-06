package com.trilink.game.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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
import com.trilink.game.ui.theme.parseHex

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    settings: GameSettings,
    onUpdateAiTimeLimit: (Int) -> Unit,
    onUpdateAiThreads: (Int) -> Unit,
    onUpdateXColor: (Int) -> Unit,
    onUpdateOColor: (Int) -> Unit,
    onUpdateCustomXColor: (String) -> Unit,
    onUpdateCustomOColor: (String) -> Unit,
    onUpdateThemeMode: (ThemeMode) -> Unit,
    onUpdateDynamicColor: (Boolean) -> Unit,
    onUpdateCustomThemeSeed: (String) -> Unit,
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = MaterialTheme.colorScheme.onSurface)
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding)
                .verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 12.dp),
        ) {
            // ── AI ──
            SectionHeader(s.aiSearch)
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val ti = TIME_LIMIT_PRESETS.indexOf(settings.aiTimeLimitMs / 1000).coerceAtLeast(0)
                    Text(s.searchTime, style = MaterialTheme.typography.titleSmall)
                    Text("${settings.aiTimeLimitMs / 1000} ${s.seconds}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(4.dp))
                    Slider(value = ti.toFloat(), onValueChange = { onUpdateAiTimeLimit(TIME_LIMIT_PRESETS[it.toInt()] * 1000) }, valueRange = 0f..(TIME_LIMIT_PRESETS.size - 1).toFloat(), steps = TIME_LIMIT_PRESETS.size - 2)
                    Spacer(Modifier.height(16.dp))
                    Text(s.threads, style = MaterialTheme.typography.titleSmall)
                    Text(if (settings.aiThreads == 0) "${s.threadsAuto} (${Runtime.getRuntime().availableProcessors()} ${s.threadsCores})" else s.threadsN(settings.aiThreads), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(4.dp))
                    Slider(value = THREAD_PRESETS.indexOf(settings.aiThreads).coerceAtLeast(0).toFloat(), onValueChange = { onUpdateAiThreads(THREAD_PRESETS[it.toInt()]) }, valueRange = 0f..(THREAD_PRESETS.size - 1).toFloat(), steps = THREAD_PRESETS.size - 2)
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── 外观 ──
            SectionHeader(s.appearance)

            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(s.themeMode, style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ThemeMode.entries.forEach { mode ->
                            FilterChip(settings.themeMode == mode, { onUpdateThemeMode(mode) }, label = { Text(when(mode){ThemeMode.LIGHT->s.themeLight;ThemeMode.DARK->s.themeDark;ThemeMode.SYSTEM->s.themeSystem}) })
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) { Text(s.dynamicColor, style = MaterialTheme.typography.titleSmall); Text(s.dynamicColorDesc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                    Switch(settings.dynamicColor, onUpdateDynamicColor)
                }
            }

            Spacer(Modifier.height(12.dp))

            // ── 主题种子色（仅关闭动态配色后可用）──
            AnimatedVisibility(
                visible = !settings.dynamicColor,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
            ) {
                Column {
                    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(s.customThemeSeed, style = MaterialTheme.typography.titleSmall)
                            Text(s.customThemeSeedDesc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.height(8.dp))
                            HexInput(settings.customThemeSeedHex, onUpdateCustomThemeSeed, "3B5CB8")
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }
            }

            // 语言
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(s.languageLabel, style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Language.entries.forEach { lang ->
                            FilterChip(settings.language == lang, { onUpdateLanguage(lang) }, label = { Text(lang.label) })
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // ── 棋子颜色 ──
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(s.xColor, style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.height(6.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        X_COLOR_PRESETS.forEachIndexed { idx, preset ->
                            ColorDot(s.xColorNames[idx], preset.lightColor, settings.xColorIndex == idx) { onUpdateXColor(idx); onUpdateCustomXColor("") }
                        }
                        ColorDot("…", androidx.compose.ui.graphics.Color.Transparent, settings.xColorIndex < 0) { onUpdateXColor(-1) }
                    }
                    if (settings.xColorIndex < 0) {
                        Spacer(Modifier.height(8.dp))
                        HexInput(settings.customXColorHex, onUpdateCustomXColor, "1A56DB")
                    }

                    Spacer(Modifier.height(12.dp))
                    Text(s.oColor, style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.height(6.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        O_COLOR_PRESETS.forEachIndexed { idx, preset ->
                            ColorDot(s.oColorNames[idx], preset.lightColor, settings.oColorIndex == idx) { onUpdateOColor(idx); onUpdateCustomOColor("") }
                        }
                        ColorDot("…", androidx.compose.ui.graphics.Color.Transparent, settings.oColorIndex < 0) { onUpdateOColor(-1) }
                    }
                    if (settings.oColorIndex < 0) {
                        Spacer(Modifier.height(8.dp))
                        HexInput(settings.customOColorHex, onUpdateCustomOColor, "DC2626")
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── 关于 ──
            SectionHeader(s.about)
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    AboutRow(s.aboutApp, s.appTitle)
                    AboutRow(s.aboutVersion, "v1.2")
                    AboutRow(s.aboutLicense, "MIT")
                    AboutRow(s.aboutPlatform, "Android 8.0+")
                    AboutRow(s.aboutTech, "Kotlin · Compose · Material 3")
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

// ─── Hex 输入组件 ──────────────────────────────────────────────────────────────

@Composable
private fun HexInput(value: String, onValueChange: (String) -> Unit, placeholder: String) {
    val focus = LocalFocusManager.current
    val preview = parseHex(value)
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("#", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = { if (it.length <= 6 && it.all { c -> c in "0123456789ABCDEFabcdef" }) onValueChange(it.uppercase()) },
            placeholder = { Text(placeholder, style = MaterialTheme.typography.bodySmall) },
            singleLine = true,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focus.clearFocus() }),
        )
        if (preview != null) {
            Spacer(Modifier.width(10.dp))
            Box(Modifier.size(28.dp).background(preview, CircleShape).border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape))
        }
    }
}

// ─── 辅助组件 ──────────────────────────────────────────────────────────────────

@Composable
private fun SectionHeader(title: String) {
    Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(bottom = 8.dp))
}

@Composable
private fun ColorDot(name: String, color: androidx.compose.ui.graphics.Color, selected: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(onClick = onClick).padding(6.dp)) {
        Box(
            modifier = Modifier.size(28.dp)
                .then(if (name == "…" && !selected) Modifier.border(1.5.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape) else Modifier)
                .background(if (name == "…" && !selected) androidx.compose.ui.graphics.Color.Transparent else color, CircleShape)
                .then(if (selected) Modifier.border(3.dp, MaterialTheme.colorScheme.primary, CircleShape) else if (name != "…") Modifier.border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape) else Modifier)
        )
        Spacer(Modifier.height(4.dp))
        Text(name, fontSize = 11.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal, color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun AboutRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}
