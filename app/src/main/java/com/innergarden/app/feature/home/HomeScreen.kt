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
import com.innergarden.app.ui.components.BotanicalCardIcon
import com.innergarden.app.ui.components.BotanicalIconContainer
import com.innergarden.app.ui.components.SettingsHeaderButton
import com.innergarden.app.ui.components.InnerGardenButton
import com.innergarden.app.ui.components.SectionHeader
import com.innergarden.app.ui.components.TreeVisual
import com.innergarden.app.ui.theme.MintSurface
import com.innergarden.app.ui.theme.SoftGardenSurface

@Composable
fun HomeScreen(viewModel: HomeViewModel, onCheckIn: () -> Unit, onReflection: () -> Unit, onSettings: () -> Unit, modifier: Modifier = Modifier) {
    val state = viewModel.uiState.collectAsStateWithLifecycle().value
    Column(modifier.verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column { Text(state.greeting, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold); Text("Welcome to your Inner Garden", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            SettingsHeaderButton { viewModel.onEvent(HomeUiEvent.Settings); onSettings() }
        }
        state.errorMessage?.let {
            GardenCard(Modifier.fillMaxWidth()) {
                Text(it, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(12.dp))
                InnerGardenButton("Try again") { viewModel.onEvent(HomeUiEvent.Retry) }
            }
        }
        GardenCard(Modifier.fillMaxWidth(), SoftGardenSurface) {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                TreeVisual(); Text(if (state.isLoading) "Loading your garden..." else state.gardenStage, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold); Text(state.streak, color = MaterialTheme.colorScheme.onSurfaceVariant)
                state.wellbeingScore?.let { Text("Recent wellbeing indicator: " + it + " / 100", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
        }
        GardenCard(Modifier.fillMaxWidth(), MintSurface) {
            SectionHeader("Daily Check-In", "Take a moment for yourself"); Spacer(Modifier.height(16.dp))
            InnerGardenButton(if (state.hasCheckedInToday) "Add another check-in" else "Check in with yourself") { viewModel.onEvent(HomeUiEvent.CheckIn); onCheckIn() }
        }
        GardenCard(Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                BotanicalIconContainer(BotanicalCardIcon.SUMMARY); Spacer(Modifier.padding(5.dp)); SectionHeader("Today's Reflection")
            }
            Spacer(Modifier.height(8.dp)); Text(state.reflection); Spacer(Modifier.height(10.dp))
            Text("View reflection  →", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold, modifier = Modifier.clickable { viewModel.onEvent(HomeUiEvent.ViewReflection); onReflection() })
        }
        SectionHeader("This Week")
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            GardenCard(Modifier.weight(1f), SoftGardenSurface) { Text("Insights", fontWeight = FontWeight.SemiBold); Spacer(Modifier.height(6.dp)); Text(state.recentSummary, style = MaterialTheme.typography.bodySmall) }
            GardenCard(Modifier.weight(1f), MintSurface) { Text("Mind Declutter", fontWeight = FontWeight.SemiBold); Spacer(Modifier.height(6.dp)); Text("Gather this week's reflections", style = MaterialTheme.typography.bodySmall) }
        }
        Spacer(Modifier.height(8.dp))
    }
}
