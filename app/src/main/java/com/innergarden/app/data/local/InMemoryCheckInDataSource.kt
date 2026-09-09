package com.innergarden.app.data.local

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class InMemoryCheckInDataSource(seedData: List<CheckInEntity> = emptyList()) : CheckInDataSource {
    private val mutex = Mutex()
    private val checkIns = MutableStateFlow(seedData.sortedByDescending { it.timestampEpochMillis })

    override fun observeAll(): Flow<List<CheckInEntity>> = checkIns.asStateFlow()

    override suspend fun save(checkIn: CheckInEntity) {
        mutex.withLock {
            val withoutSameDay = if (checkIn.localDate.isBlank()) checkIns.value else {
                checkIns.value.filterNot { it.localDate == checkIn.localDate }
            }
            checkIns.value = (withoutSameDay + checkIn).sortedByDescending { it.timestampEpochMillis }
        }
    }
}
