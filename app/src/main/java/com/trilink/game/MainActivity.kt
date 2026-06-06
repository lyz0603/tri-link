package com.trilink.game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.BackEventCompat
import androidx.activity.compose.PredictiveBackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.trilink.game.data.SettingsRepository
import com.trilink.game.data.getStrings
import com.trilink.game.ui.screens.GameScreen
import com.trilink.game.ui.screens.RulesScreen
import com.trilink.game.ui.screens.SettingsScreen
import com.trilink.game.ui.screens.SetupScreen
import com.trilink.game.ui.theme.TrilinkTheme
import com.trilink.game.viewmodel.GamePhase
import com.trilink.game.viewmodel.GameViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val settingsRepo = SettingsRepository(applicationContext)

        setContent {
            val settings by settingsRepo.settings.collectAsState()

            TrilinkTheme(
                themeMode = settings.themeMode,
                dynamicColor = settings.dynamicColor,
                xColorIndex = settings.xColorIndex,
                oColorIndex = settings.oColorIndex,
                customXColorHex = settings.customXColorHex,
                customOColorHex = settings.customOColorHex,
                customThemeSeedHex = settings.customThemeSeedHex,
                language = settings.language,
            ) {
                TriLinkApp(
                    settingsRepo = settingsRepo,
                    settings = settings,
                )
            }
        }
    }
}

private enum class Screen { Main, Rules, Settings }

@Composable
fun TriLinkApp(
    settingsRepo: SettingsRepository,
    settings: com.trilink.game.data.GameSettings,
    viewModel: GameViewModel = viewModel(
        key = settings.language.name,
        factory = viewModelFactory {
            initializer {
                GameViewModel(
                    aiTimeLimitMs = settings.aiTimeLimitMs,
                    aiThreads = settings.aiThreads,
                    s = getStrings(settings.language),
                )
            }
        }
    ),
) {
    val phase by viewModel.phase.collectAsState()
    var screen by remember { mutableStateOf(Screen.Main) }
    var backProgress by remember { mutableFloatStateOf(0f) }
    val swipeOffset = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    // 正向导航（按钮点击）— 固定动画
    fun goTo(target: Screen) {
        scope.launch {
            screen = target
        }
    }

    // 预见式返回
    if (screen != Screen.Main) {
        PredictiveBackHandler { progress: Flow<BackEventCompat> ->
            try {
                progress.collect { event ->
                    backProgress = event.progress
                    swipeOffset.snapTo(event.progress)
                }
                // 手势提交
                scope.launch {
                    swipeOffset.animateTo(1f, tween((400 * (1f - swipeOffset.value)).roundToInt()))
                    screen = Screen.Main
                    swipeOffset.snapTo(0f)
                }
            } catch (_: CancellationException) {
                // 手势取消 — 弹回去
                scope.launch {
                    swipeOffset.animateTo(0f, tween(200))
                }
            }
        }
    }

    // 主内容 + 前景覆盖（被 swipe 的页面）
    Box(modifier = Modifier.fillMaxSize()) {
        // 底层：主页（始终渲染）
        when (val current = phase) {
            is GamePhase.Setup -> SetupScreen(
                onStartGame = { p, f -> viewModel.startGame(p, f) },
                onShowRules = { goTo(Screen.Rules) },
                onShowSettings = { goTo(Screen.Settings) },
            )
            is GamePhase.Playing -> GameScreen(
                board = current.board, playerPiece = current.playerPiece,
                aiPiece = current.aiPiece, isPlayerFirst = current.isPlayerFirst,
                aiThinking = current.aiThinking, message = current.message,
                onPlayerMove = { r, c -> viewModel.playerMove(r, c) },
                onNewGame = { viewModel.backToSetup() },
            )
            is GamePhase.GameOver -> GameScreen(
                board = current.board, playerPiece = current.playerPiece,
                aiPiece = current.aiPiece, isPlayerFirst = current.isPlayerFirst,
                aiThinking = false, message = current.resultText,
                onPlayerMove = { _, _ -> },
                onNewGame = { viewModel.backToSetup() },
            )
        }

        // 上层：Rules / Settings（被侧滑推出）
        if (screen != Screen.Main) {
            val offsetX = with(density) { (swipeOffset.value * 1080).toDp() } // 屏幕宽
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset { IntOffset(offsetX.roundToPx(), 0) }
                    .alpha(1f - swipeOffset.value * 0.3f)
                    .background(MaterialTheme.colorScheme.background),
            ) {
                when (screen) {
                    Screen.Rules -> RulesScreen(onBack = { goTo(Screen.Main) })
                    Screen.Settings -> {
                        val cur by settingsRepo.settings.collectAsState()
                        SettingsScreen(
                            settings = cur,
                            onUpdateAiTimeLimit = settingsRepo::updateAiTimeLimit,
                            onUpdateAiThreads = settingsRepo::updateAiThreads,
                            onUpdateXColor = settingsRepo::updateXColor,
                            onUpdateOColor = settingsRepo::updateOColor,
                            onUpdateCustomXColor = settingsRepo::updateCustomXColor,
                            onUpdateCustomOColor = settingsRepo::updateCustomOColor,
                            onUpdateThemeMode = settingsRepo::updateThemeMode,
                            onUpdateDynamicColor = settingsRepo::updateDynamicColor,
                            onUpdateCustomThemeSeed = settingsRepo::updateCustomThemeSeed,
                            onUpdateLanguage = settingsRepo::updateLanguage,
                            onBack = { goTo(Screen.Main) },
                        )
                    }
                    Screen.Main -> {}
                }
            }
        }
    }
}
