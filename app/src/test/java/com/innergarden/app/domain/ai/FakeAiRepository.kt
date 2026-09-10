package com.innergarden.app.domain.ai

import com.innergarden.app.domain.model.ReflectionGuidance
import com.innergarden.app.domain.model.WeeklyDeclutter

internal class FakeAiRepository(
    var reflectionResult: Result<ReflectionGuidance> = Result.success(validGuidance),
    var weeklyResult: Result<WeeklyDeclutter> = Result.success(validWeekly)
) : AiRepository {
    var reflectionCalls = 0
    var weeklyCalls = 0

    override suspend fun generateReflectionGuidance(reflection: String): ReflectionGuidance {
        reflectionCalls++
        return reflectionResult.getOrThrow()
    }

    override suspend fun generateWeeklyDeclutter(reflections: List<String>): WeeklyDeclutter {
        weeklyCalls++
        return weeklyResult.getOrThrow()
    }
}

internal val validGuidance = ReflectionGuidance("Summary", "Affirmation", "Question?", "Take a short break.")
internal val validWeekly = WeeklyDeclutter("A steady week.", listOf("Quiet time helped"), "Make room for rest.", "What helped?")
