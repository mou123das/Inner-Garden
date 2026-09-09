package com.innergarden.app.feature.splash

import androidx.lifecycle.ViewModel
import com.innergarden.app.domain.auth.EnsureAuthenticatedUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class SplashUiState(
    val title: String = "Inner Garden",
    val subtitle: String = "Grow gently, one day at a time",
    val isAuthenticating: Boolean = false,
    val isAuthenticated: Boolean = false,
    val minimumDurationElapsed: Boolean = false,
    val errorMessage: String? = null
) {
    val canNavigate: Boolean get() = isAuthenticated && minimumDurationElapsed
}
sealed interface SplashUiEvent { data object Retry : SplashUiEvent }
class SplashStateHolder(
    private val ensureAuthenticated: EnsureAuthenticatedUseCase,
    private val minimumDurationMillis: Long = 1_500L,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
) {
    private val _state = MutableStateFlow(SplashUiState())
    val state: StateFlow<SplashUiState> = _state.asStateFlow()
    init {
        scope.launch {
            delay(minimumDurationMillis)
            _state.update { it.copy(minimumDurationElapsed = true) }
        }
        authenticate()
    }
    fun onEvent(event: SplashUiEvent) {
        if (event == SplashUiEvent.Retry) authenticate()
    }
    private fun authenticate() {
        if (_state.value.isAuthenticating && _state.value.errorMessage == null) return
        _state.update { it.copy(isAuthenticating = true, errorMessage = null) }
        scope.launch {
            val result = ensureAuthenticated()
            _state.update {
                if (result.isSuccess) it.copy(isAuthenticating = false, isAuthenticated = true)
                else it.copy(isAuthenticating = false, errorMessage = "Couldn't connect to your garden. Please try again.")
            }
        }
    }
    fun close() = scope.cancel()
}
class SplashViewModel(private val holder: SplashStateHolder) : ViewModel() {
    val uiState = holder.state
    fun onEvent(event: SplashUiEvent) = holder.onEvent(event)
    override fun onCleared() = holder.close()
}
