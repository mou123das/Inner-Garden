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

    fun onEvent(event: DeclutterUiEvent) {
        when (event) {
            DeclutterUiEvent.DeclutterWeek, DeclutterUiEvent.Retry -> generate()
        }
    }

    private fun generate() {
        if (_state.value.status == DeclutterStatus.LOADING) return
        _state.update { it.copy(status = DeclutterStatus.LOADING, declutter = null) }
        scope.launch {
            val result = runCatching { generateWeeklyDeclutter(getRecentReflections()) }
                .getOrDefault(WeeklyDeclutterResult.Failure)
            _state.value = when (result) {
                is WeeklyDeclutterResult.Success -> DeclutterUiState(DeclutterStatus.CONTENT, result.declutter)
                WeeklyDeclutterResult.Empty -> DeclutterUiState(DeclutterStatus.EMPTY)
                WeeklyDeclutterResult.Failure -> DeclutterUiState(DeclutterStatus.ERROR)
            }
        }
    }

    fun close() = scope.cancel()
}

class DeclutterViewModel(private val holder: DeclutterStateHolder) : ViewModel() {
    val uiState = holder.state
    fun onEvent(event: DeclutterUiEvent) = holder.onEvent(event)
    override fun onCleared() = holder.close()
}
