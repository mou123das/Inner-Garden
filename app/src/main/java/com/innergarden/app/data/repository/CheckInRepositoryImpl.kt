package com.innergarden.app.data.repository

import com.innergarden.app.data.local.CheckInDataSource
import com.innergarden.app.data.local.CheckInEntity
import com.innergarden.app.domain.repository.CheckInRepository
import kotlinx.coroutines.flow.Flow

class CheckInRepositoryImpl(private val dataSource: CheckInDataSource) : CheckInRepository {
    override fun observeAll(): Flow<List<CheckInEntity>> = dataSource.observeAll()
    override suspend fun loadAll(): List<CheckInEntity> = dataSource.loadAll()
    override suspend fun save(checkIn: CheckInEntity) = dataSource.save(checkIn)
}
