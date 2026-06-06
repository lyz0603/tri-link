package com.trilink.game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    var showRules by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }

    if (showRules) {
        RulesScreen(onBack = { showRules = false })
        return
    }

    if (showSettings) {
        val currentSettings by settingsRepo.settings.collectAsState()
        SettingsScreen(
            settings = currentSettings,
            onUpdateAiTimeLimit = settingsRepo::updateAiTimeLimit,
            onUpdateAiThreads = settingsRepo::updateAiThreads,
            onUpdateXColor = settingsRepo::updateXColor,
            onUpdateOColor = settingsRepo::updateOColor,
            onUpdateThemeMode = settingsRepo::updateThemeMode,
            onUpdateDynamicColor = settingsRepo::updateDynamicColor,
            onUpdateLanguage = settingsRepo::updateLanguage,
            onBack = { showSettings = false },
        )
        return
    }

    when (val current = phase) {
        is GamePhase.Setup -> {
            SetupScreen(
                onStartGame = { piece, isFirst -> viewModel.startGame(piece, isFirst) },
                onShowRules = { showRules = true },
                onShowSettings = { showSettings = true },
            )
        }
        is GamePhase.Playing -> {
            GameScreen(
                board = current.board,
                playerPiece = current.playerPiece,
                aiPiece = current.aiPiece,
                isPlayerFirst = current.isPlayerFirst,
                aiThinking = current.aiThinking,
                message = current.message,
                onPlayerMove = { row, col -> viewModel.playerMove(row, col) },
                onNewGame = { viewModel.backToSetup() },
            )
        }
        is GamePhase.GameOver -> {
            GameScreen(
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
