package com.innergarden.app.domain.usecase

import com.innergarden.app.domain.model.DailyCheckIn
import com.innergarden.app.domain.repository.CheckInRepository
import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID

class SaveDailyCheckInUseCase(
    private val repository: CheckInRepository,
    private val clock: Clock = Clock.systemDefaultZone(),
    private val zoneId: ZoneId = ZoneId.systemDefault()
) {
    suspend operator fun invoke(mood: Int, stress: Int, energy: Int, sleep: Int, reflection: String?): Result<DailyCheckIn> {
        val ratings = listOf(mood, stress, energy, sleep)
        if (ratings.any { it !in 1..5 }) {
            return Result.failure(IllegalArgumentException("Ratings must be between 1 and 5."))
        }
        val checkIn = DailyCheckIn(
            id = UUID.randomUUID().toString(),
            timestamp = LocalDateTime.now(clock),
            mood = mood,
            stress = stress,
            energy = energy,
            sleep = sleep,
            reflection = reflection?.trim()?.takeIf { it.isNotEmpty() }
        )
        return runCatching {
            repository.save(checkIn.toEntity(zoneId))
            checkIn
        }
    }
}
