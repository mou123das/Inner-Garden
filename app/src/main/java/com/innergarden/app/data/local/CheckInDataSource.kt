package com.innergarden.app.data.local

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

interface CheckInDataSource {
    fun observeAll(): Flow<List<CheckInEntity>>
    suspend fun loadAll(): List<CheckInEntity> = observeAll().first()
    suspend fun save(checkIn: CheckInEntity)
}
