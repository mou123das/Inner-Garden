package com.innergarden.app.domain.usecase

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class CalculateWellbeingScoreUseCaseTest {
    private val useCase = CalculateWellbeingScoreUseCase()

    @Test fun balancedValuesUseStressAdjustment() {
        assertEquals(60, useCase(checkIn(LocalDate.of(2026, 1, 1), mood = 3, stress = 3, energy = 3, sleep = 3)))
    }

    @Test fun boundaryValuesProduceExpectedScore() {
        assertEquals(100, useCase(checkIn(LocalDate.of(2026, 1, 1), mood = 5, stress = 1, energy = 5, sleep = 5)))
        assertEquals(20, useCase(checkIn(LocalDate.of(2026, 1, 1), mood = 1, stress = 5, energy = 1, sleep = 1)))
    }

    @Test fun invalidRatingIsRejected() {
        assertThrows(IllegalArgumentException::class.java) {
            useCase(checkIn(LocalDate.of(2026, 1, 1), mood = 0))
        }
    }
}
