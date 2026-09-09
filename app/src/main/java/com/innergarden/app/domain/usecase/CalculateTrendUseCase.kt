package com.innergarden.app.domain.usecase

import com.innergarden.app.domain.model.DailyCheckIn
import com.innergarden.app.domain.model.WellbeingTrend

class CalculateTrendUseCase {
    operator fun invoke(checkIns: List<DailyCheckIn>): WellbeingTrend? {
        val recent = checkIns.sortedByDescending { it.timestamp }.take(7)
        if (recent.isEmpty()) return null
        return WellbeingTrend(
            moodAverage = recent.map { it.mood }.average(),
            stressAverage = recent.map { it.stress }.average(),
            energyAverage = recent.map { it.energy }.average(),
            sleepAverage = recent.map { it.sleep }.average(),
            checkInCount = recent.size
        )
    }
}
