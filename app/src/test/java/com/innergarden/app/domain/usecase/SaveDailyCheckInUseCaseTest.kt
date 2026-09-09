package com.innergarden.app.domain.usecase

import com.innergarden.app.data.local.CheckInEntity
import com.innergarden.app.domain.repository.CheckInRepository
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SaveDailyCheckInUseCaseTest {
    private val repository = FakeRepository()
    private val zone = ZoneId.of("UTC")
    private val useCase = SaveDailyCheckInUseCase(repository, Clock.fixed(Instant.parse("2026-04-01T12:00:00Z"), zone), zone)

    @Test fun validBoundaryValuesAreSaved() = runBlocking {
        val result = useCase(1, 5, 1, 5, "  synthetic thought  ")
        assertTrue(result.isSuccess)
        assertEquals(1, repository.saved.size)
        assertEquals("synthetic thought", repository.saved.single().reflection)
    }

    @Test fun invalidValuesAreNotSaved() = runBlocking {
        assertTrue(useCase(0, 3, 3, 3, null).isFailure)
        assertTrue(useCase(3, 3, 3, 6, null).isFailure)
        assertTrue(repository.saved.isEmpty())
    }

    private class FakeRepository : CheckInRepository {
        val saved = mutableListOf<CheckInEntity>()
        private val flow = MutableStateFlow<List<CheckInEntity>>(emptyList())
        override fun observeAll(): Flow<List<CheckInEntity>> = flow
        override suspend fun save(checkIn: CheckInEntity) {
            saved += checkIn
            flow.value = saved.toList()
        }
    }
}
