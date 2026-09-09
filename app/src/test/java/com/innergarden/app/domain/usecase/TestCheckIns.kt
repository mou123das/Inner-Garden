package com.innergarden.app.domain.usecase

import com.innergarden.app.domain.model.DailyCheckIn
import java.time.LocalDate
import java.time.LocalTime

internal fun checkIn(
    day: LocalDate,
    id: String = day.toString(),
    mood: Int = 3,
    stress: Int = 3,
    energy: Int = 3,
    sleep: Int = 3
) = DailyCheckIn(id, day.atTime(LocalTime.NOON), mood, stress, energy, sleep, null)
