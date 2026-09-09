package com.innergarden.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.innergarden.app.ui.theme.DeepForest
import com.innergarden.app.ui.theme.FreshGreen
import com.innergarden.app.ui.theme.Mint
import com.innergarden.app.ui.theme.PaleMint
import com.innergarden.app.ui.theme.PrimaryGreen
import com.innergarden.app.ui.theme.SoftGreen

@Composable
fun GardenCard(modifier: Modifier = Modifier, containerColor: Color = Color.White, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp), content = content)
    }
}

@Composable
fun InnerGardenButton(text: String, modifier: Modifier = Modifier, enabled: Boolean = true, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
    ) { Text(text, style = MaterialTheme.typography.labelLarge) }
}

@Composable
fun SectionHeader(title: String, subtitle: String? = null) {
    Text(title, style = MaterialTheme.typography.titleLarge)
    subtitle?.let {
        Spacer(Modifier.height(4.dp))
        Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun BackHeader(title: String, subtitle: String? = null, onBack: () -> Unit) {
    Row(verticalAlignment = Alignment.Top) {
        IconButton(onClick = onBack) { Text("‹", style = MaterialTheme.typography.headlineMedium, color = DeepForest) }
        Column(modifier = Modifier.padding(top = 7.dp)) { SectionHeader(title, subtitle) }
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
    Canvas(modifier = modifier.size(164.dp)) {
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
