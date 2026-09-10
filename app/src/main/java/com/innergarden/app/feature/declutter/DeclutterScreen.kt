package com.innergarden.app.feature.declutter

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.innergarden.app.ui.components.GardenCard
import com.innergarden.app.ui.components.AnimatedDeclutterLoading
import com.innergarden.app.ui.components.BackHeader
import com.innergarden.app.ui.components.BotanicalCardIcon
import com.innergarden.app.ui.components.InnerGardenButton
import com.innergarden.app.ui.components.ReflectionContentCard
import com.innergarden.app.ui.components.SectionHeader
import com.innergarden.app.ui.components.ThemeChip
import com.innergarden.app.ui.theme.MintSurface
import com.innergarden.app.ui.theme.SoftGardenSurface
import com.innergarden.app.ui.theme.WarmSurface

@Composable
fun DeclutterScreen(viewModel: DeclutterViewModel, onBackToGarden: () -> Unit, modifier: Modifier = Modifier) {
    val state = viewModel.uiState.collectAsStateWithLifecycle().value
    BackHandler(onBack = onBackToGarden)
    LaunchedEffect(Unit) { viewModel.onEvent(DeclutterUiEvent.ScreenShown) }
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        BackHeader("Mind Declutter", "A little space for the thoughts you've been carrying this week.", onBackToGarden)
        Crossfade(targetState = state.status, animationSpec = tween(250), label = "declutter-result") { status ->
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                when (status) {
                    DeclutterStatus.IDLE -> DeclutterMessageCard("When you're ready, gather your recent reflections into a gentle weekly look-back.")
                    DeclutterStatus.LOADING -> AnimatedDeclutterLoading(Modifier.height(176.dp))
                    DeclutterStatus.EMPTY -> DeclutterMessageCard("Add a reflection in a daily check-in, then return here to gather your weekly themes.")
                    DeclutterStatus.ERROR -> DeclutterMessageCard("Mind Declutter isn't available right now. Please try again later.")
                    DeclutterStatus.CONTENT -> state.declutter?.let { declutter ->
                        GardenCard(Modifier.fillMaxWidth(), WarmSurface) {
                            SectionHeader("This week's summary"); Spacer(Modifier.height(10.dp)); Text(declutter.summary)
                        }
                        GardenCard(Modifier.fillMaxWidth(), MintSurface) {
                            SectionHeader("This week's themes"); Spacer(Modifier.height(12.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                declutter.recurringThemes.forEach { ThemeChip(it) }
                            }
                        }
                        ReflectionContentCard("Carry forward", declutter.carryForwardReflection, BotanicalCardIcon.CARRY_FORWARD, emphasized = true)
                        ReflectionContentCard("Reflection question", declutter.reflectionQuestion, BotanicalCardIcon.QUESTION)
                    }
                }
            }
        }
        InnerGardenButton(
            text = when (state.status) {
                DeclutterStatus.LOADING -> "Gathering reflections..."
                DeclutterStatus.ERROR -> "Try again"
                DeclutterStatus.EMPTY -> "Back to Garden"
                else -> "Declutter this week"
            },
            enabled = state.status != DeclutterStatus.LOADING
        ) {
            when (state.status) {
                DeclutterStatus.EMPTY -> onBackToGarden()
                DeclutterStatus.ERROR -> viewModel.onEvent(DeclutterUiEvent.Retry)
                else -> viewModel.onEvent(DeclutterUiEvent.DeclutterWeek)
            }
        }
    }
}

@Composable
private fun DeclutterMessageCard(message: String) {
    GardenCard(Modifier.fillMaxWidth().height(176.dp), SoftGardenSurface) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
            Text(message)
        }
    }
}
