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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.trilink.game.ui.theme.LocalStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RulesScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val s = LocalStrings.current

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(s.rulesTitle) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
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
            SectionTitle(s.rulesBoardTitle)
            SectionBody(s.rulesBoardBody)
            Spacer(modifier = Modifier.height(20.dp))

            SectionTitle(s.rulesThreesTitle)
            SectionBody(s.rulesThreesBody)
            Spacer(modifier = Modifier.height(8.dp))
            SectionBody(s.rulesScoring)
            Spacer(modifier = Modifier.height(20.dp))

            SectionTitle(s.rulesWinTitle)
            SectionBody(s.rulesWinBody)
            Spacer(modifier = Modifier.height(20.dp))

            SectionTitle(s.rulesAiTitle)
            SectionBody(s.rulesAiIntro)
            Spacer(modifier = Modifier.height(8.dp))
            AlgorithmCard(s.rulesIterative, s.rulesIterativeBody)
            AlgorithmCard(s.rulesAlphaBeta, s.rulesAlphaBetaBody)
            AlgorithmCard(s.rulesParallel, s.rulesParallelBody)
            AlgorithmCard(s.rulesEval, s.rulesEvalBody)
            AlgorithmCard(s.rulesTime, s.rulesTimeBody)
            AlgorithmCard(s.rulesWeight, s.rulesWeightBody)
            Spacer(modifier = Modifier.height(20.dp))

            SectionTitle(s.rulesTechTitle)
            SectionBody(s.rulesTechBody)
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

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
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
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
