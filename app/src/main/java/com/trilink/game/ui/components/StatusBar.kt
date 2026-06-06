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
import com.trilink.game.ui.theme.LocalStrings

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
    val s = LocalStrings.current
    val pieceColors = LocalPieceColors.current

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    PieceChip(label = s.you, piece = playerPiece)
                    Text(s.vs, style = MaterialTheme.typography.labelSmall,
                         color = MaterialTheme.colorScheme.onSurfaceVariant)
                    PieceChip(label = s.ai, piece = aiPiece)
                }

                val statusText = when {
                    isGameOver -> s.gameOver
                    isAiThinking -> s.aiThinking
                    isPlayerTurn -> s.yourTurn
                    else -> s.aiThinking
                }
                val statusColor = animateStatusColor(isGameOver, isPlayerTurn)

                Text(
                    text = statusText,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = statusColor,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = if (isPlayerFirst) s.firstHand else s.secondHand,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "${s.remaining} $emptyCount",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }

    if (message.isNotEmpty()) {
        val msgColor = when {
            message.contains(s.youWin.dropLast(1).take(2)) -> MaterialTheme.colorScheme.primary
            message.contains(s.gameOver) -> MaterialTheme.colorScheme.onSurface
            message == s.invalidPos || message == s.cellOccupied || message == s.waitAi ->
                MaterialTheme.colorScheme.error
            else -> MaterialTheme.colorScheme.onSurface
        }
        Surface(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
        ) {
            Text(
                text = message,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = msgColor,
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
        label = { Text("$label $piece", style = MaterialTheme.typography.labelLarge, color = chipColor) },
        colors = AssistChipDefaults.assistChipColors(containerColor = chipBg),
        shape = MaterialTheme.shapes.small,
    )
}

@Composable
private fun animateStatusColor(isGameOver: Boolean, isPlayerTurn: Boolean): androidx.compose.ui.graphics.Color {
    val target = when {
        isGameOver -> MaterialTheme.colorScheme.error
        isPlayerTurn -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.tertiary
    }
    val animated by animateColorAsState(target, tween(400), label = "statusColor")
    return animated
}
