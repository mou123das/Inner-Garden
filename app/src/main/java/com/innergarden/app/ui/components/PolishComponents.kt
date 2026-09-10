package com.innergarden.app.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.innergarden.app.ui.theme.DeepForest
import com.innergarden.app.ui.theme.FreshGreen
import com.innergarden.app.ui.theme.MintSurface
import com.innergarden.app.ui.theme.MintSurfaceStrong
import com.innergarden.app.ui.theme.PrimaryGreen
import com.innergarden.app.ui.theme.SoftGardenSurface

enum class BotanicalCardIcon(val symbol: String) {
    SUMMARY("⌁"), AFFIRMATION("✦"), QUESTION("?"), ACTIVITY("↗"), CARRY_FORWARD("↟")
}

@Composable
fun ReflectionContentCard(
    title: String,
    body: String,
    icon: BotanicalCardIcon,
    modifier: Modifier = Modifier,
    emphasized: Boolean = false
) {
    GardenCard(
        modifier = modifier.fillMaxWidth(),
        containerColor = if (emphasized) MintSurfaceStrong else SoftGardenSurface
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            BotanicalIconContainer(icon)
            Spacer(Modifier.size(10.dp))
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(10.dp))
        Text(
            body,
            style = MaterialTheme.typography.bodyLarge,
            color = DeepForest,
            fontWeight = if (emphasized) FontWeight.Medium else FontWeight.Normal
        )
    }
}

@Composable
fun BotanicalIconContainer(icon: BotanicalCardIcon) {
    Box(
        Modifier.size(34.dp).background(MintSurfaceStrong, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(icon.symbol, color = PrimaryGreen, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun AnimatedReflectionLoading(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "reflection-growing")
    val growth by transition.animateFloat(
        initialValue = .64f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1_150), RepeatMode.Reverse),
        label = "reflection-growth"
    )
    GardenCard(modifier.fillMaxWidth(), MintSurface) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Canvas(Modifier.size(width = 72.dp, height = 56.dp)) {
                val base = Offset(size.width / 2, size.height * .86f)
                val top = Offset(size.width / 2, size.height * (.74f - .38f * growth))
                drawLine(PrimaryGreen.copy(alpha = .78f), base, top, 3.dp.toPx(), StrokeCap.Round)
                val leafWidth = 17.dp.toPx() * growth
                val leafHeight = 10.dp.toPx() * growth
                drawOval(FreshGreen.copy(alpha = .82f), Offset(top.x - leafWidth, top.y), Size(leafWidth, leafHeight))
                drawOval(PrimaryGreen.copy(alpha = .82f), Offset(top.x, top.y + 5.dp.toPx()), Size(leafWidth, leafHeight))
                drawCircle(MintSurfaceStrong, 7.dp.toPx() * growth, Offset(base.x, base.y + 2.dp.toPx()))
            }
            Spacer(Modifier.height(6.dp))
            Text("Preparing your reflection", color = DeepForest, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(3.dp))
            Text("A gentle thought is taking shape.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun AnimatedDeclutterLoading(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "declutter-gathering")
    val gathering by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1_450, easing = FastOutSlowInEasing), RepeatMode.Restart),
        label = "declutter-gathering-progress"
    )
    val glow by transition.animateFloat(
        initialValue = .35f,
        targetValue = .85f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "declutter-center-glow"
    )
    GardenCard(modifier.fillMaxWidth(), MintSurface) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Canvas(Modifier.size(width = 132.dp, height = 72.dp)) {
                val target = Offset(size.width / 2, size.height * .55f)
                drawCircle(MintSurfaceStrong.copy(alpha = glow), 18.dp.toPx(), target)
                repeat(5) { index ->
                    val startX = size.width * (.08f + index * .21f)
                    val startY = size.height * if (index % 2 == 0) .28f else .76f
                    val angle = (index - 2) * (Math.PI / 5.0)
                    val end = Offset(
                        target.x + kotlin.math.cos(angle).toFloat() * 13.dp.toPx(),
                        target.y + kotlin.math.sin(angle).toFloat() * 11.dp.toPx()
                    )
                    val center = Offset(
                        startX + (end.x - startX) * gathering,
                        startY + (end.y - startY) * gathering
                    )
                    val r = (5.5f + gathering * 1.8f).dp.toPx()
                    val leaf = Path().apply {
                        moveTo(center.x, center.y - r)
                        cubicTo(center.x + r * 1.15f, center.y - r * .65f, center.x + r, center.y + r, center.x, center.y + r)
                        cubicTo(center.x - r, center.y + r, center.x - r * 1.15f, center.y - r * .65f, center.x, center.y - r)
                        close()
                    }
                    drawPath(leaf, if (index == 2) PrimaryGreen else FreshGreen.copy(alpha = .72f + gathering * .18f))
                }
                drawCircle(PrimaryGreen.copy(alpha = gathering), 4.dp.toPx(), target)
            }
            Spacer(Modifier.height(4.dp))
            Text("Gathering your reflections…", color = DeepForest, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(3.dp))
            Text("Bringing scattered thoughts into focus.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun ThemeChip(text: String, modifier: Modifier = Modifier) {
    Row(
        modifier.fillMaxWidth().background(MintSurfaceStrong, RoundedCornerShape(14.dp)).padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(7.dp).background(FreshGreen, CircleShape))
        Spacer(Modifier.size(9.dp))
        Text(text, color = DeepForest, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun BotanicalEmptyState(message: String, modifier: Modifier = Modifier) {
    GardenCard(modifier.fillMaxWidth(), SoftGardenSurface) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Canvas(Modifier.size(44.dp)) {
                val base = Offset(size.width * .5f, size.height * .8f)
                val top = Offset(size.width * .5f, size.height * .3f)
                drawLine(PrimaryGreen, base, top, 2.dp.toPx(), StrokeCap.Round)
                drawOval(FreshGreen, Offset(top.x - 13.dp.toPx(), top.y), Size(13.dp.toPx(), 8.dp.toPx()))
                drawOval(PrimaryGreen, Offset(top.x, top.y + 6.dp.toPx()), Size(13.dp.toPx(), 8.dp.toPx()))
            }
            Spacer(Modifier.size(12.dp))
            Text(message, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, color = DeepForest)
        }
    }
}
