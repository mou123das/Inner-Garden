package com.innergarden.app.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

data class DailyCheckIn(
    val id: String,
    val timestamp: LocalDateTime,
    val mood: Int,
    val stress: Int,
    val energy: Int,
    val sleep: Int,
    val reflection: String?,
    val localDate: LocalDate = timestamp.toLocalDate()
) {
    val date: LocalDate get() = localDate
}
