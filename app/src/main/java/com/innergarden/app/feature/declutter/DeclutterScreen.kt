package com.innergarden.app.feature.declutter

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.innergarden.app.ui.components.GardenCard
import com.innergarden.app.ui.components.InnerGardenButton
import com.innergarden.app.ui.components.SectionHeader

@Composable
fun DeclutterScreen(viewModel: DeclutterViewModel, modifier: Modifier = Modifier) {
    val state = viewModel.uiState.collectAsStateWithLifecycle().value
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        SectionHeader("Mind Declutter", "A little space for the thoughts you've been carrying this week.")
        when (state.status) {
            DeclutterStatus.IDLE -> GardenCard(Modifier.fillMaxWidth()) {
                Text("When you're ready, gather your recent reflections into a gentle weekly look-back.")
            }
            DeclutterStatus.LOADING -> GardenCard(Modifier.fillMaxWidth()) {
                Text("Gathering your reflections...", color = MaterialTheme.colorScheme.primary)
            }
            DeclutterStatus.EMPTY -> GardenCard(Modifier.fillMaxWidth()) {
                Text("Add a few reflections this week and your Mind Declutter will help you look back on recurring themes.")
            }
            DeclutterStatus.ERROR -> GardenCard(Modifier.fillMaxWidth()) {
                Text("Mind Declutter isn't available right now. Please try again later.")
            }
            DeclutterStatus.CONTENT -> state.declutter?.let { declutter ->
                GardenCard(Modifier.fillMaxWidth()) {
                    SectionHeader("This week's summary"); Spacer(Modifier.height(10.dp)); Text(declutter.summary)
                }
                GardenCard(Modifier.fillMaxWidth(), MaterialTheme.colorScheme.primaryContainer) {
                    SectionHeader("This week's themes"); Spacer(Modifier.height(12.dp))
                    declutter.recurringThemes.forEach { Text("•  $it", modifier = Modifier.padding(vertical = 6.dp)) }
                }
                SectionHeader("What you may want to carry forward")
                GardenCard(Modifier.fillMaxWidth()) { Text(declutter.carryForwardReflection) }
                SectionHeader("Something to reflect on")
                Text(declutter.reflectionQuestion)
            }
        }
        InnerGardenButton(
            text = when (state.status) {
                DeclutterStatus.LOADING -> "Gathering reflections..."
                DeclutterStatus.ERROR -> "Try again"
                else -> "Declutter this week"
            },
            enabled = state.status != DeclutterStatus.LOADING
        ) {
            viewModel.onEvent(if (state.status == DeclutterStatus.ERROR) DeclutterUiEvent.Retry else DeclutterUiEvent.DeclutterWeek)
        }
    }
}
