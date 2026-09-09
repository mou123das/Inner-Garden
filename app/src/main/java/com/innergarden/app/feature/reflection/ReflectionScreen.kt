package com.innergarden.app.feature.reflection

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.innergarden.app.ui.components.BackHeader
import com.innergarden.app.ui.components.GardenCard
import com.innergarden.app.ui.components.InnerGardenButton
import com.innergarden.app.ui.components.SectionHeader

@Composable
fun ReflectionScreen(viewModel: ReflectionViewModel, onBack: () -> Unit, onGarden: () -> Unit) {
    val guidance = viewModel.uiState.collectAsStateWithLifecycle().value.guidance
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        BackHeader("Your Reflection", onBack = onBack)
        Text("✓  Check-in complete", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
        ReflectionCard("Your reflection", guidance.summary)
        ReflectionCard("A thought for you", guidance.affirmation, true)
        SectionHeader("Something to reflect on"); Text(guidance.reflectionQuestion)
        SectionHeader("Try something small"); Text(guidance.wellnessActivity)
        InnerGardenButton("Back to Garden") { viewModel.onEvent(ReflectionUiEvent.BackToGarden); onGarden() }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable private fun ReflectionCard(title: String, body: String, tinted: Boolean = false) {
    GardenCard(Modifier.fillMaxWidth(), if (tinted) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface) { SectionHeader(title); Spacer(Modifier.height(8.dp)); Text(body) }
}
