package com.trilink.game.engine

import android.util.Log
import kotlin.math.max

private const val TAG = "TriLink.AI2"

/**
 * 方向枚举权重算法。
 * 对每个空格的四个方向分析遮挡情况，计算权重 (0~12)，选最高分落子。
 * 翻译自 index.html 的 AI 权重模块。
 */

private val DIRS = arrayOf(
    intArrayOf(0, 1), intArrayOf(1, 0), intArrayOf(1, 1), intArrayOf(1, -1)
)

/** 计算 (x,y) 在给定棋盘上的权重。已有棋子返回 -1。 */
fun calcWeight(x: Int, y: Int, board: CharArray, player: Char): Int {
    if (board[x * 6 + y] != '.') return -1

    val opp = if (player == 'X') 'O' else 'X'

    fun isBlocked(r: Int, c: Int): Boolean {
        if (r < 0 || r >= 5 || c < 0 || c >= 5) return true
        return board[r * 6 + c] == opp
    }

    var a = 12

    // Step 1: 4 方向遮挡分析
    for ((dr, dc) in DIRS) {
        val nearBlocked = isBlocked(x - dr, y - dc)
        val farBlocked  = isBlocked(x + dr, y + dc)
        if (nearBlocked && farBlocked) a -= 3
        else if (nearBlocked || farBlocked) a -= 2
    }

    // Step 2: 远距离阻挡罚分（Chebyshev 距离 = 2）
    for ((dr, dc) in DIRS) {
        // Near side distance-2
        val nr = x - 2 * dr; val nc = y - 2 * dc
        if (isBlocked(nr, nc)) {
            val mr = x - dr; val mc = y - dc
            if (mr in 0..4 && mc in 0..4 && board[mr * 6 + mc] != opp) a -= 1
        }
        // Far side distance-2
        val fr = x + 2 * dr; val fc = y + 2 * dc
        if (isBlocked(fr, fc)) {
            val mr = x + dr; val mc = y + dc
            if (mr in 0..4 && mc in 0..4 && board[mr * 6 + mc] != opp) a -= 1
        }
    }

    return max(0, a)
}

/** 权重算法 — 为 player 寻找最佳落子。5×5 范围扫描。 */
fun findBestMoveWeight(board: CharArray, player: Char): Int {
    var best = mutableListOf<Int>()
    var maxW = -1

    for (r in 0 until 5) {
        for (c in 0 until 5) {
            val w = calcWeight(r, c, board, player)
            if (w > maxW) {
                maxW = w
                best = mutableListOf(r * 5 + c)
            } else if (w == maxW) {
                best.add(r * 5 + c)
            }
        }
    }

    val pos = if (maxW < 0 || best.isEmpty()) {
        // fallback: 第一个空格
        board.indices.firstOrNull { board[it] == '.' } ?: -1
    } else {
        best.random()
    }

    val row = pos / 5; val col = pos % 5
    Log.i(TAG, "权重法落子 ($row,$col), maxW=$maxW, 候选数=${best.size}")
    return pos
}
