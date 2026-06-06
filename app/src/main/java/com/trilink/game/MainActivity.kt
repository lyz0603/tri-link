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
import com.trilink.game.ui.screens.GameScreen
import com.trilink.game.ui.screens.RulesScreen
import com.trilink.game.ui.screens.SetupScreen
import com.trilink.game.ui.theme.TrilinkTheme
import com.trilink.game.viewmodel.GamePhase
import com.trilink.game.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TrilinkTheme {
                TriLinkApp()
            }
        }
    }
}

@Composable
fun TriLinkApp(
    viewModel: GameViewModel = viewModel(),
) {
    var showRules by remember { mutableStateOf(false) }
    val phase by viewModel.phase.collectAsState()

    if (showRules) {
        RulesScreen(onBack = { showRules = false })
        return
    }

    when (val current = phase) {
        is GamePhase.Setup -> {
            SetupScreen(
                onStartGame = { piece, isFirst -> viewModel.startGame(piece, isFirst) },
                onShowRules = { showRules = true },
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
