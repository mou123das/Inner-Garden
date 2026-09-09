package com.innergarden.app.feature.insights

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.innergarden.app.ui.components.GardenCard
import com.innergarden.app.ui.components.SectionHeader
import com.innergarden.app.ui.theme.PrimaryGreen

@Composable
fun InsightsScreen(viewModel: InsightsViewModel, modifier: Modifier = Modifier) {
    val state = viewModel.uiState.collectAsStateWithLifecycle().value
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionHeader("Insights", "Small patterns from your daily check-ins")
        Spacer(Modifier.height(2.dp)); Text("Your last 7 days", style = MaterialTheme.typography.titleLarge)
        state.metrics.chunked(2).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { metric -> MetricCard(metric, Modifier.weight(1f)) }
            }
        }
        GardenCard(Modifier.fillMaxWidth(), MaterialTheme.colorScheme.primaryContainer) {
            Text("Gentle observation", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp)); Text(state.observation)
        }
    }
}

@Composable private fun MetricCard(metric: InsightMetric, modifier: Modifier) {
    GardenCard(modifier) {
        Text(metric.label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(metric.value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))
        Canvas(Modifier.fillMaxWidth().height(36.dp)) {
            val step = size.width / (metric.points.size - 1)
            metric.points.zipWithNext().forEachIndexed { index, (a, b) ->
                drawLine(PrimaryGreen, Offset(index * step, size.height * (1 - a)), Offset((index + 1) * step, size.height * (1 - b)), 5f, StrokeCap.Round)
            }
        }
    }
}
