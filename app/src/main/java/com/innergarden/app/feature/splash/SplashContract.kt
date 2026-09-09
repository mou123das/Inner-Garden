package com.innergarden.app.feature.splash

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SplashUiState(val title: String = "Inner Garden", val subtitle: String = "Grow gently, one day at a time")
sealed interface SplashUiEvent { data object Finished : SplashUiEvent }
class SplashStateHolder { private val _state = MutableStateFlow(SplashUiState()); val state: StateFlow<SplashUiState> = _state.asStateFlow(); fun onEvent(event: SplashUiEvent) = Unit }
class SplashViewModel(private val holder: SplashStateHolder = SplashStateHolder()) : ViewModel() { val uiState = holder.state; fun onEvent(event: SplashUiEvent) = holder.onEvent(event) }
