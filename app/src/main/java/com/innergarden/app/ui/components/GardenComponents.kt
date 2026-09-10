package com.innergarden.app.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.innergarden.app.ui.theme.DeepForest
import com.innergarden.app.ui.theme.FreshGreen
import com.innergarden.app.ui.theme.Mint
import com.innergarden.app.ui.theme.PaleMint
import com.innergarden.app.ui.theme.PrimaryGreen
import com.innergarden.app.ui.theme.SoftGreen
import com.innergarden.app.ui.theme.WarmSurface

@Composable
fun GardenCard(
    modifier: Modifier = Modifier,
    containerColor: Color = WarmSurface,
    contentPadding: PaddingValues = PaddingValues(18.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(modifier = Modifier.padding(contentPadding), content = content)
    }
}

@Composable
fun InnerGardenButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
    ) {
        if (leadingIcon != null) {
            leadingIcon()
            Spacer(Modifier.width(8.dp))
        }
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun SectionHeader(title: String, subtitle: String? = null) {
    Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
    subtitle?.let {
        Spacer(Modifier.height(4.dp))
        Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun BackHeader(title: String, subtitle: String? = null, onBack: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        HeaderIconButton("Back", "‹", onBack)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) { SectionHeader(title, subtitle) }
    }
}

@Composable
fun HeaderIconButton(contentDescription: String, symbol: String, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(48.dp)
            .background(PaleMint.copy(alpha = 0.72f), CircleShape)
            .semantics { this.contentDescription = contentDescription }
    ) {
        Text(symbol, style = MaterialTheme.typography.titleLarge, color = DeepForest, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun SettingsHeaderButton(onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(48.dp)
            .background(PaleMint.copy(alpha = 0.72f), CircleShape)
            .semantics { contentDescription = "Settings" }
    ) {
        Canvas(Modifier.size(22.dp)) {
            val stroke = 1.8.dp.toPx()
            val ys = listOf(size.height * .25f, size.height * .5f, size.height * .75f)
            val knobs = listOf(size.width * .36f, size.width * .66f, size.width * .45f)
            ys.forEachIndexed { index, y ->
                drawLine(DeepForest, Offset(0f, y), Offset(size.width, y), stroke, StrokeCap.Round)
                drawCircle(PaleMint, 3.dp.toPx(), Offset(knobs[index], y))
                drawCircle(DeepForest, 1.8.dp.toPx(), Offset(knobs[index], y))
            }
        }
    }
}

@Composable
fun RatingSelector(value: Int, onValueChange: (Int) -> Unit, labels: List<String> = listOf("1", "2", "3", "4", "5")) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        labels.forEachIndexed { index, label ->
            val rating = index + 1
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(if (rating == value) PrimaryGreen else PaleMint, CircleShape)
                    .clickable { onValueChange(rating) },
                contentAlignment = Alignment.Center
            ) {
                Text(label, color = if (rating == value) Color.White else DeepForest, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun TreeVisual(modifier: Modifier = Modifier) {
    val entrance = remember { Animatable(.84f) }
    LaunchedEffect(Unit) { entrance.animateTo(1f, tween(1_050)) }
    val transition = rememberInfiniteTransition(label = "tree-breathing")
    val scale by transition.animateFloat(
        initialValue = 0.972f,
        targetValue = 1.028f,
        animationSpec = infiniteRepeatable(tween(3_600), RepeatMode.Reverse),
        label = "tree-scale"
    )
    val swayDp by transition.animateFloat(
        initialValue = -3.5f,
        targetValue = 3.5f,
        animationSpec = infiniteRepeatable(tween(4_600), RepeatMode.Reverse),
        label = "tree-sway"
    )
    Canvas(modifier = modifier.size(164.dp).graphicsLayer {
        scaleX = scale * entrance.value
        scaleY = scale * entrance.value
        translationX = swayDp.dp.toPx()
        rotationZ = swayDp * .24f
        transformOrigin = androidx.compose.ui.graphics.TransformOrigin(.5f, .82f)
    }) {
        val w = size.width
        val h = size.height
        drawLine(DeepForest, Offset(w * .5f, h * .82f), Offset(w * .5f, h * .42f), w * .07f, StrokeCap.Round)
        drawLine(DeepForest, Offset(w * .5f, h * .60f), Offset(w * .30f, h * .43f), w * .04f, StrokeCap.Round)
        drawLine(DeepForest, Offset(w * .5f, h * .55f), Offset(w * .70f, h * .36f), w * .04f, StrokeCap.Round)
        drawCircle(FreshGreen, w * .23f, Offset(w * .50f, h * .29f))
        drawCircle(SoftGreen, w * .20f, Offset(w * .29f, h * .40f))
        drawCircle(PrimaryGreen, w * .20f, Offset(w * .70f, h * .37f))
        drawCircle(Mint, w * .16f, Offset(w * .47f, h * .15f))
        drawOval(PaleMint, Offset(w * .14f, h * .78f), androidx.compose.ui.geometry.Size(w * .72f, h * .13f))
    }
}
