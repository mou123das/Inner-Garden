package com.innergarden.app.feature.insights

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class InsightMetric(val label: String, val value: String, val points: List<Float>)
data class InsightsUiState(val metrics: List<InsightMetric> = listOf(
    InsightMetric("Mood", "4.1 / 5", listOf(.55f,.65f,.60f,.78f,.72f,.84f,.82f)),
    InsightMetric("Energy", "3.7 / 5", listOf(.50f,.62f,.58f,.66f,.70f,.68f,.74f)),
    InsightMetric("Stress", "2.8 / 5", listOf(.72f,.62f,.67f,.55f,.58f,.50f,.48f)),
    InsightMetric("Sleep", "4.0 / 5", listOf(.64f,.74f,.70f,.80f,.76f,.82f,.80f))
), val observation: String = "Your energy has been fairly consistent this week.")
sealed interface InsightsUiEvent { data object Viewed : InsightsUiEvent }
class InsightsStateHolder { private val _state = MutableStateFlow(InsightsUiState()); val state: StateFlow<InsightsUiState> = _state.asStateFlow(); fun onEvent(event: InsightsUiEvent) = Unit }
class InsightsViewModel(private val holder: InsightsStateHolder = InsightsStateHolder()) : ViewModel() { val uiState = holder.state; fun onEvent(event: InsightsUiEvent) = holder.onEvent(event) }
