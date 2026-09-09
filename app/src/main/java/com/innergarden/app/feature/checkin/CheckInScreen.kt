package com.innergarden.app.feature.checkin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.innergarden.app.ui.components.BackHeader
import com.innergarden.app.ui.components.GardenCard
import com.innergarden.app.ui.components.InnerGardenButton
import com.innergarden.app.ui.components.RatingSelector

@Composable
fun CheckInScreen(viewModel: CheckInViewModel, onBack: () -> Unit, onSaved: () -> Unit) {
    val state = viewModel.uiState.collectAsStateWithLifecycle().value
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        BackHeader("Daily Check-In", "Take a minute to notice how you're feeling.", onBack)
        RatingCard("Mood", state.mood, listOf("○", "◔", "◑", "◕", "●")) { viewModel.onEvent(CheckInUiEvent.MoodChanged(it)) }
        RatingCard("Stress", state.stress) { viewModel.onEvent(CheckInUiEvent.StressChanged(it)) }
        RatingCard("Energy", state.energy) { viewModel.onEvent(CheckInUiEvent.EnergyChanged(it)) }
        RatingCard("Sleep Quality", state.sleep) { viewModel.onEvent(CheckInUiEvent.SleepChanged(it)) }
        GardenCard(Modifier.fillMaxWidth()) {
            Text("Anything on your mind?", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = state.reflection,
                onValueChange = { viewModel.onEvent(CheckInUiEvent.ReflectionChanged(it)) },
                modifier = Modifier.fillMaxWidth().height(130.dp),
                placeholder = { Text("Write a few thoughts...") },
                shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp)
            )
        }
        InnerGardenButton("Save today's check-in") { viewModel.onEvent(CheckInUiEvent.Save); onSaved() }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun RatingCard(title: String, value: Int, labels: List<String> = listOf("1", "2", "3", "4", "5"), onChange: (Int) -> Unit) {
    GardenCard(Modifier.fillMaxWidth()) { Text(title, style = MaterialTheme.typography.titleMedium); Spacer(Modifier.height(12.dp)); RatingSelector(value, onChange, labels) }
}
