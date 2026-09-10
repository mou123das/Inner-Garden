package com.innergarden.app.domain.ai

import com.innergarden.app.domain.model.WeeklyDeclutter

sealed interface WeeklyDeclutterResult {
    data class Success(val declutter: WeeklyDeclutter) : WeeklyDeclutterResult
    data object Empty : WeeklyDeclutterResult
    data object Failure : WeeklyDeclutterResult
}

class GenerateWeeklyDeclutterUseCase(private val repository: AiRepository) {
    suspend operator fun invoke(reflections: List<String>): WeeklyDeclutterResult {
        val usable = reflections.map(String::trim).filter(String::isNotBlank)
        if (usable.isEmpty()) return WeeklyDeclutterResult.Empty

        return runCatching { repository.generateWeeklyDeclutter(usable) }
            .getOrNull()
            ?.takeIf(WeeklyDeclutter::isValid)
            ?.let(WeeklyDeclutterResult::Success)
            ?: WeeklyDeclutterResult.Failure
    }
}

private fun WeeklyDeclutter.isValid() =
    summary.isNotBlank() && recurringThemes.isNotEmpty() &&
        recurringThemes.all(String::isNotBlank) &&
        carryForwardReflection.isNotBlank() && reflectionQuestion.isNotBlank()
