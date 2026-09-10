package com.innergarden.app.domain.ai

import com.innergarden.app.domain.model.ReflectionGuidance
import com.innergarden.app.domain.usecase.GetPlaceholderReflectionGuidanceUseCase

class GenerateReflectionGuidanceUseCase(
    private val repository: AiRepository,
    private val placeholderGuidance: GetPlaceholderReflectionGuidanceUseCase
) {
    suspend operator fun invoke(reflection: String): ReflectionGuidance {
        val trimmed = reflection.trim()
        if (trimmed.isBlank()) return placeholderGuidance()

        return runCatching { repository.generateReflectionGuidance(trimmed) }
            .getOrNull()
            ?.takeIf(ReflectionGuidance::isValid)
            ?: placeholderGuidance()
    }
}

private fun ReflectionGuidance.isValid() =
    summary.isNotBlank() && affirmation.isNotBlank() &&
        reflectionQuestion.isNotBlank() && wellnessActivity.isNotBlank()
