package com.innergarden.app.feature.reflection

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ReflectionGuidance(val summary: String, val affirmation: String, val reflectionQuestion: String, val wellnessActivity: String)
data class ReflectionUiState(val guidance: ReflectionGuidance = ReflectionGuidance(
    summary = "You checked in with yourself and noticed what today has been carrying.",
    affirmation = "I can make room for what I feel, one gentle moment at a time.",
    reflectionQuestion = "What felt a little lighter today?",
    wellnessActivity = "Step near a window and take three slow, comfortable breaths."
))
sealed interface ReflectionUiEvent { data object BackToGarden : ReflectionUiEvent }
class ReflectionStateHolder { private val _state = MutableStateFlow(ReflectionUiState()); val state: StateFlow<ReflectionUiState> = _state.asStateFlow(); fun onEvent(event: ReflectionUiEvent) = Unit }
class ReflectionViewModel(private val holder: ReflectionStateHolder = ReflectionStateHolder()) : ViewModel() { val uiState = holder.state; fun onEvent(event: ReflectionUiEvent) = holder.onEvent(event) }
