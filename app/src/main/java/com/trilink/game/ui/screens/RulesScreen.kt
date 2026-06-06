package com.trilink.game.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RulesScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("规则与算法") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←", style = MaterialTheme.typography.titleLarge)
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
        ) {
            // ── 棋盘规则 ──
            SectionTitle("棋盘")
            SectionBody(
                "6×6 = 36 格棋盘。双方轮流落子，不可覆盖已有棋子，直到下满为止。"
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ── 三连规则 ──
            SectionTitle("三连")
            SectionBody(
                "棋盘上所有长度 ≥ 3 的连续线段共 32 条：\n" +
                "• 6 条横线（每行 6 格）\n" +
                "• 6 条竖线（每列 6 格）\n" +
                "• 10 条主对角线（↘ 方向）\n" +
                "• 10 条反对角线（↙ 方向）"
            )
            Spacer(modifier = Modifier.height(8.dp))
            SectionBody(
                "每条线段上，连续同色棋子 ≥ 3 颗即构成三连。\n" +
                "长连拆分规则：\n" +
                "• 3 连 = 1 分\n" +
                "• 4 连 = 2 分（拆为 2 个三连）\n" +
                "• 5 连 = 3 分（拆为 3 个三连）\n" +
                "• 6 连 = 4 分（拆为 4 个三连）"
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ── 胜负判定 ──
            SectionTitle("胜负判定")
            SectionBody(
                "棋盘下满后，分别统计双方在 32 条线段上的三连总数，" +
                "多者获胜，相等则平局。"
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ── AI 算法 ──
            SectionTitle("AI 算法")
            SectionBody(
                "采用迭代加深 Alpha-Beta 剪枝搜索："
            )

            Spacer(modifier = Modifier.height(8.dp))

            AlgorithmCard(
                title = "迭代加深 (Iterative Deepening)",
                body = "从深度 1 开始，逐层增加搜索深度。每完成一层检查是否超时，" +
                       "超时则返回当前最佳结果。保证在时限内总能给出一个合法走法。",
            )

            AlgorithmCard(
                title = "Alpha-Beta 剪枝",
                body = "经典的博弈树剪枝算法。AI 层最大化估值，玩家层最小化估值。" +
                       "通过 α-β 窗口剔除不可能被选中的分支，大幅减少搜索节点数。",
            )

            AlgorithmCard(
                title = "根节点并行",
                body = "根节点的每个候选走法独立搜索，使用 Kotlin 协程分配到多个 " +
                       "CPU 核心并行计算，充分利用现代手机的多核性能。",
            )

            AlgorithmCard(
                title = "启发式评估",
                body = "终局：精确计算三连差值。\n" +
                       "中局：三连差值 × 20 + 2-of-3 威胁（±3）+ 1-of-3 潜力（±1）。" +
                       "走法按中心优先排序以提升剪枝效率。",
            )

            AlgorithmCard(
                title = "时限控制",
                body = "单步搜索时限 3 秒。每次递归入口和循环内部均检查截止时间，" +
                       "超时立即返回，确保 UI 不卡顿。",
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ── 技术栈 ──
            SectionTitle("技术栈")
            SectionBody(
                "• Kotlin + Jetpack Compose (Material Design 3)\n" +
                "• MVVM 架构 (ViewModel + StateFlow)\n" +
                "• Kotlin Coroutines 异步搜索\n" +
                "• Android 12+ Dynamic Color 动态配色\n" +
                "• 最低支持 Android 8.0 (API 26)"
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ─── 小型辅助组件 ──────────────────────────────────────────────────────────────

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
    )
    Spacer(modifier = Modifier.height(6.dp))
}

@Composable
private fun SectionBody(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface,
        lineHeight = MaterialTheme.typography.bodyMedium.lineHeight,
    )
}

@Composable
private fun AlgorithmCard(title: String, body: String) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = body,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = MaterialTheme.typography.bodySmall.lineHeight,
            )
        }
    }
}
