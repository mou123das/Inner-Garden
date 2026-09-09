package com.innergarden.app.feature.checkin

import androidx.lifecycle.ViewModel
import com.innergarden.app.domain.usecase.SaveDailyCheckInUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CheckInUiState(
    val mood: Int = 3,
    val stress: Int = 3,
    val energy: Int = 3,
    val sleep: Int = 3,
    val reflection: String = "",
    val isSaving: Boolean = false,
    val saveCompleted: Boolean = false,
    val errorMessage: String? = null
)

sealed interface CheckInUiEvent {
    data class MoodChanged(val value: Int) : CheckInUiEvent
    data class StressChanged(val value: Int) : CheckInUiEvent
    data class EnergyChanged(val value: Int) : CheckInUiEvent
    data class SleepChanged(val value: Int) : CheckInUiEvent
    data class ReflectionChanged(val value: String) : CheckInUiEvent
    data object Save : CheckInUiEvent
    data object SaveHandled : CheckInUiEvent
}

class CheckInStateHolder(
    private val saveDailyCheckIn: SaveDailyCheckInUseCase,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
) {
    private val _state = MutableStateFlow(CheckInUiState())
    val state: StateFlow<CheckInUiState> = _state.asStateFlow()
    fun onEvent(event: CheckInUiEvent) {
        if (event == CheckInUiEvent.Save) {
            save()
            return
        }
        _state.update { current ->
            when (event) {
                is CheckInUiEvent.MoodChanged -> current.copy(mood = event.value.coerceIn(1, 5))
                is CheckInUiEvent.StressChanged -> current.copy(stress = event.value.coerceIn(1, 5))
                is CheckInUiEvent.EnergyChanged -> current.copy(energy = event.value.coerceIn(1, 5))
                is CheckInUiEvent.SleepChanged -> current.copy(sleep = event.value.coerceIn(1, 5))
                is CheckInUiEvent.ReflectionChanged -> current.copy(reflection = event.value)
                CheckInUiEvent.SaveHandled -> current.copy(saveCompleted = false)
                CheckInUiEvent.Save -> current
            }
        }
    }

    private fun save() {
        val current = _state.value
        if (current.isSaving || current.saveCompleted) return
        _state.update { it.copy(isSaving = true, errorMessage = null) }
        scope.launch {
            val result = saveDailyCheckIn(current.mood, current.stress, current.energy, current.sleep, current.reflection)
            _state.update {
                if (result.isSuccess) it.copy(isSaving = false, saveCompleted = true)
                else it.copy(isSaving = false, errorMessage = "We couldn't save your check-in. Please try again.")
            }
        }
    }

    fun close() = scope.cancel()
}

class CheckInViewModel(private val holder: CheckInStateHolder) : ViewModel() {
    val uiState: StateFlow<CheckInUiState> = holder.state
    fun onEvent(event: CheckInUiEvent) = holder.onEvent(event)
    override fun onCleared() = holder.close()
}
