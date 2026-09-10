package com.innergarden.app.feature.checkin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.innergarden.app.ui.components.BackHeader
import com.innergarden.app.ui.components.GardenCard
import com.innergarden.app.ui.components.InnerGardenButton
import com.innergarden.app.ui.components.ReflectionJournalCard
import com.innergarden.app.ui.components.SaveLeafIcon
import com.innergarden.app.ui.components.WellbeingMetricCard
import com.innergarden.app.ui.components.WellbeingMetricType
import com.innergarden.app.ui.theme.MintSurface

@Composable
fun CheckInScreen(viewModel: CheckInViewModel, onBack: () -> Unit, onSaved: (String) -> Unit) {
    val state = viewModel.uiState.collectAsStateWithLifecycle().value
    LaunchedEffect(state.saveCompleted) {
        if (state.saveCompleted) {
            viewModel.onEvent(CheckInUiEvent.SaveHandled)
            onSaved(state.reflection)
        }
    }
    Column(
        Modifier.fillMaxSize().imePadding().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        BackHeader("Daily Check-In", "Take a moment to notice how today feels.", onBack)
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Your wellbeing garden", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            Text("Choose what feels closest right now.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        WellbeingGarden(state, viewModel::onEvent)
        ReflectionJournalCard(state.reflection, { viewModel.onEvent(CheckInUiEvent.ReflectionChanged(it)) }, Modifier.fillMaxWidth())
        state.errorMessage?.let {
            GardenCard(Modifier.fillMaxWidth(), MintSurface) { Text(it, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        }
        InnerGardenButton(
            text = if (state.isSaving) "Saving..." else "Save Check-In",
            enabled = !state.isSaving,
            leadingIcon = if (state.isSaving) null else ({ SaveLeafIcon() })
        ) { viewModel.onEvent(CheckInUiEvent.Save) }
        Spacer(Modifier.height(6.dp))
    }
}

@Composable
private fun WellbeingGarden(state: CheckInUiState, onEvent: (CheckInUiEvent) -> Unit) {
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val compactGrid = maxWidth >= 350.dp
        if (compactGrid) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    WellbeingMetricCard("Mood", state.mood, WellbeingMetricType.MOOD, Modifier.weight(1f)) { onEvent(CheckInUiEvent.MoodChanged(it)) }
                    WellbeingMetricCard("Stress", state.stress, WellbeingMetricType.STRESS, Modifier.weight(1f)) { onEvent(CheckInUiEvent.StressChanged(it)) }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    WellbeingMetricCard("Energy", state.energy, WellbeingMetricType.ENERGY, Modifier.weight(1f)) { onEvent(CheckInUiEvent.EnergyChanged(it)) }
                    WellbeingMetricCard("Sleep", state.sleep, WellbeingMetricType.SLEEP, Modifier.weight(1f)) { onEvent(CheckInUiEvent.SleepChanged(it)) }
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                WellbeingMetricCard("Mood", state.mood, WellbeingMetricType.MOOD, Modifier.fillMaxWidth()) { onEvent(CheckInUiEvent.MoodChanged(it)) }
                WellbeingMetricCard("Stress", state.stress, WellbeingMetricType.STRESS, Modifier.fillMaxWidth()) { onEvent(CheckInUiEvent.StressChanged(it)) }
                WellbeingMetricCard("Energy", state.energy, WellbeingMetricType.ENERGY, Modifier.fillMaxWidth()) { onEvent(CheckInUiEvent.EnergyChanged(it)) }
                WellbeingMetricCard("Sleep Quality", state.sleep, WellbeingMetricType.SLEEP, Modifier.fillMaxWidth()) { onEvent(CheckInUiEvent.SleepChanged(it)) }
            }
        }
    }
}
