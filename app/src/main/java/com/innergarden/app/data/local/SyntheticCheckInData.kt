package com.innergarden.app.data.local

import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

/** Removable development-only data used to make the in-memory Phase 2 experience demonstrable. */
object SyntheticCheckInData {
    fun create(today: LocalDate = LocalDate.now()): List<CheckInEntity> {
        val reflections = listOf(
            "Had a busy day and took a short walk.",
            "Spent some quiet time after work.",
            "Had a productive morning and rested in the evening."
        )
        return (1L..7L).map { daysAgo ->
            val timestamp = today.minusDays(daysAgo).atTime(LocalTime.of(19, 0))
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val index = daysAgo.toInt()
            CheckInEntity(
                id = "synthetic-day-$daysAgo",
                timestampEpochMillis = timestamp,
                mood = 3 + (index % 3),
                stress = 2 + (index % 3),
                energy = 3 + (index % 2),
                sleep = 3 + ((index + 1) % 2),
                reflection = reflections[index % reflections.size]
            )
        }
    }
}
