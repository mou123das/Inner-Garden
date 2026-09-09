package com.innergarden.app.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.innergarden.app.ui.components.BackHeader
import com.innergarden.app.ui.components.GardenCard

@Composable
fun SettingsScreen(viewModel: SettingsViewModel, onBack: () -> Unit) {
    val state = viewModel.uiState.collectAsStateWithLifecycle().value
    Column(Modifier.fillMaxSize().padding(20.dp)) {
        BackHeader("Settings", onBack = onBack)
        GardenCard(Modifier.fillMaxWidth().padding(top = 20.dp)) {
            state.rows.forEachIndexed { index, label ->
                Row(Modifier.fillMaxWidth().clickable { viewModel.onEvent(SettingsUiEvent.RowSelected(label)) }.padding(vertical = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(label, style = MaterialTheme.typography.bodyLarge); Text("›", color = MaterialTheme.colorScheme.primary)
                }
                if (index < state.rows.lastIndex) HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            }
        }
        Spacer(Modifier.weight(1f)); Text(state.version, modifier = Modifier.align(Alignment.CenterHorizontally), color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
