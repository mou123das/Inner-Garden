package com.innergarden.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.innergarden.app.ui.theme.DeepForest
import com.innergarden.app.ui.theme.GardenTextSecondary
import com.innergarden.app.ui.theme.MintSurface
import com.innergarden.app.ui.theme.MintSurfaceStrong

enum class BottomIcon { GARDEN, INSIGHTS, DECLUTTER }
data class BottomDestination(val route: String, val label: String, val icon: BottomIcon)

val bottomDestinations = listOf(
    BottomDestination("home", "Garden", BottomIcon.GARDEN),
    BottomDestination("insights", "Insights", BottomIcon.INSIGHTS),
    BottomDestination("declutter", "Declutter", BottomIcon.DECLUTTER)
)

@Composable
fun InnerGardenBottomBar(currentRoute: String?, onNavigate: (String) -> Unit) {
    val shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp)
    NavigationBar(
        modifier = Modifier.fillMaxWidth().shadow(4.dp, shape, clip = false).clip(shape),
        containerColor = MintSurface,
        tonalElevation = 0.dp
    ) {
        bottomDestinations.forEach { destination ->
            val selected = currentRoute == destination.route
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(destination.route) },
                icon = { BottomNavigationIcon(destination.icon, destination.label, selected) },
                label = {
                    Text(
                        destination.label,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Clip
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = DeepForest,
                    selectedTextColor = DeepForest,
                    indicatorColor = MintSurfaceStrong,
                    unselectedIconColor = GardenTextSecondary,
                    unselectedTextColor = GardenTextSecondary
                )
            )
        }
    }
}

@Composable
private fun BottomNavigationIcon(icon: BottomIcon, label: String, selected: Boolean) {
    val color = if (selected) DeepForest else GardenTextSecondary
    Canvas(Modifier.size(24.dp).semantics { contentDescription = label }) {
        when (icon) {
            BottomIcon.GARDEN -> {
                val leaf = Path().apply {
                    moveTo(size.width * .18f, size.height * .82f)
                    cubicTo(size.width * .18f, size.height * .35f, size.width * .56f, size.height * .12f, size.width * .88f, size.height * .12f)
                    cubicTo(size.width * .86f, size.height * .58f, size.width * .58f, size.height * .86f, size.width * .18f, size.height * .82f)
                    close()
                }
                drawPath(leaf, color)
                drawLine(MintSurface, Offset(size.width * .28f, size.height * .72f), Offset(size.width * .73f, size.height * .28f), 1.5.dp.toPx(), StrokeCap.Round)
            }
            BottomIcon.INSIGHTS -> {
                val radius = CornerRadius(2.dp.toPx())
                drawRoundRect(color, Offset(size.width * .12f, size.height * .53f), Size(size.width * .18f, size.height * .35f), radius)
                drawRoundRect(color, Offset(size.width * .41f, size.height * .32f), Size(size.width * .18f, size.height * .56f), radius)
                drawRoundRect(color, Offset(size.width * .70f, size.height * .14f), Size(size.width * .18f, size.height * .74f), radius)
            }
            BottomIcon.DECLUTTER -> {
                listOf(.27f, .5f, .73f).forEachIndexed { index, y ->
                    val inset = index * size.width * .08f
                    drawLine(color, Offset(size.width * .17f + inset, size.height * y), Offset(size.width * .83f - inset, size.height * y), 3.dp.toPx(), StrokeCap.Round)
                }
            }
        }
    }
}
