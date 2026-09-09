package com.innergarden.app.feature.insights

import androidx.lifecycle.ViewModel
import com.innergarden.app.domain.model.DailyCheckIn
import com.innergarden.app.domain.usecase.CalculateTrendUseCase
import com.innergarden.app.domain.usecase.GetRecentCheckInsUseCase
import java.util.Locale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class InsightMetric(val label: String, val value: String, val points: List<Float>)
data class InsightsUiState(
    val metrics: List<InsightMetric> = emptyList(),
    val observation: String = "Complete a check-in to begin seeing your recent patterns.",
    val isEmpty: Boolean = true
)
sealed interface InsightsUiEvent { data object Viewed : InsightsUiEvent }
class InsightsStateHolder(
    recentCheckIns: GetRecentCheckInsUseCase,
    private val calculateTrend: CalculateTrendUseCase,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
) {
    private val _state = MutableStateFlow(InsightsUiState())
    val state: StateFlow<InsightsUiState> = _state.asStateFlow()
    init {
        scope.launch {
            recentCheckIns(7).collect { checkIns ->
                val trend = calculateTrend(checkIns)
                _state.value = if (trend == null) InsightsUiState() else InsightsUiState(
                    metrics = listOf(
                        metric("Mood", trend.moodAverage, checkIns) { it.mood },
                        metric("Energy", trend.energyAverage, checkIns) { it.energy },
                        metric("Stress", trend.stressAverage, checkIns) { it.stress },
                        metric("Sleep", trend.sleepAverage, checkIns) { it.sleep }
                    ),
                    observation = "These averages reflect your " + trend.checkInCount + " most recent check-in" + if (trend.checkInCount == 1) "." else "s.",
                    isEmpty = false
                )
            }
        }
    }
    private fun metric(label: String, average: Double, values: List<DailyCheckIn>, selector: (DailyCheckIn) -> Int) =
        InsightMetric(label, String.format(Locale.US, "%.1f / 5", average), values.asReversed().map { selector(it) / 5f })
    fun onEvent(event: InsightsUiEvent) = Unit
    fun close() = scope.cancel()
}
class InsightsViewModel(private val holder: InsightsStateHolder) : ViewModel() {
    val uiState = holder.state
    fun onEvent(event: InsightsUiEvent) = holder.onEvent(event)
    override fun onCleared() = holder.close()
}
