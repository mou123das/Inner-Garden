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
        GardenCard(Modifier.fillMaxWidth(), MaterialTheme.colorScheme.primaryContainer) {
            SectionHeader("This week's themes"); Spacer(Modifier.height(12.dp))
            state.themes.forEach { Text("•  $it", modifier = Modifier.padding(vertical = 6.dp)) }
        }
        SectionHeader("What you may want to carry forward")
        GardenCard(Modifier.fillMaxWidth()) { Text(state.carryForward) }
        InnerGardenButton("Declutter this week") { viewModel.onEvent(DeclutterUiEvent.DeclutterWeek) }
    }
}
