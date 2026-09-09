package com.innergarden.app.feature.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SettingsUiState(val rows: List<String> = listOf("Reminders", "Notifications", "About Inner Garden", "Privacy"), val version: String = "App version 1.0")
sealed interface SettingsUiEvent { data class RowSelected(val label: String) : SettingsUiEvent }
class SettingsStateHolder { private val _state = MutableStateFlow(SettingsUiState()); val state: StateFlow<SettingsUiState> = _state.asStateFlow(); fun onEvent(event: SettingsUiEvent) = Unit }
class SettingsViewModel(private val holder: SettingsStateHolder = SettingsStateHolder()) : ViewModel() { val uiState = holder.state; fun onEvent(event: SettingsUiEvent) = holder.onEvent(event) }
