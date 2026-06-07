package com.trilink.game.engine

import android.util.Log
import kotlin.math.max

private const val TAG = "TriLink.AI2"

/**
 * 方向枚举权重算法 — 适配 6×6 棋盘。
 * 对每个空格的四个方向分析遮挡情况，计算权重 (0~12)，选最高分落子。
 * 翻译自 index.html 的 AI 权重模块。
 */

private val DIRS = arrayOf(
    intArrayOf(0, 1), intArrayOf(1, 0), intArrayOf(1, 1), intArrayOf(1, -1)
)

/** 计算 (x,y) 在 6×6 棋盘上的权重。已有棋子返回 -1。 */
fun calcWeight(x: Int, y: Int, board: CharArray, player: Char): Int {
    if (board[pos(x, y)] != '.') return -1

    val opp = if (player == 'X') 'O' else 'X'

    fun isBlocked(r: Int, c: Int): Boolean {
        if (r < 0 || r >= GRID || c < 0 || c >= GRID) return true
        return board[pos(r, c)] == opp
    }

    var a = 12

    for ((dr, dc) in DIRS) {
        val nearBlocked = isBlocked(x - dr, y - dc)
        val farBlocked  = isBlocked(x + dr, y + dc)
        if (nearBlocked && farBlocked) a -= 3
        else if (nearBlocked || farBlocked) a -= 2
    }

    for ((dr, dc) in DIRS) {
        val nr = x - 2 * dr; val nc = y - 2 * dc
        if (isBlocked(nr, nc)) {
            val mr = x - dr; val mc = y - dc
            if (mr in 0 until GRID && mc in 0 until GRID && board[pos(mr, mc)] != opp) a -= 1
        }
        val fr = x + 2 * dr; val fc = y + 2 * dc
        if (isBlocked(fr, fc)) {
            val mr = x + dr; val mc = y + dc
            if (mr in 0 until GRID && mc in 0 until GRID && board[pos(mr, mc)] != opp) a -= 1
        }
    }

    return max(0, a)
}

/** 权重算法 — 为 player 寻找最佳落子。6×6 范围扫描。 */
fun findBestMoveWeight(board: CharArray, player: Char): Int {
    var best = mutableListOf<Int>()
    var maxW = -1

    for (r in 0 until GRID) {
        for (c in 0 until GRID) {
            val w = calcWeight(r, c, board, player)
            if (w > maxW) {
                maxW = w
                best = mutableListOf(pos(r, c))
            } else if (w == maxW) {
                best.add(pos(r, c))
            }
        }
    }

    val index = if (maxW < 0 || best.isEmpty()) {
        board.indices.firstOrNull { board[it] == '.' } ?: -1
    } else {
        best.random()
    }

    Log.i(TAG, "权重法落子 (${index / GRID},${index % GRID}), maxW=$maxW, 候选数=${best.size}")
    return index
}
