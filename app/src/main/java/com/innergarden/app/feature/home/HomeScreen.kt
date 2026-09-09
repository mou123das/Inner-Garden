package com.innergarden.app.feature.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.innergarden.app.ui.components.GardenCard
import com.innergarden.app.ui.components.InnerGardenButton
import com.innergarden.app.ui.components.SectionHeader
import com.innergarden.app.ui.components.TreeVisual

@Composable
fun HomeScreen(viewModel: HomeViewModel, onCheckIn: () -> Unit, onReflection: () -> Unit, onSettings: () -> Unit, modifier: Modifier = Modifier) {
    val state = viewModel.uiState.collectAsStateWithLifecycle().value
    Column(modifier.verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column { Text(state.greeting, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold); Text("Welcome to your Inner Garden", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            Text("Settings", color = MaterialTheme.colorScheme.primary, modifier = Modifier.clickable { viewModel.onEvent(HomeUiEvent.Settings); onSettings() }.padding(12.dp), style = MaterialTheme.typography.labelLarge)
        }
        GardenCard(Modifier.fillMaxWidth(), MaterialTheme.colorScheme.primaryContainer) {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                TreeVisual(); Text(state.gardenStage, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold); Text(state.streak, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        GardenCard(Modifier.fillMaxWidth()) {
            SectionHeader("Daily Check-In", "Take a moment for yourself"); Spacer(Modifier.height(16.dp))
            InnerGardenButton("Check in with yourself") { viewModel.onEvent(HomeUiEvent.CheckIn); onCheckIn() }
        }
        GardenCard(Modifier.fillMaxWidth()) {
            SectionHeader("Today's Reflection"); Spacer(Modifier.height(8.dp)); Text(state.reflection); Spacer(Modifier.height(10.dp))
            Text("View reflection  →", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold, modifier = Modifier.clickable { viewModel.onEvent(HomeUiEvent.ViewReflection); onReflection() })
        }
        SectionHeader("This Week")
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            GardenCard(Modifier.weight(1f), MaterialTheme.colorScheme.surfaceVariant) { Text("Insights", fontWeight = FontWeight.Bold); Spacer(Modifier.height(6.dp)); Text("A steady week", style = MaterialTheme.typography.bodySmall) }
            GardenCard(Modifier.weight(1f), MaterialTheme.colorScheme.surfaceVariant) { Text("Mind Declutter", fontWeight = FontWeight.Bold); Spacer(Modifier.height(6.dp)); Text("3 themes noticed", style = MaterialTheme.typography.bodySmall) }
        }
        Spacer(Modifier.height(8.dp))
    }
}
