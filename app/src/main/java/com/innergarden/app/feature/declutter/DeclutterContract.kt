package com.innergarden.app.feature.declutter

import androidx.lifecycle.ViewModel
import com.innergarden.app.domain.ai.GenerateWeeklyDeclutterUseCase
import com.innergarden.app.domain.ai.WeeklyDeclutterResult
import com.innergarden.app.domain.model.WeeklyDeclutter
import com.innergarden.app.domain.usecase.GetRecentReflectionsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class DeclutterStatus { IDLE, LOADING, CONTENT, EMPTY, ERROR }

data class DeclutterUiState(
    val status: DeclutterStatus = DeclutterStatus.IDLE,
    val declutter: WeeklyDeclutter? = null
)

sealed interface DeclutterUiEvent {
    data object ScreenShown : DeclutterUiEvent
    data object DeclutterWeek : DeclutterUiEvent
    data object Retry : DeclutterUiEvent
}

class DeclutterStateHolder(
    private val getRecentReflections: GetRecentReflectionsUseCase,
    private val generateWeeklyDeclutter: GenerateWeeklyDeclutterUseCase,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
) {
    private val _state = MutableStateFlow(DeclutterUiState())
    val state: StateFlow<DeclutterUiState> = _state.asStateFlow()
    private var generationJob: kotlinx.coroutines.Job? = null

    fun onEvent(event: DeclutterUiEvent) {
        when (event) {
            DeclutterUiEvent.ScreenShown -> if (_state.value.status == DeclutterStatus.EMPTY) {
                _state.value = DeclutterUiState()
            }
            DeclutterUiEvent.DeclutterWeek, DeclutterUiEvent.Retry -> generate()
        }
    }

    private fun generate() {
        if (generationJob?.isActive == true) return
        generationJob = scope.launch {
            val reflections = runCatching { getRecentReflections() }.getOrElse {
                _state.value = DeclutterUiState(DeclutterStatus.ERROR)
                return@launch
            }
            if (reflections.isEmpty()) {
                _state.value = DeclutterUiState(DeclutterStatus.EMPTY)
                return@launch
            }

            _state.value = DeclutterUiState(DeclutterStatus.LOADING)
            val loadingStartedAt = System.nanoTime()
            val result = runCatching { generateWeeklyDeclutter(reflections) }
                .getOrDefault(WeeklyDeclutterResult.Failure)
            val elapsedMillis = (System.nanoTime() - loadingStartedAt) / 1_000_000L
            delay((MINIMUM_LOADING_MILLIS - elapsedMillis).coerceAtLeast(0L))
            _state.value = when (result) {
                is WeeklyDeclutterResult.Success -> DeclutterUiState(DeclutterStatus.CONTENT, result.declutter)
                WeeklyDeclutterResult.Empty -> DeclutterUiState(DeclutterStatus.EMPTY)
                WeeklyDeclutterResult.Failure -> DeclutterUiState(DeclutterStatus.ERROR)
            }
        }
    }

    fun close() = scope.cancel()

    private companion object {
        const val MINIMUM_LOADING_MILLIS = 3000L
    }
}

class DeclutterViewModel(private val holder: DeclutterStateHolder) : ViewModel() {
    val uiState = holder.state
    fun onEvent(event: DeclutterUiEvent) = holder.onEvent(event)
    override fun onCleared() = holder.close()
}
