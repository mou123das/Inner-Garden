package com.innergarden.app.data.local

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class InMemoryCheckInDataSourceTest {
    @Test
    fun savingSameLocalDateReplacesExistingCheckIn() = runBlocking {
        val dataSource = InMemoryCheckInDataSource()
        dataSource.save(entity("first", mood = 2))
        dataSource.save(entity("second", mood = 5))

        val saved = dataSource.observeAll().first()
        assertEquals(1, saved.size)
        assertEquals("second", saved.single().id)
        assertEquals(5, saved.single().mood)
    }

    private fun entity(id: String, mood: Int) = CheckInEntity(
        id = id,
        timestampEpochMillis = 1L,
        mood = mood,
        stress = 3,
        energy = 3,
        sleep = 3,
        reflection = null,
        localDate = "2026-09-10"
    )
}
