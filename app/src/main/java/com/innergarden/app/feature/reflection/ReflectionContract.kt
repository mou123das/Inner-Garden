package com.innergarden.app.feature.reflection

import androidx.lifecycle.ViewModel
import com.innergarden.app.domain.model.ReflectionGuidance
import com.innergarden.app.domain.usecase.GetPlaceholderReflectionGuidanceUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ReflectionUiState(val guidance: ReflectionGuidance)
sealed interface ReflectionUiEvent { data object BackToGarden : ReflectionUiEvent }
class ReflectionStateHolder(guidance: GetPlaceholderReflectionGuidanceUseCase) {
    private val _state = MutableStateFlow(ReflectionUiState(guidance()))
    val state: StateFlow<ReflectionUiState> = _state.asStateFlow()
    fun onEvent(event: ReflectionUiEvent) = Unit
}
class ReflectionViewModel(private val holder: ReflectionStateHolder) : ViewModel() {
    val uiState = holder.state
    fun onEvent(event: ReflectionUiEvent) = holder.onEvent(event)
}
