package com.trilink.game.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.trilink.game.engine.countEmpty
import com.trilink.game.engine.countThrees
import com.trilink.game.engine.isGameOver
import com.trilink.game.engine.isPlayerTurn
import com.trilink.game.ui.components.BoardGrid
import com.trilink.game.ui.components.StatusBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    board: CharArray,
    playerPiece: Char,
    aiPiece: Char,
    isPlayerFirst: Boolean,
    aiThinking: Boolean,
    message: String,
    onPlayerMove: (Int, Int) -> Unit,
    onNewGame: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val over = isGameOver(board)
    val playerTurn = !over && !aiThinking && isPlayerTurn(board, isPlayerFirst)
    val empty = countEmpty(board)

    val title = when {
        over -> "对局结束"
        aiThinking -> "AI 思考中…"
        else -> "三连棋"
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // ── 状态栏 ──
            StatusBar(
                playerPiece = playerPiece,
                aiPiece = aiPiece,
                isPlayerFirst = isPlayerFirst,
                isPlayerTurn = playerTurn,
                isGameOver = over,
                isAiThinking = aiThinking,
                emptyCount = empty,
                message = message,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── 终局结果卡片（带动画）──
            AnimatedVisibility(
                visible = over,
                enter = fadeIn() + scaleIn() + slideInVertically(),
            ) {
                val playerThrees = countThrees(board, playerPiece)
                val aiThrees = countThrees(board, aiPiece)
                val (resultEmoji, resultText) = when {
                    playerThrees > aiThrees -> Pair("🎉", "你赢了！")
                    aiThrees > playerThrees -> Pair("🤖", "AI 赢了")
                    else -> Pair("🤝", "平局")
                }

                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "$resultEmoji $resultText",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "你: $playerThrees 三连    AI: $aiThrees 三连",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // ── 棋盘 ──
            BoardGrid(
                board = board,
                enabled = playerTurn,
                onCellClick = { r, c -> onPlayerMove(r, c) },
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ── 新游戏按钮 ──
            ElevatedButton(
                onClick = onNewGame,
                modifier = Modifier.height(52.dp),
                shape = MaterialTheme.shapes.large,
            ) {
                Text(
                    text = "新游戏",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
