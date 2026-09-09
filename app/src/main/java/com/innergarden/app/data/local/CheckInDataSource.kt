package com.innergarden.app.data.local

import kotlinx.coroutines.flow.Flow

interface CheckInDataSource {
    fun observeAll(): Flow<List<CheckInEntity>>
    suspend fun save(checkIn: CheckInEntity)
}
