package com.innergarden.app.domain.ai

import com.innergarden.app.domain.model.ReflectionGuidance
import com.innergarden.app.domain.usecase.GetPlaceholderReflectionGuidanceUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class GenerateReflectionGuidanceUseCaseTest {
    private val fallback = GetPlaceholderReflectionGuidanceUseCase()

    @Test fun validReflectionCallsRepository() = runBlocking {
        val repository = FakeAiRepository()
        val result = GenerateReflectionGuidanceUseCase(repository, fallback)("A synthetic reflection")
        assertEquals(validGuidance, result)
        assertEquals(1, repository.reflectionCalls)
    }

    @Test fun blankReflectionSkipsRepository() = runBlocking {
        val repository = FakeAiRepository()
        val result = GenerateReflectionGuidanceUseCase(repository, fallback)("  ")
        assertEquals(fallback(), result)
        assertEquals(0, repository.reflectionCalls)
    }

    @Test fun repositoryErrorUsesFallback() = runBlocking {
        val repository = FakeAiRepository(reflectionResult = Result.failure(IllegalStateException()))
        assertEquals(fallback(), GenerateReflectionGuidanceUseCase(repository, fallback)("A thought"))
    }

    @Test fun malformedResultUsesFallback() = runBlocking {
        val repository = FakeAiRepository(
            reflectionResult = Result.success(ReflectionGuidance("", "Affirmation", "Question?", "Activity"))
        )
        assertEquals(fallback(), GenerateReflectionGuidanceUseCase(repository, fallback)("A thought"))
    }
}
