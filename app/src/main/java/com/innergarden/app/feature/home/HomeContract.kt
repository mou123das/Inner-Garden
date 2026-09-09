package com.innergarden.app.feature.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class HomeUiState(
    val greeting: String = "Good evening",
    val gardenStage: String = "Growing Tree",
    val streak: String = "4 day reflection streak",
    val reflection: String = "You gave yourself permission to move gently today."
)

sealed interface HomeUiEvent { data object CheckIn : HomeUiEvent; data object ViewReflection : HomeUiEvent; data object Settings : HomeUiEvent }

class HomeStateHolder {
    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()
    fun onEvent(event: HomeUiEvent) = Unit
}

class HomeViewModel(private val holder: HomeStateHolder = HomeStateHolder()) : ViewModel() {
    val uiState: StateFlow<HomeUiState> = holder.state
    fun onEvent(event: HomeUiEvent) = holder.onEvent(event)
}
