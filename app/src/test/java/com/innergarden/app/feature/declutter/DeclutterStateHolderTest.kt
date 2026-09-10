package com.innergarden.app.feature.declutter

import com.innergarden.app.data.local.CheckInEntity
import com.innergarden.app.domain.ai.AiRepository
import com.innergarden.app.domain.ai.GenerateWeeklyDeclutterUseCase
import com.innergarden.app.domain.model.ReflectionGuidance
import com.innergarden.app.domain.model.WeeklyDeclutter
import com.innergarden.app.domain.repository.CheckInRepository
import com.innergarden.app.domain.usecase.GetRecentCheckInsUseCase
import com.innergarden.app.domain.usecase.GetRecentReflectionsUseCase
import java.time.LocalDate
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class DeclutterStateHolderTest {
    @Test fun exposesLoadingSuccessAndPreventsConcurrentDuplicates() = runBlocking {
        val ai = WaitingAiRepository()
        val holder = holder(ai, this)

        holder.onEvent(DeclutterUiEvent.DeclutterWeek)
        holder.onEvent(DeclutterUiEvent.DeclutterWeek)
        delay(25)
        assertEquals(DeclutterStatus.LOADING, holder.state.value.status)
        assertEquals(1, ai.calls)

        ai.response.complete(validWeekly)
        delay(25)
        assertEquals(DeclutterStatus.LOADING, holder.state.value.status)
        delay(3_000)
        assertEquals(DeclutterStatus.CONTENT, holder.state.value.status)
        assertEquals(validWeekly, holder.state.value.declutter)
    }

    @Test fun aiErrorBecomesCalmErrorState() = runBlocking {
        val ai = WaitingAiRepository()
        val holder = holder(ai, this)
        holder.onEvent(DeclutterUiEvent.DeclutterWeek)
        delay(25)
        ai.response.completeExceptionally(IllegalStateException())
        delay(25)
        assertEquals(DeclutterStatus.LOADING, holder.state.value.status)
        delay(3_000)
        assertEquals(DeclutterStatus.ERROR, holder.state.value.status)
    }

    @Test fun emptyReflectionsShowEmptyWithoutCallingAi() = runBlocking {
        val ai = WaitingAiRepository()
        val holder = holder(ai, this, EmptyRecentRepository())

        holder.onEvent(DeclutterUiEvent.DeclutterWeek)
        delay(25)

        assertEquals(DeclutterStatus.EMPTY, holder.state.value.status)
        assertEquals(0, ai.calls)
    }

    private fun holder(ai: AiRepository, scope: CoroutineScope, repository: CheckInRepository = RecentRepository()) = DeclutterStateHolder(
        GetRecentReflectionsUseCase(GetRecentCheckInsUseCase(repository)),
        GenerateWeeklyDeclutterUseCase(ai),
        scope
    )
}

private class EmptyRecentRepository : CheckInRepository {
    override fun observeAll(): Flow<List<CheckInEntity>> = flowOf(emptyList())
    override suspend fun save(checkIn: CheckInEntity) = Unit
}

private class RecentRepository : CheckInRepository {
    override fun observeAll(): Flow<List<CheckInEntity>> = flowOf(
        listOf(CheckInEntity("1", 1L, 3, 3, 3, 3, "A synthetic reflection", LocalDate.now().toString()))
    )
    override suspend fun save(checkIn: CheckInEntity) = Unit
}

private class WaitingAiRepository : AiRepository {
    val response = CompletableDeferred<WeeklyDeclutter>()
    var calls = 0
    override suspend fun generateWeeklyDeclutter(reflections: List<String>): WeeklyDeclutter {
        calls++
        return response.await()
    }
    override suspend fun generateReflectionGuidance(reflection: String): ReflectionGuidance = error("Unused")
}

private val validWeekly = WeeklyDeclutter(
    "The week had a few quiet moments.",
    listOf("Quiet time helped", "Taking breaks", "Time outdoors"),
    "Make room for a pause.",
    "What helped?"
)
