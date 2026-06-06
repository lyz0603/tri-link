package com.trilink.game.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trilink.game.engine.GRID
import com.trilink.game.engine.pos
import com.trilink.game.ui.theme.LocalPieceColors

/**
 * 6×6 棋盘网格 — Material 3 风格。
 */
@Composable
fun BoardGrid(
    board: CharArray,
    enabled: Boolean,
    onCellClick: (Int, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pieceColors = LocalPieceColors.current
    val shape = MaterialTheme.shapes.extraLarge

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = shape,
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
    ) {
        Column {
            for (row in 0 until GRID) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (col in 0 until GRID) {
                        val idx = pos(row, col)
                        val cell = board[idx]

                        val bgColor = when (cell) {
                            'X' -> pieceColors.xBackground
                            'O' -> pieceColors.oBackground
                            else -> MaterialTheme.colorScheme.surface
                        }
                        val fgColor = when (cell) {
                            'X' -> pieceColors.xPiece
                            'O' -> pieceColors.oPiece
                            else -> if (enabled) pieceColors.dotColor
                            else pieceColors.dotColor.copy(alpha = 0.25f)
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .then(
                                    if (cell == '.' && enabled) {
                                        Modifier.clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = ripple(bounded = true),
                                            onClick = { onCellClick(row, col) },
                                        )
                                    } else {
                                        Modifier
                                    }
                                )
                                .border(
                                    width = 0.5.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant,
                                )
                                .background(bgColor),
                            contentAlignment = Alignment.Center,
                        ) {
                            when (cell) {
                                'X' -> Text(
                                    text = "X",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = fgColor,
                                )
                                'O' -> Text(
                                    text = "O",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = fgColor,
                                )
                                else -> Text(
                                    text = "·",
                                    fontSize = 22.sp,
                                    color = fgColor,
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
