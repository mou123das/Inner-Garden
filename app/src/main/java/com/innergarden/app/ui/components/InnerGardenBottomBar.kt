package com.innergarden.app.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

data class BottomDestination(val route: String, val label: String, val icon: ImageVector)

private val LeafIcon = ImageVector.Builder("Leaf", 24.dp, 24.dp, 24f, 24f).apply {
    path { moveTo(4f, 20f); curveTo(5f, 9f, 12f, 4f, 21f, 3f); curveTo(20f, 13f, 15f, 20f, 4f, 20f); moveTo(6f, 17f); curveTo(10f, 13f, 14f, 10f, 18f, 7f) }
}.build()
private val ChartIcon = ImageVector.Builder("Chart", 24.dp, 24.dp, 24f, 24f).apply {
    path { moveTo(4f, 20f); lineTo(4f, 12f); lineTo(8f, 12f); lineTo(8f, 20f); close(); moveTo(10f, 20f); lineTo(10f, 7f); lineTo(14f, 7f); lineTo(14f, 20f); close(); moveTo(16f, 20f); lineTo(16f, 3f); lineTo(20f, 3f); lineTo(20f, 20f); close() }
}.build()
private val LinesIcon = ImageVector.Builder("Lines", 24.dp, 24.dp, 24f, 24f).apply {
    path { moveTo(5f, 7f); lineTo(19f, 7f); moveTo(5f, 12f); lineTo(16f, 12f); moveTo(5f, 17f); lineTo(13f, 17f) }
}.build()

val bottomDestinations = listOf(
    BottomDestination("home", "Garden", LeafIcon),
    BottomDestination("insights", "Insights", ChartIcon),
    BottomDestination("declutter", "Declutter", LinesIcon)
)

@Composable
fun InnerGardenBottomBar(currentRoute: String?, onNavigate: (String) -> Unit) {
    NavigationBar {
        bottomDestinations.forEach { destination ->
            NavigationBarItem(
                selected = currentRoute == destination.route,
                onClick = { onNavigate(destination.route) },
                icon = { Icon(destination.icon, null, Modifier.size(22.dp)) },
                label = { Text(destination.label) }
            )
        }
    }
}
