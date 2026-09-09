package com.innergarden.app.domain.repository

import com.innergarden.app.data.local.CheckInEntity
import kotlinx.coroutines.flow.Flow

interface CheckInRepository {
    fun observeAll(): Flow<List<CheckInEntity>>
    suspend fun save(checkIn: CheckInEntity)
}
