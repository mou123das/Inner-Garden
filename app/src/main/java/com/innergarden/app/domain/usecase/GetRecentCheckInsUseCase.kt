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
            entities.toRecentDomain(limit)
        }

    suspend fun load(limit: Int? = null): List<DailyCheckIn> = repository.loadAll().toRecentDomain(limit)

    private fun List<com.innergarden.app.data.local.CheckInEntity>.toRecentDomain(limit: Int?): List<DailyCheckIn> {
        val recent = sortedByDescending { it.timestampEpochMillis }
        return (limit?.let(recent::take) ?: recent).map { it.toDomain(zoneId) }
    }
}
