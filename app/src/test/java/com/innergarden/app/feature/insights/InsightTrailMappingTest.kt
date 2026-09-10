package com.innergarden.app.feature.insights

import com.innergarden.app.domain.model.DailyCheckIn
import java.time.LocalDate
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class InsightTrailMappingTest {
    private val today = LocalDate.of(2026, 9, 10)

    @Test fun zeroCheckInsProducesSevenEmptySlots() {
        val slots = buildSevenDayPoints(emptyList(), today) { it.mood }
        assertEquals(7, slots.size)
        assertEquals(7, slots.count { it.value == null })
    }

    @Test fun oneCheckInUsesItsCalendarDaySlot() {
        val slots = buildSevenDayPoints(listOf(checkIn(today.minusDays(2), mood = 4)), today) { it.mood }
        assertEquals(.8f, slots[4].value)
        assertEquals(1, slots.count { it.value != null })
    }

    @Test fun twoCheckInsPreserveChronologicalPositions() {
        val slots = buildSevenDayPoints(listOf(checkIn(today, 5), checkIn(today.minusDays(6), 2)), today) { it.mood }
        assertEquals(.4f, slots.first().value)
        assertEquals(1f, slots.last().value)
    }

    @Test fun sevenCheckInsFillAllSlots() {
        val values = (0L..6L).map { checkIn(today.minusDays(it), (it % 5 + 1).toInt()) }
        val slots = buildSevenDayPoints(values, today) { it.mood }
        assertEquals(7, slots.count { it.value != null })
    }

    @Test fun missingDaysRemainEmptyInsteadOfBeingInterpolated() {
        val slots = buildSevenDayPoints(listOf(checkIn(today.minusDays(4), 3), checkIn(today.minusDays(2), 4)), today) { it.mood }
        assertEquals(.6f, slots[2].value)
        assertNull(slots[3].value)
        assertEquals(.8f, slots[4].value)
    }

    private fun checkIn(day: LocalDate, mood: Int) = DailyCheckIn(
        id = day.toString(),
        timestamp = day.atTime(LocalTime.NOON),
        mood = mood,
        stress = 3,
        energy = 3,
        sleep = 3,
        reflection = null
    )
}
