package com.innergarden.app.feature.reflection

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import com.innergarden.app.ui.components.AnimatedReflectionLoading
import com.innergarden.app.ui.components.BotanicalCardIcon
import com.innergarden.app.ui.components.InnerGardenButton
import com.innergarden.app.ui.components.ReflectionContentCard

@Composable
fun ReflectionScreen(viewModel: ReflectionViewModel, onBack: () -> Unit, onGarden: () -> Unit) {
    val state = viewModel.uiState.collectAsStateWithLifecycle().value
    val guidance = state.guidance
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        BackHeader("Your Reflection", onBack = onBack)
        Text("✓  Check-in complete", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
        Crossfade(targetState = state.isLoading, animationSpec = tween(250), label = "reflection-result") { isLoading ->
            if (isLoading) {
                AnimatedReflectionLoading()
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    ReflectionContentCard("Summary", guidance.summary, BotanicalCardIcon.SUMMARY)
                    ReflectionContentCard("Affirmation", guidance.affirmation, BotanicalCardIcon.AFFIRMATION, emphasized = true)
                    ReflectionContentCard("Reflection question", guidance.reflectionQuestion, BotanicalCardIcon.QUESTION)
                    ReflectionContentCard("Wellness activity", guidance.wellnessActivity, BotanicalCardIcon.ACTIVITY)
                }
            }
        }
        InnerGardenButton("Back to Garden") { viewModel.onEvent(ReflectionUiEvent.BackToGarden); onGarden() }
        Spacer(Modifier.height(8.dp))
    }
}
