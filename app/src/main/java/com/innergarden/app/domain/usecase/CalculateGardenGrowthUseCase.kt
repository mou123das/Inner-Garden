package com.innergarden.app.domain.usecase

import com.innergarden.app.domain.model.DailyCheckIn
import com.innergarden.app.domain.model.GardenStage
import com.innergarden.app.domain.model.GardenState
import java.time.LocalDate

class CalculateGardenGrowthUseCase {
    operator fun invoke(checkIns: List<DailyCheckIn>, today: LocalDate = LocalDate.now()): GardenState {
        val uniqueDays = checkIns.map { it.date }.distinct().sortedDescending()
        val stage = when (uniqueDays.size) {
            0 -> GardenStage.SEED
            in 1..2 -> GardenStage.SPROUT
            in 3..5 -> GardenStage.YOUNG_TREE
            in 6..10 -> GardenStage.GROWING_TREE
            else -> GardenStage.BLOOMING_TREE
        }
        return GardenState(stage, uniqueDays.size, calculateStreak(uniqueDays, today))
    }

    private fun calculateStreak(daysDescending: List<LocalDate>, today: LocalDate): Int {
        if (daysDescending.isEmpty()) return 0
        val latest = daysDescending.first()
        if (latest != today && latest != today.minusDays(1)) return 0
        var expected = latest
        var streak = 0
        for (day in daysDescending) {
            if (day == expected) {
                streak++
                expected = expected.minusDays(1)
            } else if (day.isBefore(expected)) {
                break
            }
        }
        return streak
    }
}
