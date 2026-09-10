package com.innergarden.app.feature.insights

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.innergarden.app.ui.components.BackHeader
import com.innergarden.app.ui.components.GardenCard
import com.innergarden.app.ui.components.InnerGardenButton
import com.innergarden.app.ui.theme.DeepForest
import com.innergarden.app.ui.theme.FreshGreen
import com.innergarden.app.ui.theme.GardenTextSecondary
import com.innergarden.app.ui.theme.Mint
import com.innergarden.app.ui.theme.MintSurface
import com.innergarden.app.ui.theme.MintSurfaceStrong
import com.innergarden.app.ui.theme.PaleMint
import com.innergarden.app.ui.theme.PrimaryGreen
import com.innergarden.app.ui.theme.SoftGardenSurface
import com.innergarden.app.ui.theme.SoftGreen
import com.innergarden.app.ui.theme.WarmSurface
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun InsightsScreen(viewModel: InsightsViewModel, onBackToGarden: () -> Unit, modifier: Modifier = Modifier) {
    val state = viewModel.uiState.collectAsStateWithLifecycle().value
    BackHandler(onBack = onBackToGarden)
    Column(
        modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        BackHeader("Insights", "A gentle look at your week", onBackToGarden)
        Spacer(Modifier.height(22.dp))
        when {
            state.isLoading -> GardenCard(Modifier.fillMaxWidth(), MintSurface) {
                Text("Your weekly garden is taking shape...", color = GardenTextSecondary)
            }
            state.errorMessage != null -> GardenCard(Modifier.fillMaxWidth(), SoftGardenSurface) {
                Text(state.errorMessage, color = GardenTextSecondary)
                Spacer(Modifier.height(12.dp))
                InnerGardenButton("Try again") { viewModel.onEvent(InsightsUiEvent.Retry) }
            }
            else -> InsightsGarden(state)
        }
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun InsightsGarden(state: InsightsUiState) {
    val orderedMetrics = remember(state.metrics) {
        val order = listOf("Mood", "Stress", "Energy", "Sleep")
        state.metrics.sortedBy { metric -> order.indexOf(metric.label).let { if (it == -1) Int.MAX_VALUE else it } }
    }
    val referencePoints = orderedMetrics.firstOrNull()?.points ?: emptyWeekPoints()
    val weeklyPoints = remember(orderedMetrics, referencePoints) {
        referencePoints.indices.map { index ->
            val values = orderedMetrics.mapNotNull { it.points.getOrNull(index)?.value }
            DailyMetricPoint(referencePoints[index].dayLabel, if (values.isEmpty()) null else values.average().toFloat())
        }
    }

    WeeklyBloomHero(weeklyPoints, state.isEmpty)
    if (orderedMetrics.isNotEmpty()) {
        Spacer(Modifier.height(28.dp))
        Text("Your wellbeing garden", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, color = DeepForest)
        Spacer(Modifier.height(14.dp))
        InsightsMetricGrid(orderedMetrics)
        Spacer(Modifier.height(24.dp))
    } else {
        Spacer(Modifier.height(24.dp))
    }
    GentleObservationCard(state.observation)
}

@Composable
private fun WeeklyBloomHero(points: List<DailyMetricPoint>, isEmpty: Boolean) {
    val recordedDays = points.count { it.value != null }
    GardenCard(
        Modifier.fillMaxWidth(),
        MintSurface,
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 22.dp)
    ) {
        Text(
            "THIS WEEK IN YOUR GARDEN",
            Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.labelLarge,
            color = PrimaryGreen,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(6.dp))
        WellbeingBloom(
            points,
            BloomStyle.WEEKLY,
            190.dp,
            0,
            if (recordedDays == 0) "Weekly wellbeing garden with no recorded check-ins yet"
            else "Weekly wellbeing garden with $recordedDays recorded ${if (recordedDays == 1) "day" else "days"}",
            Modifier.align(Alignment.CenterHorizontally)
        )
        WeekdayLabels(points)
        Spacer(Modifier.height(12.dp))
        Text(
            currentWeekRange(),
            Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodyMedium,
            color = GardenTextSecondary,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium
        )
        if (isEmpty) {
            Spacer(Modifier.height(12.dp))
            Text(
                "Your weekly garden will begin to take shape after your first check-in.",
                Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodyMedium,
                color = GardenTextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun WeekdayLabels(points: List<DailyMetricPoint>) {
    Row(Modifier.fillMaxWidth()) {
        points.forEach { point ->
            Text(point.dayLabel, Modifier.weight(1f), style = MaterialTheme.typography.bodySmall, color = GardenTextSecondary, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun InsightsMetricGrid(metrics: List<InsightMetric>) {
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        if (maxWidth >= 340.dp) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                metrics.chunked(2).forEachIndexed { rowIndex, rowMetrics ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        rowMetrics.forEachIndexed { columnIndex, metric ->
                            MetricBloomCard(metric, rowIndex * 2 + columnIndex, Modifier.weight(1f))
                        }
                        if (rowMetrics.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                metrics.forEachIndexed { index, metric -> MetricBloomCard(metric, index, Modifier.fillMaxWidth()) }
            }
        }
    }
}

@Composable
private fun MetricBloomCard(metric: InsightMetric, index: Int, modifier: Modifier = Modifier) {
    val style = when (metric.label) {
        "Mood" -> BloomStyle.MOOD
        "Stress" -> BloomStyle.STRESS
        "Energy" -> BloomStyle.ENERGY
        "Sleep" -> BloomStyle.SLEEP
        else -> BloomStyle.WEEKLY
    }
    val recordedDays = metric.points.count { it.value != null }
    val surface = when (style) {
        BloomStyle.MOOD -> WarmSurface
        BloomStyle.STRESS -> SoftGardenSurface
        BloomStyle.ENERGY -> MintSurface
        BloomStyle.SLEEP -> Color(0xFFF1F8F2)
        BloomStyle.WEEKLY -> WarmSurface
    }
    GardenCard(modifier, surface, contentPadding = PaddingValues(14.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(metric.label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = DeepForest)
            Text(metric.value, style = MaterialTheme.typography.bodyMedium, color = PrimaryGreen, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(6.dp))
        WellbeingBloom(
            metric.points,
            style,
            112.dp,
            90L + index * 80L,
            "${metric.label} average ${metric.value} over the last 7 days, $recordedDays recorded ${if (recordedDays == 1) "day" else "days"}",
            Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun GentleObservationCard(observation: String) {
    GardenCard(Modifier.fillMaxWidth(), MintSurfaceStrong, contentPadding = PaddingValues(20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Canvas(Modifier.size(28.dp)) {
                val leaf = Path().apply {
                    moveTo(size.width * .18f, size.height * .76f)
                    cubicTo(size.width * .12f, size.height * .20f, size.width * .70f, size.height * .10f, size.width * .82f, size.height * .18f)
                    cubicTo(size.width * .90f, size.height * .58f, size.width * .56f, size.height * .90f, size.width * .18f, size.height * .76f)
                    close()
                }
                drawPath(leaf, PrimaryGreen)
            }
            Spacer(Modifier.size(10.dp))
            Text("Gentle observation", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = DeepForest)
        }
        Spacer(Modifier.height(12.dp))
        Text(observation, style = MaterialTheme.typography.bodyLarge, color = DeepForest)
    }
}

private enum class BloomStyle { WEEKLY, MOOD, STRESS, ENERGY, SLEEP }

@Composable
private fun WellbeingBloom(
    points: List<DailyMetricPoint>,
    style: BloomStyle,
    bloomSize: Dp,
    animationDelayMillis: Long,
    accessibilityDescription: String,
    modifier: Modifier = Modifier
) {
    val reveal = remember(points, style) { Animatable(0f) }
    LaunchedEffect(points, style) {
        reveal.snapTo(0f)
        delay(animationDelayMillis)
        reveal.animateTo(1f, tween(720, easing = FastOutSlowInEasing))
    }
    val latestIndex = points.indexOfLast { it.value != null }
    Canvas(modifier.size(bloomSize).clearAndSetSemantics { contentDescription = accessibilityDescription }) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val minSide = size.minDimension
        val coreRadius = minSide * if (style == BloomStyle.WEEKLY) .075f else .085f
        val baseLength = minSide * .15f
        val growthRange = minSide * .20f
        val petalWidthFactor = when (style) {
            BloomStyle.STRESS -> .105f
            BloomStyle.ENERGY -> .115f
            BloomStyle.SLEEP -> .145f
            else -> .13f
        }
        val palette = listOf(SoftGreen, FreshGreen, Mint, PrimaryGreen, SoftGreen, FreshGreen, Mint)
        points.take(7).forEachIndexed { index, point ->
            val value = point.value
            val angleDegrees = -90f + index * (360f / 7f)
            val angleRadians = Math.toRadians(angleDegrees.toDouble())
            val animatedValue = (value ?: .32f) * reveal.value
            val length = baseLength + growthRange * animatedValue
            val width = minSide * petalWidthFactor * (.82f + animatedValue * .20f)
            val distance = coreRadius + length * .52f
            val petalCenter = Offset(
                center.x + cos(angleRadians).toFloat() * distance,
                center.y + sin(angleRadians).toFloat() * distance
            )
            val color = if (value == null) PaleMint.copy(alpha = .46f * reveal.value)
            else palette[index].copy(alpha = (.64f + value * .32f) * reveal.value)
            withTransform({ rotate(angleDegrees + 90f, pivot = petalCenter) }) {
                if (index == latestIndex && value != null) {
                    drawOval(
                        Mint.copy(alpha = .34f * reveal.value),
                        Offset(petalCenter.x - width * .82f, petalCenter.y - length * .60f),
                        Size(width * 1.64f, length * 1.20f)
                    )
                }
                if (style == BloomStyle.ENERGY) {
                    val leaf = Path().apply {
                        moveTo(petalCenter.x, petalCenter.y - length / 2f)
                        cubicTo(petalCenter.x + width, petalCenter.y - length * .12f, petalCenter.x + width * .55f, petalCenter.y + length * .42f, petalCenter.x, petalCenter.y + length / 2f)
                        cubicTo(petalCenter.x - width * .55f, petalCenter.y + length * .42f, petalCenter.x - width, petalCenter.y - length * .12f, petalCenter.x, petalCenter.y - length / 2f)
                        close()
                    }
                    drawPath(leaf, color)
                } else {
                    drawOval(color, Offset(petalCenter.x - width / 2f, petalCenter.y - length / 2f), Size(width, length))
                }
            }
        }
        drawCircle(MintSurfaceStrong.copy(alpha = reveal.value), coreRadius * 1.35f, center)
        drawCircle(PrimaryGreen.copy(alpha = reveal.value), coreRadius * .62f, center)
        drawCircle(PaleMint.copy(alpha = reveal.value), coreRadius * .20f, center)
    }
}

private fun emptyWeekPoints(today: LocalDate = LocalDate.now()): List<DailyMetricPoint> =
    (6L downTo 0L).map { daysAgo ->
        val date = today.minusDays(daysAgo)
        DailyMetricPoint(date.dayOfWeek.getDisplayName(TextStyle.NARROW, Locale.US), null)
    }

private fun currentWeekRange(today: LocalDate = LocalDate.now()): String {
    val formatter = DateTimeFormatter.ofPattern("MMM d", Locale.US)
    return "${today.minusDays(6).format(formatter)} – ${today.format(formatter)}"
}
