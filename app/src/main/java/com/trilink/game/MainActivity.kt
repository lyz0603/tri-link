package com.trilink.game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
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

    // 系统返回键 / 手势返回（Android 14+ 预见式返回动画由 manifest enableOnBackInvokedCallback 开启）
    androidx.activity.compose.BackHandler(enabled = screen != Screen.Main) {
        screen = Screen.Main
    }

    AnimatedContent(
        targetState = screen,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        transitionSpec = {
            val isForward = targetState != Screen.Main
            val duration = 320
            if (isForward) {
                // 进入 Rules/Settings：从右滑入
                (slideInHorizontally(tween(duration)) { it } + fadeIn(tween(duration / 2)))
                    .togetherWith(slideOutHorizontally(tween(duration)) { -it / 3 } + fadeOut(tween(duration / 3)))
            } else {
                // 返回 Main：从左滑入
                (slideInHorizontally(tween(duration)) { -it / 3 } + fadeIn(tween(duration / 2)))
                    .togetherWith(slideOutHorizontally(tween(duration)) { it } + fadeOut(tween(duration / 3)))
            }
        },
        label = "screen",
    ) { currentScreen ->
        when (currentScreen) {
            Screen.Rules -> {
                RulesScreen(onBack = { screen = Screen.Main })
            }
            Screen.Settings -> {
                val currentSettings by settingsRepo.settings.collectAsState()
                SettingsScreen(
                    settings = currentSettings,
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
                    onBack = { screen = Screen.Main },
                )
            }
            Screen.Main -> when (val current = phase) {
                is GamePhase.Setup -> SetupScreen(
                    onStartGame = { piece, isFirst -> viewModel.startGame(piece, isFirst) },
                    onShowRules = { screen = Screen.Rules },
                    onShowSettings = { screen = Screen.Settings },
                )
                is GamePhase.Playing -> GameScreen(
                    board = current.board,
                    playerPiece = current.playerPiece,
                    aiPiece = current.aiPiece,
                    isPlayerFirst = current.isPlayerFirst,
                    aiThinking = current.aiThinking,
                    message = current.message,
                    onPlayerMove = { row, col -> viewModel.playerMove(row, col) },
                    onNewGame = { viewModel.backToSetup() },
                )
                is GamePhase.GameOver -> GameScreen(
                    board = current.board,
                    playerPiece = current.playerPiece,
                    aiPiece = current.aiPiece,
                    isPlayerFirst = current.isPlayerFirst,
                    aiThinking = false,
                    message = current.resultText,
                    onPlayerMove = { _, _ -> },
                    onNewGame = { viewModel.backToSetup() },
                )
            }
        }
    }
}
