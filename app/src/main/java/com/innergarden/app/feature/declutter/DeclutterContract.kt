package com.innergarden.app.feature.declutter

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class DeclutterUiState(val themes: List<String> = listOf("Work felt demanding", "Rest helped your energy", "You valued quiet time"), val carryForward: String = "Keep protecting a small pocket of quiet. It seemed to give the rest of your week more room.")
sealed interface DeclutterUiEvent { data object DeclutterWeek : DeclutterUiEvent }
class DeclutterStateHolder { private val _state = MutableStateFlow(DeclutterUiState()); val state: StateFlow<DeclutterUiState> = _state.asStateFlow(); fun onEvent(event: DeclutterUiEvent) = Unit }
class DeclutterViewModel(private val holder: DeclutterStateHolder = DeclutterStateHolder()) : ViewModel() { val uiState = holder.state; fun onEvent(event: DeclutterUiEvent) = holder.onEvent(event) }
