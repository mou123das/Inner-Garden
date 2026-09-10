package com.innergarden.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.innergarden.app.ui.theme.DeepForest
import com.innergarden.app.ui.theme.FreshGreen
import com.innergarden.app.ui.theme.GardenTextSecondary
import com.innergarden.app.ui.theme.Mint
import com.innergarden.app.ui.theme.MintSurface
import com.innergarden.app.ui.theme.MintSurfaceStrong
import com.innergarden.app.ui.theme.PaleMint
import com.innergarden.app.ui.theme.PrimaryGreen
import com.innergarden.app.ui.theme.SoftGardenSurface
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlinx.coroutines.delay

enum class WellbeingMetricType { MOOD, STRESS, ENERGY, SLEEP }

@Composable
fun WellbeingMetricCard(
    title: String,
    value: Int,
    type: WellbeingMetricType,
    modifier: Modifier = Modifier,
    onValueChange: (Int) -> Unit
) {
    val surface = when (type) {
        WellbeingMetricType.MOOD -> MintSurface
        WellbeingMetricType.STRESS -> SoftGardenSurface
        WellbeingMetricType.ENERGY -> Color(0xFFE2F2E6)
        WellbeingMetricType.SLEEP -> Color(0xFFECF6ED)
    }
    GardenCard(modifier, surface, contentPadding = androidx.compose.foundation.layout.PaddingValues(14.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text("$value/5", style = MaterialTheme.typography.bodyMedium, color = PrimaryGreen, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth().selectableGroup(), verticalAlignment = Alignment.CenterVertically) {
            (1..5).forEach { level ->
                MetricOption(title, level, value == level, type, surface) { onValueChange(level) }
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Low", style = MaterialTheme.typography.bodySmall, color = GardenTextSecondary)
            Text("High", style = MaterialTheme.typography.bodySmall, color = GardenTextSecondary)
        }
    }
}

@Composable
private fun RowScope.MetricOption(
    metric: String,
    level: Int,
    selected: Boolean,
    type: WellbeingMetricType,
    surface: Color,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(if (selected) 1.12f else .9f, tween(220), label = "$metric-$level-scale")
    Box(
        Modifier
            .weight(1f)
            .height(46.dp)
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
            .semantics {
                contentDescription = "$metric, $level out of 5"
                stateDescription = if (selected) "Selected" else "Not selected"
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.size(30.dp).graphicsLayer { scaleX = scale; scaleY = scale }) {
            val center = Offset(size.width / 2, size.height / 2)
            if (selected) drawCircle(MintSurfaceStrong, size.minDimension * .48f, center)
            val color = if (selected) PrimaryGreen else FreshGreen.copy(alpha = .48f)
            when (type) {
                WellbeingMetricType.MOOD -> drawBloom(center, level, color)
                WellbeingMetricType.STRESS -> drawRipples(center, level, color)
                WellbeingMetricType.ENERGY -> drawGrowth(center, level, color)
                WellbeingMetricType.SLEEP -> drawRest(center, level, color, surface)
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawBloom(center: Offset, level: Int, color: Color) {
    val petalCount = level + 2
    val orbit = size.minDimension * (.10f + level * .012f)
    val petalRadius = size.minDimension * (.06f + level * .006f)
    repeat(petalCount) { index ->
        val angle = 2 * PI * index / petalCount - PI / 2
        drawCircle(color, petalRadius, Offset(center.x + cos(angle).toFloat() * orbit, center.y + sin(angle).toFloat() * orbit))
    }
    drawCircle(color, size.minDimension * .07f, center)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawRipples(center: Offset, level: Int, color: Color) {
    repeat(level) { index ->
        val radius = size.minDimension * (.10f + index * .055f)
        drawCircle(color.copy(alpha = .45f + index * .1f), radius, center, style = Stroke(1.5.dp.toPx()))
    }
    drawCircle(color, 2.dp.toPx(), center)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawGrowth(center: Offset, level: Int, color: Color) {
    val baseY = center.y + size.height * .20f
    val topY = baseY - size.height * (.12f + level * .055f)
    drawCircle(color, 2.5.dp.toPx(), Offset(center.x, baseY + 2.dp.toPx()))
    if (level >= 2) drawLine(color, Offset(center.x, baseY), Offset(center.x, topY), 2.dp.toPx(), StrokeCap.Round)
    if (level >= 3) drawOval(color, Offset(center.x - 8.dp.toPx(), topY + 3.dp.toPx()), androidx.compose.ui.geometry.Size(8.dp.toPx(), 5.dp.toPx()))
    if (level >= 4) drawOval(color, Offset(center.x, topY + 7.dp.toPx()), androidx.compose.ui.geometry.Size(8.dp.toPx(), 5.dp.toPx()))
    if (level >= 5) drawCircle(color, 3.dp.toPx(), Offset(center.x, topY))
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawRest(center: Offset, level: Int, color: Color, surface: Color) {
    val radius = size.minDimension * (.13f + level * .012f)
    drawCircle(color, radius, center)
    drawCircle(surface, radius * .88f, Offset(center.x + radius * .48f, center.y - radius * .18f))
    repeat((level - 1).coerceAtLeast(0)) { index ->
        val angle = 2 * PI * index / 4
        drawCircle(color.copy(alpha = .7f), 1.3.dp.toPx(), Offset(center.x + cos(angle).toFloat() * radius * 1.65f, center.y + sin(angle).toFloat() * radius * 1.4f))
    }
}

@Composable
fun ReflectionJournalCard(value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    var isFocused by remember { mutableStateOf(false) }
    LaunchedEffect(isFocused) {
        if (isFocused) {
            delay(180)
            bringIntoViewRequester.bringIntoView()
        }
    }
    GardenCard(modifier, MintSurface, contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            BotanicalIconContainer(BotanicalCardIcon.SUMMARY)
            Spacer(Modifier.size(10.dp))
            Column {
                Text("Reflection", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text("Anything you'd like to leave here from today?", style = MaterialTheme.typography.bodySmall, color = GardenTextSecondary)
            }
        }
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .bringIntoViewRequester(bringIntoViewRequester)
                .onFocusChanged { isFocused = it.isFocused },
            minLines = 4,
            placeholder = { Text("Write a few thoughts...") },
            shape = RoundedCornerShape(18.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = PrimaryGreen,
                unfocusedBorderColor = Mint,
                cursorColor = PrimaryGreen
            )
        )
    }
}

@Composable
fun SaveLeafIcon() {
    Canvas(Modifier.size(18.dp)) {
        drawOval(Color.White, Offset(size.width * .12f, size.height * .12f), Size(size.width * .72f, size.height * .58f))
        drawLine(PrimaryGreen, Offset(size.width * .28f, size.height * .65f), Offset(size.width * .72f, size.height * .24f), 1.2.dp.toPx(), StrokeCap.Round)
    }
}
