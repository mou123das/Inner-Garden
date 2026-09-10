package com.innergarden.app.domain.repository

import com.innergarden.app.data.local.CheckInEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

interface CheckInRepository {
    fun observeAll(): Flow<List<CheckInEntity>>
    suspend fun loadAll(): List<CheckInEntity> = observeAll().first()
    suspend fun save(checkIn: CheckInEntity)
}
