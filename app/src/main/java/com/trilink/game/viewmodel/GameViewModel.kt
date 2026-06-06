package com.trilink.game.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trilink.game.data.AIMode
import com.trilink.game.data.Strings
import com.trilink.game.data.ZhStrings
import com.trilink.game.engine.findBestMoveWeight
import com.trilink.game.engine.iterativeDeepening
import com.trilink.game.engine.isGameOver
import com.trilink.game.engine.newBoard
import com.trilink.game.engine.countThrees
import com.trilink.game.engine.countEmpty
import com.trilink.game.engine.isPlayerTurn
import com.trilink.game.engine.otherPiece
import com.trilink.game.engine.pos
import com.trilink.game.engine.cloneBoard
import com.trilink.game.engine.GRID
import com.trilink.game.engine.BOARD_SIZE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "TriLink.VM"

/**
 * 游戏阶段 sealed class。
 */
sealed class GamePhase {
    /** 初始选棋界面 */
    data object Setup : GamePhase()

    /** 对局中 */
    data class Playing(
        val board: CharArray,
        val playerPiece: Char,
        val aiPiece: Char,
        val isPlayerFirst: Boolean,
        val message: String,
        val aiThinking: Boolean,
    ) : GamePhase()

    /** 游戏结束 */
    data class GameOver(
        val board: CharArray,
        val playerPiece: Char,
        val aiPiece: Char,
        val isPlayerFirst: Boolean,
        val playerThrees: Int,
        val aiThrees: Int,
        val resultText: String,
    ) : GamePhase()
}

/**
 * 游戏状态容器。
 */
class GameViewModel(
    private val aiTimeLimitMs: Int = 3000,
    private val aiThreads: Int = 0,
    private val aiMode: AIMode = AIMode.ALPHA_BETA,
    private val s: Strings = ZhStrings,
) : ViewModel() {

    private val _phase = MutableStateFlow<GamePhase>(GamePhase.Setup)
    val phase: StateFlow<GamePhase> = _phase.asStateFlow()

    // ─── 公开方法 ────────────────────────────────────────────────────────────────

    /** 开始新游戏 */
    fun startGame(playerPiece: Char, isPlayerFirst: Boolean) {
        val aiPiece = otherPiece(playerPiece)
        val board = newBoard()
        Log.i(TAG, "=== 新游戏开始 ===")
        Log.i(TAG, "玩家执 $playerPiece, AI 执 $aiPiece, 玩家${if (isPlayerFirst) "先手" else "后手"}")

        if (isPlayerFirst) {
            _phase.value = GamePhase.Playing(
                board = board,
                playerPiece = playerPiece,
                aiPiece = aiPiece,
                isPlayerFirst = true,
                message = s.newGameStarted(playerPiece),
                aiThinking = false,
            )
        } else {
            // AI 先手
            _phase.value = GamePhase.Playing(
                board = board,
                playerPiece = playerPiece,
                aiPiece = aiPiece,
                isPlayerFirst = false,
                message = s.aiThinking,
                aiThinking = true,
            )
            runAiMove(board, aiPiece, playerPiece, isPlayerFirst = false)
        }
    }

    /** 玩家落子 */
    fun playerMove(row: Int, col: Int) {
        val current = _phase.value
        if (current !is GamePhase.Playing) return
        if (current.aiThinking) return

        val idx = pos(row, col)

        // 校验位置有效
        if (row !in 0 until GRID || col !in 0 until GRID) {
            _phase.value = current.copy(message = s.invalidPos)
            return
        }

        // 校验空格
        if (current.board[idx] != '.') {
            _phase.value = current.copy(message = s.cellOccupied)
            return
        }

        // 校验回合
        if (!isPlayerTurn(current.board, current.isPlayerFirst)) {
            _phase.value = current.copy(message = s.waitAi)
            return
        }

        // 执行玩家落子
        val newBoard = cloneBoard(current.board)
        newBoard[idx] = current.playerPiece
        Log.i(TAG, "玩家落子 ($row,$col)")
        Log.d(TAG, "当前棋盘:\n${boardToLog(newBoard)}")

        // 检查终局
        if (isGameOver(newBoard)) {
            endGame(newBoard, current.playerPiece, current.aiPiece, current.isPlayerFirst)
            return
        }

        // 触发 AI
        _phase.value = GamePhase.Playing(
            board = newBoard,
            playerPiece = current.playerPiece,
            aiPiece = current.aiPiece,
            isPlayerFirst = current.isPlayerFirst,
            message = s.aiThinking,
            aiThinking = true,
        )
        runAiMove(newBoard, current.aiPiece, current.playerPiece, current.isPlayerFirst)
    }

    /** 返回选棋界面 */
    fun backToSetup() {
        _phase.value = GamePhase.Setup
    }

    // ─── 内部方法 ────────────────────────────────────────────────────────────────

    /** 在协程中执行 AI 搜索 */
    private fun runAiMove(
        board: CharArray,
        aiPiece: Char,
        playerPiece: Char,
        isPlayerFirst: Boolean,
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            val bestPos = when (aiMode) {
                AIMode.WEIGHT -> findBestMoveWeight(board, aiPiece)
                AIMode.ALPHA_BETA -> iterativeDeepening(
                    board = board, ai = aiPiece, player = playerPiece,
                    timeLimitMs = aiTimeLimitMs, numThreads = aiThreads,
                ).first
            }

            if (bestPos < 0 || bestPos >= BOARD_SIZE) {
                // AI 无走法
                launch(Dispatchers.Main) {
                    _phase.value = (_phase.value as? GamePhase.Playing)?.copy(
                        message = s.aiCantMove,
                        aiThinking = false,
                    ) ?: return@launch
                }
                return@launch
            }

            val newBoard = cloneBoard(board)
            newBoard[bestPos] = aiPiece
            val aiRow = bestPos / GRID
            val aiCol = bestPos % GRID
            Log.i(TAG, "AI 落子 ($aiRow,$aiCol)")
            Log.d(TAG, "当前棋盘:\n${boardToLog(newBoard)}")

            launch(Dispatchers.Main) {
                if (isGameOver(newBoard)) {
                    endGame(newBoard, playerPiece, aiPiece, isPlayerFirst)
                } else {
                    val current = _phase.value as? GamePhase.Playing ?: return@launch
                    _phase.value = current.copy(
                        board = newBoard,
                        message = s.aiMoved(aiRow, aiCol),
                        aiThinking = false,
                    )
                }
            }
        }
    }

    /** 终局结算 */
    private fun endGame(
        board: CharArray,
        playerPiece: Char,
        aiPiece: Char,
        isPlayerFirst: Boolean,
    ) {
        val playerThrees = countThrees(board, playerPiece)
        val aiThrees = countThrees(board, aiPiece)
        val winner: Int = when {
            playerThrees > aiThrees -> 1
            aiThrees > playerThrees -> -1
            else -> 0
        }
        val resultText = s.gameEnded(playerThrees, aiThrees, winner)
        Log.i(TAG, "游戏结束。玩家三连: $playerThrees, AI三连: $aiThrees, winner=$winner")
        Log.i(TAG, "=== 本局结束 ===")

        _phase.value = GamePhase.GameOver(
            board = board,
            playerPiece = playerPiece,
            aiPiece = aiPiece,
            isPlayerFirst = isPlayerFirst,
            playerThrees = playerThrees,
            aiThrees = aiThrees,
            resultText = resultText,
        )
    }

    private fun boardToLog(board: CharArray): String = buildString {
        for (r in 0 until GRID) {
            for (c in 0 until GRID) {
                append(board[pos(r, c)])
            }
            if (r < GRID - 1) append('\n')
        }
    }
}
