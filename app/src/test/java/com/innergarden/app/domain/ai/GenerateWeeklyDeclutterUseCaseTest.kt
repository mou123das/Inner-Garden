package com.innergarden.app.domain.ai

import com.innergarden.app.domain.model.WeeklyDeclutter
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GenerateWeeklyDeclutterUseCaseTest {
    @Test fun nonEmptyReflectionsInvokeAi() = runBlocking {
        val repository = FakeAiRepository()
        val result = GenerateWeeklyDeclutterUseCase(repository)(listOf("A synthetic reflection"))
        assertEquals(WeeklyDeclutterResult.Success(validWeekly), result)
        assertEquals(1, repository.weeklyCalls)
    }

    @Test fun emptyReflectionsSkipAi() = runBlocking {
        val repository = FakeAiRepository()
        assertEquals(WeeklyDeclutterResult.Empty, GenerateWeeklyDeclutterUseCase(repository)(listOf(" ")))
        assertEquals(0, repository.weeklyCalls)
    }

    @Test fun repositoryErrorIsFailure() = runBlocking {
        val repository = FakeAiRepository(weeklyResult = Result.failure(IllegalStateException()))
        assertEquals(WeeklyDeclutterResult.Failure, GenerateWeeklyDeclutterUseCase(repository)(listOf("Reflection")))
    }

    @Test fun malformedResultIsFailure() = runBlocking {
        val repository = FakeAiRepository(
            weeklyResult = Result.success(WeeklyDeclutter("Summary", emptyList(), "Carry", "Question?"))
        )
        assertTrue(GenerateWeeklyDeclutterUseCase(repository)(listOf("Reflection")) is WeeklyDeclutterResult.Failure)
    }

    @Test fun multipleGeneratedThemesArePreserved() = runBlocking {
        val themes = listOf("Focused mornings", "Taking breaks", "Time outdoors")
        val repository = FakeAiRepository(
            weeklyResult = Result.success(WeeklyDeclutter("Summary", themes, "Carry", "Question?"))
        )

        val result = GenerateWeeklyDeclutterUseCase(repository)(listOf("One", "Two", "Three"))

        assertEquals(WeeklyDeclutterResult.Success(WeeklyDeclutter("Summary", themes, "Carry", "Question?")), result)
    }
}
