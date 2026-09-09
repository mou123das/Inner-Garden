package com.innergarden.app.feature.checkin

import com.innergarden.app.data.local.CheckInEntity
import com.innergarden.app.domain.repository.CheckInRepository
import com.innergarden.app.domain.usecase.SaveDailyCheckInUseCase
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CheckInStateHolderTest {
    @Test
    fun timeoutStopsSavingPreservesInputAndAllowsRetry() = runBlocking {
        val holder = CheckInStateHolder(
            saveDailyCheckIn = SaveDailyCheckInUseCase(HangingRepository()),
            saveTimeoutMillis = 25L,
            scope = this
        )
        holder.onEvent(CheckInUiEvent.ReflectionChanged("Keep this reflection"))
        holder.onEvent(CheckInUiEvent.Save)
        assertTrue(holder.state.value.isSaving)

        delay(75L)

        assertFalse(holder.state.value.isSaving)
        assertFalse(holder.state.value.saveCompleted)
        assertEquals("Keep this reflection", holder.state.value.reflection)
        assertEquals(
            "We couldn't confirm your check-in yet. Check your connection and try again.",
            holder.state.value.errorMessage
        )

        holder.onEvent(CheckInUiEvent.Save)
        assertTrue(holder.state.value.isSaving)
    }

    @Test
    fun confirmedWriteCompletesSave() = runBlocking {
        val holder = CheckInStateHolder(
            saveDailyCheckIn = SaveDailyCheckInUseCase(ImmediateRepository()),
            saveTimeoutMillis = 100L,
            scope = this
        )
        holder.onEvent(CheckInUiEvent.Save)
        delay(25L)
        assertFalse(holder.state.value.isSaving)
        assertTrue(holder.state.value.saveCompleted)
    }

    private class HangingRepository : CheckInRepository {
        override fun observeAll(): Flow<List<CheckInEntity>> = flowOf(emptyList())
        override suspend fun save(checkIn: CheckInEntity): Unit = awaitCancellation()
    }

    private class ImmediateRepository : CheckInRepository {
        override fun observeAll(): Flow<List<CheckInEntity>> = flowOf(emptyList())
        override suspend fun save(checkIn: CheckInEntity) = Unit
    }
}
