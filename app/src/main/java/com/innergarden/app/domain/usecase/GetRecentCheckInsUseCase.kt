package com.innergarden.app.domain.usecase

import com.innergarden.app.domain.model.DailyCheckIn
import com.innergarden.app.domain.repository.CheckInRepository
import java.time.ZoneId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetRecentCheckInsUseCase(
    private val repository: CheckInRepository,
    private val zoneId: ZoneId = ZoneId.systemDefault()
) {
    operator fun invoke(limit: Int? = null): Flow<List<DailyCheckIn>> =
        repository.observeAll().map { entities ->
            val recent = entities.sortedByDescending { it.timestampEpochMillis }
            (limit?.let(recent::take) ?: recent).map { it.toDomain(zoneId) }
        }
}
