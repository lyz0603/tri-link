package com.trilink.game.engine

import android.util.Log

/**
 * 游戏核心逻辑：三连计数、启发评估、终局判断。
 * 直接翻译 ref/game.cpp 的算法。
 */

private const val TAG = "TriLink.Engine"

/**
 * 统计某个玩家在棋盘上的全局三连总数。
 * 对 32 条线段，每段内连续同色 ≥ 3 时，N 连 = N - 2 个三连。
 */
fun countThrees(board: CharArray, player: Char): Int {
    var total = 0
    for (line in LINES) {
        var run = 0
        for (idx in line) {
            if (board[idx] == player) {
                run++
            } else {
                if (run >= 3) total += run - 2
                run = 0
            }
        }
        if (run >= 3) total += run - 2
    }
    return total
}

/** 空格数量 */
fun countEmpty(board: CharArray): Int = board.count { it == '.' }

/** 棋盘已满时游戏结束 */
fun isGameOver(board: CharArray): Boolean = countEmpty(board) == 0

/** 获取所有空格位置的索引列表 */
fun getValidMoves(board: CharArray): List<Int> =
    board.indices.filter { board[it] == '.' }

/**
 * 启发式评估函数。从 AI 视角计分，正值对 AI 有利。
 *   base = (AI三连 - 玩家三连) × 20
 *   + 滑动窗口检测 2-of-3 威胁（±3）和 1-of-3 潜力（±1）
 */
fun evaluate(board: CharArray, ai: Char, player: Char): Int {
    val aiThrees = countThrees(board, ai)
    val playerThrees = countThrees(board, player)
    var score = (aiThrees - playerThrees) * 20

    for (line in LINES) {
        val len = line.size
        for (i in 0..len - 3) {
            var aiCnt = 0
            var plCnt = 0
            var empty = 0
            for (j in 0 until 3) {
                when (board[line[i + j]]) {
                    ai -> aiCnt++
                    player -> plCnt++
                    else -> empty++
                }
            }
            // 2-of-3 威胁
            if (plCnt == 0 && aiCnt == 2 && empty == 1) score += 3
            if (aiCnt == 0 && plCnt == 2 && empty == 1) score -= 3
            // 1-of-3 潜力
            if (plCnt == 0 && aiCnt == 1 && empty == 2) score += 1
            if (aiCnt == 0 && plCnt == 1 && empty == 2) score -= 1
        }
    }
    return score
}

/**
 * 判断当前是否是玩家回合。
 * 先手：空格偶数时落子（36,34,...,2,0）
 * 后手：空格奇数时落子（35,33,...,1）
 */
fun isPlayerTurn(board: CharArray, playerFirst: Boolean): Boolean {
    val empty = countEmpty(board)
    return if (playerFirst) empty % 2 == 0 else empty % 2 == 1
}

/** 对手棋子 */
fun otherPiece(piece: Char): Char = if (piece == 'X') 'O' else 'X'
