package com.innergarden.app.domain.usecase

import com.innergarden.app.domain.model.GardenStage
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class CalculateGardenGrowthUseCaseTest {
    private val useCase = CalculateGardenGrowthUseCase()
    private val today = LocalDate.of(2026, 2, 10)

    @Test fun noCheckInsIsSeedWithNoStreak() {
        val result = useCase(emptyList(), today)
        assertEquals(GardenStage.SEED, result.stage)
        assertEquals(0, result.currentStreak)
    }

    @Test fun stageUsesUniqueDaysAndIgnoresRatings() {
        val repeatedDay = listOf(
            checkIn(today, "a", mood = 1, stress = 5, energy = 1, sleep = 1),
            checkIn(today, "b", mood = 5, stress = 1, energy = 5, sleep = 5)
        )
        assertEquals(GardenStage.SPROUT, useCase(repeatedDay, today).stage)
        assertEquals(1, useCase(repeatedDay, today).totalCheckInDays)

        val elevenDays = (0L..10L).map { checkIn(today.minusDays(it), it.toString()) }
        assertEquals(GardenStage.BLOOMING_TREE, useCase(elevenDays, today).stage)
    }

    @Test fun consecutiveDaysCreateStreak() {
        val result = useCase((0L..3L).map { checkIn(today.minusDays(it)) }, today)
        assertEquals(4, result.currentStreak)
        assertEquals(GardenStage.YOUNG_TREE, result.stage)
    }

    @Test fun brokenAndOldStreaksStopSafely() {
        val broken = listOf(checkIn(today), checkIn(today.minusDays(2)))
        assertEquals(1, useCase(broken, today).currentStreak)
        assertEquals(0, useCase(listOf(checkIn(today.minusDays(3))), today).currentStreak)
    }
}
