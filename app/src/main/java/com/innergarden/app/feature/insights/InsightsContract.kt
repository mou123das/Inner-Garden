package com.innergarden.app.feature.insights

import androidx.lifecycle.ViewModel
import com.innergarden.app.domain.model.DailyCheckIn
import com.innergarden.app.domain.usecase.CalculateTrendUseCase
import com.innergarden.app.domain.usecase.GetRecentCheckInsUseCase
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.catch

data class DailyMetricPoint(val dayLabel: String, val value: Float?)
data class InsightMetric(val label: String, val value: String, val points: List<DailyMetricPoint>)
data class InsightsUiState(
    val metrics: List<InsightMetric> = emptyList(),
    val observation: String = "Complete a check-in to begin seeing your recent patterns.",
    val isEmpty: Boolean = true,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
sealed interface InsightsUiEvent { data object Viewed : InsightsUiEvent; data object Retry : InsightsUiEvent }
class InsightsStateHolder(
    private val recentCheckIns: GetRecentCheckInsUseCase,
    private val calculateTrend: CalculateTrendUseCase,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
) {
    private val _state = MutableStateFlow(InsightsUiState())
    val state: StateFlow<InsightsUiState> = _state.asStateFlow()
    private var loadJob: Job? = null
    init { load() }
    private fun load() {
        loadJob?.cancel()
        _state.value = _state.value.copy(isLoading = true, errorMessage = null)
        loadJob = scope.launch {
            recentCheckIns(7)
                .catch { _state.value = _state.value.copy(isLoading = false, errorMessage = "Couldn't load your insights right now. Please try again.") }
                .collect { checkIns ->
                val trend = calculateTrend(checkIns)
                _state.value = if (trend == null) InsightsUiState(isLoading = false) else InsightsUiState(
                    metrics = listOf(
                        metric("Mood", trend.moodAverage, checkIns) { it.mood },
                        metric("Energy", trend.energyAverage, checkIns) { it.energy },
                        metric("Stress", trend.stressAverage, checkIns) { it.stress },
                        metric("Sleep", trend.sleepAverage, checkIns) { it.sleep }
                    ),
                    observation = "These averages reflect your " + trend.checkInCount + " most recent check-in" + if (trend.checkInCount == 1) "." else "s.",
                    isEmpty = false,
                    isLoading = false
                )
            }
        }
    }
    private fun metric(label: String, average: Double, values: List<DailyCheckIn>, selector: (DailyCheckIn) -> Int) =
        InsightMetric(label, String.format(Locale.US, "%.1f / 5", average), buildSevenDayPoints(values, selector = selector))
    fun onEvent(event: InsightsUiEvent) { if (event == InsightsUiEvent.Retry) load() }
    fun close() = scope.cancel()
}

internal fun buildSevenDayPoints(
    checkIns: List<DailyCheckIn>,
    today: LocalDate = LocalDate.now(),
    selector: (DailyCheckIn) -> Int
): List<DailyMetricPoint> {
    val latestByDate = checkIns.groupBy(DailyCheckIn::date).mapValues { (_, entries) -> entries.maxBy { it.timestamp } }
    return (6L downTo 0L).map { daysAgo ->
        val date = today.minusDays(daysAgo)
        DailyMetricPoint(
            dayLabel = date.dayOfWeek.getDisplayName(TextStyle.NARROW, Locale.US),
            value = latestByDate[date]?.let { selector(it).coerceIn(1, 5) / 5f }
        )
    }
}
class InsightsViewModel(private val holder: InsightsStateHolder) : ViewModel() {
    val uiState = holder.state
    fun onEvent(event: InsightsUiEvent) = holder.onEvent(event)
    override fun onCleared() = holder.close()
}
