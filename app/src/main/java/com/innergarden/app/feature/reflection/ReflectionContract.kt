package com.innergarden.app.feature.reflection

import androidx.lifecycle.ViewModel
import com.innergarden.app.domain.ai.GenerateReflectionGuidanceUseCase
import com.innergarden.app.domain.model.ReflectionGuidance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReflectionUiState(val guidance: ReflectionGuidance, val isLoading: Boolean)
sealed interface ReflectionUiEvent { data object BackToGarden : ReflectionUiEvent }
class ReflectionStateHolder(
    reflection: String,
    private val generateGuidance: GenerateReflectionGuidanceUseCase,
    initialGuidance: ReflectionGuidance,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
) {
    private val _state = MutableStateFlow(ReflectionUiState(initialGuidance, reflection.isNotBlank()))
    val state: StateFlow<ReflectionUiState> = _state.asStateFlow()
    init {
        if (reflection.isNotBlank()) scope.launch {
            val guidance = generateGuidance(reflection)
            _state.update { it.copy(guidance = guidance, isLoading = false) }
        }
    }
    fun onEvent(event: ReflectionUiEvent) = Unit
    fun close() = scope.cancel()
}
class ReflectionViewModel(private val holder: ReflectionStateHolder) : ViewModel() {
    val uiState = holder.state
    fun onEvent(event: ReflectionUiEvent) = holder.onEvent(event)
    override fun onCleared() = holder.close()
}
