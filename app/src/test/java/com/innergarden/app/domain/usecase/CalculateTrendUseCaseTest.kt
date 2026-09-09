package com.innergarden.app.domain.usecase

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CalculateTrendUseCaseTest {
    private val useCase = CalculateTrendUseCase()
    private val today = LocalDate.of(2026, 3, 10)

    @Test fun noCheckInsReturnsNoTrend() {
        assertNull(useCase(emptyList()))
    }

    @Test fun fewerThanSevenAreAveraged() {
        val result = useCase(listOf(checkIn(today, mood = 2), checkIn(today.minusDays(1), mood = 4)))!!
        assertEquals(3.0, result.moodAverage, 0.001)
        assertEquals(2, result.checkInCount)
    }

    @Test fun onlyLatestSevenAreAveraged() {
        val data = (0L..8L).map { offset ->
            checkIn(today.minusDays(offset), mood = if (offset < 7) 5 else 1)
        }
        val result = useCase(data)!!
        assertEquals(5.0, result.moodAverage, 0.001)
        assertEquals(7, result.checkInCount)
    }
}
