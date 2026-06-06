package com.trilink.game.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.trilink.game.ui.theme.LocalPieceColors

/**
 * 游戏状态栏 — 使用 AssistChip + Surface，Material 3 风格。
 */
@Composable
fun StatusBar(
    playerPiece: Char,
    aiPiece: Char,
    isPlayerFirst: Boolean,
    isPlayerTurn: Boolean,
    isGameOver: Boolean,
    isAiThinking: Boolean,
    emptyCount: Int,
    message: String,
    modifier: Modifier = Modifier,
) {
    val pieceColors = LocalPieceColors.current

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 第一行：玩家/AI 标签 + 回合状态
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // 左侧：你 X · AI O
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    PieceChip(label = "你", piece = playerPiece)
                    Text(
                        text = "vs",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    PieceChip(label = "AI", piece = aiPiece)
                }

                // 右侧：回合状态
                val statusText = when {
                    isGameOver -> "游戏结束"
                    isAiThinking -> "AI 思考中…"
                    isPlayerTurn -> "轮到你了"
                    else -> "AI 思考中…"
                }
                val statusColor = animateStatusColor(
                    isGameOver = isGameOver,
                    isPlayerTurn = isPlayerTurn,
                )

                Text(
                    text = statusText,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = statusColor,
                )
            }

            // 第二行：信息 + 空格数
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = when {
                        isPlayerFirst -> "先手"
                        else -> "后手"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "剩余 $emptyCount 格",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }

    // 消息行（错误/提示/结果）
    if (message.isNotEmpty()) {
        val messageColor = when {
            message.contains("胜") -> MaterialTheme.colorScheme.primary
            message.contains("结束") -> MaterialTheme.colorScheme.onSurface
            message.contains("无效") || message.contains("已有") || message.contains("等待") ->
                MaterialTheme.colorScheme.error
            else -> MaterialTheme.colorScheme.onSurface
        }
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
        ) {
            Text(
                text = message,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = messageColor,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun PieceChip(label: String, piece: Char) {
    val pieceColors = LocalPieceColors.current
    val (chipColor, chipBg) = when (piece) {
        'X' -> pieceColors.xPiece to pieceColors.xBackground
        'O' -> pieceColors.oPiece to pieceColors.oBackground
        else -> MaterialTheme.colorScheme.onSurface to MaterialTheme.colorScheme.surface
    }

    AssistChip(
        onClick = {},
        label = {
            Text(
                text = "$label 执 $piece",
                style = MaterialTheme.typography.labelLarge,
                color = chipColor,
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = chipBg,
        ),
        shape = MaterialTheme.shapes.small,
    )
}

// ─── 动画辅助 ──────────────────────────────────────────────────────────────────

@Composable
private fun animateStatusColor(isGameOver: Boolean, isPlayerTurn: Boolean): androidx.compose.ui.graphics.Color {
    val targetColor = when {
        isGameOver -> MaterialTheme.colorScheme.error
        isPlayerTurn -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.tertiary
    }
    val animated by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(400),
        label = "statusColor",
    )
    return animated
}
