package com.trilink.game.engine

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.abs
import kotlin.math.min

/**
 * AI 搜索模块：迭代加深 Alpha-Beta 剪枝 + 根节点协程并行。
 * 翻译自 ref/game.cpp 的搜索算法。
 */

private const val TAG = "TriLink.AI"

// ─── 走法排序 ──────────────────────────────────────────────────────────────────

/** 计算格子到棋盘中心 (2.5, 2.5) 的距离平方，用于中心优先排序 */
private fun centrality(pos: Int): Int {
    val r = pos / GRID
    val c = pos % GRID
    // (2*r - 5)² + (2*c - 5)²  以 (2.5, 2.5) 为中心
    val dr = 2 * r - 5
    val dc = 2 * c - 5
    return dr * dr + dc * dc
}

/** 按中心优先排序走法 */
private fun sortMoves(moves: MutableList<Int>) {
    moves.sortBy { centrality(it) }
}

// ─── Alpha-Beta 递归搜索 ──────────────────────────────────────────────────────

/**
 * 核心 Alpha-Beta 搜索。
 * @param maximizing true=AI层(最大化), false=玩家层(最小化)
 * @return 局面估值
 */
private fun alphaBeta(
    board: CharArray,
    depth: Int,
    alpha: Int,
    beta: Int,
    maximizing: Boolean,
    ai: Char,
    player: Char,
    deadlineNanos: Long,
    nodes: AtomicInteger,
    timedOut: AtomicBoolean,
): Int {
    nodes.incrementAndGet()

    // 超时/截止检查
    if (timedOut.get()) return 0
    if (System.nanoTime() >= deadlineNanos) {
        timedOut.set(true)
        return 0
    }

    val empty = countEmpty(board)

    // 终局：精确三连差值
    if (empty == 0) {
        return countThrees(board, ai) - countThrees(board, player)
    }

    // 到达深度限制：启发式评估
    if (depth == 0) {
        return evaluate(board, ai, player)
    }

    val moves = getValidMoves(board).toMutableList()
    sortMoves(moves)

    if (maximizing) {
        // AI 层 — 最大化
        var best = Int.MIN_VALUE / 2
        var localAlpha = alpha
        for (pos in moves) {
            board[pos] = ai
            val v = alphaBeta(
                board, depth - 1, localAlpha, beta, false,
                ai, player, deadlineNanos, nodes, timedOut
            )
            board[pos] = '.'
            if (v > best) best = v
            if (best > localAlpha) localAlpha = best
            if (localAlpha >= beta) break                     // β 剪枝
            if (timedOut.get()) break
            if (System.nanoTime() >= deadlineNanos) break
        }
        return if (best == Int.MIN_VALUE / 2) 0 else best
    } else {
        // 玩家层 — 最小化
        var best = Int.MAX_VALUE / 2
        var localBeta = beta
        for (pos in moves) {
            board[pos] = player
            val v = alphaBeta(
                board, depth - 1, alpha, localBeta, true,
                ai, player, deadlineNanos, nodes, timedOut
            )
            board[pos] = '.'
            if (v < best) best = v
            if (best < localBeta) localBeta = best
            if (alpha >= localBeta) break                     // α 剪枝
            if (timedOut.get()) break
            if (System.nanoTime() >= deadlineNanos) break
        }
        return if (best == Int.MAX_VALUE / 2) 0 else best
    }
}

// ─── 迭代加深搜索（入口）───────────────────────────────────────────────────────

/**
 * 迭代加深 Alpha-Beta 搜索。
 * 对根节点的每个走法使用协程并行搜索（多线程根节点并行）。
 *
 * @param board      当前棋盘（会被内部拷贝，不修改原数组）
 * @param ai         AI 棋子 'X' 或 'O'
 * @param player     玩家棋子
 * @param timeLimitMs 搜索时限（毫秒），默认 3000
 * @param numThreads 并行线程数，0 表示自动 = CPU 核心数
 * @return Pair(最佳位置索引, 估值)
 */
suspend fun iterativeDeepening(
    board: CharArray,
    ai: Char,
    player: Char,
    timeLimitMs: Int = 3000,
    numThreads: Int = 0,
): Pair<Int, Int> = coroutineScope {
    val startNanos = System.nanoTime()
    val deadlineNanos = startNanos + timeLimitMs * 1_000_000L

    val moves = getValidMoves(board).toMutableList()
    sortMoves(moves)

    if (moves.isEmpty()) return@coroutineScope Pair(-1, 0)

    // 空棋盘：随机落子（与参考实现一致）
    if (moves.size == BOARD_SIZE) {
        val pos = moves.random()
        val row = pos / GRID
        val col = pos % GRID
        Log.i(TAG, "空棋盘 - 随机落子 ($row,$col)")
        return@coroutineScope Pair(pos, 0)
    }

    val threads = if (numThreads <= 0) Runtime.getRuntime().availableProcessors() else numThreads

    var bestPos = moves[0]
    var bestVal = 0
    var finalDepth = 0
    val maxDepth = moves.size

    Log.i(TAG, "开始搜索... 时限 ${timeLimitMs}ms, 可选走法 ${moves.size}, 线程 $threads")

    for (depth in 1..maxDepth) {
        val depthStartNanos = System.nanoTime()
        if (depthStartNanos >= deadlineNanos) break

        val depthNodes = AtomicInteger(0)
        val timedOut = AtomicBoolean(false)

        var bestV = Int.MIN_VALUE / 2
        var depthBestPos = moves[0]

        if (threads <= 1) {
            // 单线程：顺序搜索根节点
            for (pos in moves) {
                if (timedOut.get()) break
                if (System.nanoTime() >= deadlineNanos) {
                    timedOut.set(true)
                    break
                }
                board[pos] = ai
                val v = alphaBeta(
                    board, depth - 1, Int.MIN_VALUE / 2, Int.MAX_VALUE / 2,
                    false, ai, player, deadlineNanos, depthNodes, timedOut
                )
                board[pos] = '.'
                if (v > bestV) { bestV = v; depthBestPos = pos }
            }
        } else {
            // 多线程：将根节点走法分片给协程并行搜索
            val boardSnapshot = board.copyOf()
            val batchSize = (moves.size + threads - 1) / threads

            val futures = (0 until threads).map { t ->
                val i0 = t * batchSize
                val i1 = min(i0 + batchSize, moves.size)
                if (i0 >= i1) return@map null

                async(Dispatchers.Default) {
                    val localBoard = boardSnapshot.copyOf()
                    var localBestV = Int.MIN_VALUE / 2
                    var localBestP = moves[i0]
                    for (i in i0 until i1) {
                        val pos = moves[i]
                        if (timedOut.get()) break
                        if (System.nanoTime() >= deadlineNanos) {
                            timedOut.set(true)
                            break
                        }
                        localBoard[pos] = ai
                        val v = alphaBeta(
                            localBoard, depth - 1, Int.MIN_VALUE / 2, Int.MAX_VALUE / 2,
                            false, ai, player, deadlineNanos, depthNodes, timedOut
                        )
                        localBoard[pos] = '.'
                        if (v > localBestV) { localBestV = v; localBestP = pos }
                    }
                    Pair(localBestP, localBestV)
                }
            }.filterNotNull()

            // 合并各线程结果
            for (f in futures) {
                val (pos, v) = f.await()
                if (v > bestV) { bestV = v; depthBestPos = pos }
            }
        }

        if (timedOut.get()) break

        bestVal = bestV
        bestPos = depthBestPos
        finalDepth = depth

        val elapsed = (System.nanoTime() - startNanos) / 1_000_000
        Log.i(TAG, "深度 $depth 完成, 最佳值 ${if (bestV >= 0) "+$bestV" else "$bestV"}, 节点数 ${depthNodes.get()}, 耗时 ${elapsed}ms")
    }

    val totalElapsed = (System.nanoTime() - startNanos) / 1_000_000
    val row = bestPos / GRID
    val col = bestPos % GRID
    Log.i(TAG, "搜索完成, 深度 $finalDepth, 最佳值 ${if (bestVal >= 0) "+$bestVal" else "$bestVal"}, 耗时 ${totalElapsed}ms, 落子 ($row,$col)")

    Pair(bestPos, bestVal)
}
