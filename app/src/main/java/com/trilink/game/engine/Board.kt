package com.trilink.game.engine

/**
 * 棋盘常量与线段定义。
 * 内部棋盘为 6×6 = 36 格，row-major 存储，字符 '.' 空、'X'、'O'。
 */

const val BOARD_SIZE = 36
const val GRID = 6

/**
 * 32 条长度 ≥ 3 的线段。
 * 6横 + 6竖 + 10主对角线 + 10反对角线。
 * 索引为 0-35 的格子编号（row * 6 + col）。
 */
val LINES: List<IntArray> = listOf(
    // ── 横线 (6 × 长度6) ──
    intArrayOf(0, 1, 2, 3, 4, 5),
    intArrayOf(6, 7, 8, 9, 10, 11),
    intArrayOf(12, 13, 14, 15, 16, 17),
    intArrayOf(18, 19, 20, 21, 22, 23),
    intArrayOf(24, 25, 26, 27, 28, 29),
    intArrayOf(30, 31, 32, 33, 34, 35),

    // ── 竖线 (6 × 长度6) ──
    intArrayOf(0, 6, 12, 18, 24, 30),
    intArrayOf(1, 7, 13, 19, 25, 31),
    intArrayOf(2, 8, 14, 20, 26, 32),
    intArrayOf(3, 9, 15, 21, 27, 33),
    intArrayOf(4, 10, 16, 22, 28, 34),
    intArrayOf(5, 11, 17, 23, 29, 35),

    // ── 主对角线 (Top-Left → Bottom-Right) ──
    intArrayOf(0, 7, 14, 21, 28, 35),   // 长度6
    intArrayOf(1, 8, 15, 22, 29),       // 长度5
    intArrayOf(6, 13, 20, 27, 34),      // 长度5
    intArrayOf(2, 9, 16, 23),           // 长度4
    intArrayOf(7, 14, 21, 28),          // 长度4
    intArrayOf(12, 19, 26, 33),         // 长度4
    intArrayOf(3, 10, 17),              // 长度3
    intArrayOf(8, 15, 22),              // 长度3
    intArrayOf(13, 20, 27),             // 长度3
    intArrayOf(18, 25, 32),             // 长度3

    // ── 反对角线 (Top-Right → Bottom-Left) ──
    intArrayOf(5, 10, 15, 20, 25, 30),  // 长度6
    intArrayOf(4, 9, 14, 19, 24),       // 长度5
    intArrayOf(11, 16, 21, 26, 31),     // 长度5
    intArrayOf(3, 8, 13, 18),           // 长度4
    intArrayOf(10, 15, 20, 25),         // 长度4
    intArrayOf(17, 22, 27, 32),         // 长度4
    intArrayOf(2, 7, 12),               // 长度3
    intArrayOf(9, 14, 19),              // 长度3
    intArrayOf(16, 21, 26),             // 长度3
    intArrayOf(23, 28, 33),             // 长度3
)

/** 创建空棋盘 */
fun newBoard(): CharArray = CharArray(BOARD_SIZE) { '.' }

/** 深拷贝棋盘 */
fun cloneBoard(board: CharArray): CharArray = board.copyOf()

/** row * 6 + col */
fun pos(row: Int, col: Int): Int = row * GRID + col

/** 将棋盘打印为 6×6 字符串（日志用） */
fun boardToString(board: CharArray): String = buildString {
    for (r in 0 until GRID) {
        for (c in 0 until GRID) {
            append(board[pos(r, c)])
        }
        if (r < GRID - 1) append('\n')
    }
}
