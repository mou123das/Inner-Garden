package com.innergarden.app.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.innergarden.app.ui.components.BackHeader
import com.innergarden.app.ui.components.GardenCard
import com.innergarden.app.ui.components.SectionHeader
import com.innergarden.app.ui.theme.DeepForest
import com.innergarden.app.ui.theme.MintSurface
import com.innergarden.app.ui.theme.MintSurfaceStrong
import com.innergarden.app.ui.theme.PrimaryGreen
import com.innergarden.app.ui.theme.WarmSurface

@Composable
fun SettingsScreen(viewModel: SettingsViewModel, onBack: () -> Unit) {
    val state = viewModel.uiState.collectAsStateWithLifecycle().value
    val wellbeingRows = state.rows.take(2)
    val informationRows = state.rows.drop(2)
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        BackHeader("Settings", "Shape how your garden supports you.", onBack)
        SettingsGroup("Gentle support", wellbeingRows, viewModel::onEvent)
        SettingsGroup("Privacy & information", informationRows, viewModel::onEvent)
        Spacer(Modifier.height(8.dp))
        Text(
            state.version,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SettingsGroup(title: String, rows: List<String>, onEvent: (SettingsUiEvent) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionHeader(title)
        GardenCard(Modifier.fillMaxWidth(), WarmSurface, contentPadding = androidx.compose.foundation.layout.PaddingValues(10.dp)) {
            rows.forEachIndexed { index, label ->
                SettingsRow(label) { onEvent(SettingsUiEvent.RowSelected(label)) }
                if (index < rows.lastIndex) Spacer(Modifier.height(4.dp))
            }
        }
    }
}

@Composable
private fun SettingsRow(label: String, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 8.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(36.dp).background(MintSurface, CircleShape), contentAlignment = Alignment.Center) {
            Text(
                when {
                    label.startsWith("Reminder") -> "○"
                    label.startsWith("Notification") -> "·"
                    label.startsWith("Privacy") -> "⌁"
                    else -> "i"
                },
                color = PrimaryGreen,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.size(12.dp))
        Text(label, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge, color = DeepForest)
        Box(Modifier.size(30.dp).background(MintSurfaceStrong, CircleShape), contentAlignment = Alignment.Center) {
            Text("›", color = DeepForest, fontWeight = FontWeight.SemiBold)
        }
    }
}
