package com.innergarden.app.feature.home

import androidx.lifecycle.ViewModel
import com.innergarden.app.domain.usecase.CalculateGardenGrowthUseCase
import com.innergarden.app.domain.usecase.CalculateWellbeingScoreUseCase
import com.innergarden.app.domain.usecase.GetRecentCheckInsUseCase
import java.time.LocalDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val greeting: String = "Good evening",
    val gardenStage: String = "Seed",
    val streak: String = "Start your reflection streak",
    val reflection: String = "Your next check-in will create today's reflection.",
    val hasCheckedInToday: Boolean = false,
    val wellbeingScore: Int? = null,
    val recentSummary: String = "No check-ins yet"
)

sealed interface HomeUiEvent { data object CheckIn : HomeUiEvent; data object ViewReflection : HomeUiEvent; data object Settings : HomeUiEvent }

class HomeStateHolder(
    recentCheckIns: GetRecentCheckInsUseCase,
    private val gardenGrowth: CalculateGardenGrowthUseCase,
    private val wellbeingScore: CalculateWellbeingScoreUseCase,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
) {
    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()
    init {
        scope.launch {
            recentCheckIns().collect { checkIns ->
                val garden = gardenGrowth(checkIns)
                val todayCheckIn = checkIns.firstOrNull { it.date == LocalDate.now() }
                _state.value = HomeUiState(
                    gardenStage = garden.stage.displayName,
                    streak = if (garden.currentStreak == 0) "Start your reflection streak" else garden.currentStreak.toString() + " day reflection streak",
                    reflection = if (todayCheckIn == null) "Your next check-in will create today's reflection." else "Today's check-in is safely part of your garden.",
                    hasCheckedInToday = todayCheckIn != null,
                    wellbeingScore = checkIns.firstOrNull()?.let(wellbeingScore::invoke),
                    recentSummary = if (checkIns.isEmpty()) "No check-ins yet" else minOf(7, checkIns.size).toString() + " recent check-ins"
                )
            }
        }
    }
    fun onEvent(event: HomeUiEvent) = Unit
    fun close() = scope.cancel()
}

class HomeViewModel(private val holder: HomeStateHolder) : ViewModel() {
    val uiState: StateFlow<HomeUiState> = holder.state
    fun onEvent(event: HomeUiEvent) = holder.onEvent(event)
    override fun onCleared() = holder.close()
}
