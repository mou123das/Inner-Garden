package com.innergarden.app.feature.checkin

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CheckInUiState(val mood: Int = 3, val stress: Int = 3, val energy: Int = 3, val sleep: Int = 3, val reflection: String = "")

sealed interface CheckInUiEvent {
    data class MoodChanged(val value: Int) : CheckInUiEvent
    data class StressChanged(val value: Int) : CheckInUiEvent
    data class EnergyChanged(val value: Int) : CheckInUiEvent
    data class SleepChanged(val value: Int) : CheckInUiEvent
    data class ReflectionChanged(val value: String) : CheckInUiEvent
    data object Save : CheckInUiEvent
}

class CheckInStateHolder {
    private val _state = MutableStateFlow(CheckInUiState())
    val state: StateFlow<CheckInUiState> = _state.asStateFlow()
    fun onEvent(event: CheckInUiEvent) {
        _state.update { current ->
            when (event) {
                is CheckInUiEvent.MoodChanged -> current.copy(mood = event.value)
                is CheckInUiEvent.StressChanged -> current.copy(stress = event.value)
                is CheckInUiEvent.EnergyChanged -> current.copy(energy = event.value)
                is CheckInUiEvent.SleepChanged -> current.copy(sleep = event.value)
                is CheckInUiEvent.ReflectionChanged -> current.copy(reflection = event.value)
                CheckInUiEvent.Save -> current
            }
        }
    }
}

class CheckInViewModel(private val holder: CheckInStateHolder = CheckInStateHolder()) : ViewModel() {
    val uiState: StateFlow<CheckInUiState> = holder.state
    fun onEvent(event: CheckInUiEvent) = holder.onEvent(event)
}
