package com.innergarden.app.feature.reflection

import com.innergarden.app.domain.ai.FakeAiRepository
import com.innergarden.app.domain.ai.GenerateReflectionGuidanceUseCase
import com.innergarden.app.domain.ai.validGuidance
import com.innergarden.app.domain.usecase.GetPlaceholderReflectionGuidanceUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReflectionStateHolderTest {
    @Test fun generatesOnceAndExposesLoadingThenSuccess() = runBlocking {
        val repository = FakeAiRepository()
        val fallback = GetPlaceholderReflectionGuidanceUseCase()
        val holder = ReflectionStateHolder(
            "A synthetic reflection",
            GenerateReflectionGuidanceUseCase(repository, fallback),
            fallback(),
            this
        )
        assertTrue(holder.state.value.isLoading)
        delay(25)
        assertTrue(holder.state.value.isLoading)
        delay(3_000)
        assertFalse(holder.state.value.isLoading)
        assertEquals(validGuidance, holder.state.value.guidance)
        assertEquals(1, repository.reflectionCalls)
    }

    @Test fun blankReflectionIsImmediateFallbackWithoutAi() = runBlocking {
        val repository = FakeAiRepository()
        val fallback = GetPlaceholderReflectionGuidanceUseCase()
        val holder = ReflectionStateHolder(" ", GenerateReflectionGuidanceUseCase(repository, fallback), fallback(), this)
        assertFalse(holder.state.value.isLoading)
        assertEquals(fallback(), holder.state.value.guidance)
        assertEquals(0, repository.reflectionCalls)
    }
}
