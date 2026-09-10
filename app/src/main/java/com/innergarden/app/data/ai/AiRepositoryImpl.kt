package com.innergarden.app.data.ai

import com.innergarden.app.domain.ai.AiRepository
import com.innergarden.app.domain.model.ReflectionGuidance
import com.innergarden.app.domain.model.WeeklyDeclutter

class AiRepositoryImpl(private val dataSource: GeminiDataSource) : AiRepository {
    override suspend fun generateReflectionGuidance(reflection: String): ReflectionGuidance =
        dataSource.generateReflectionGuidance(reflection).let {
            ReflectionGuidance(it.summary, it.affirmation, it.reflectionQuestion, it.wellnessActivity)
        }

    override suspend fun generateWeeklyDeclutter(reflections: List<String>): WeeklyDeclutter =
        dataSource.generateWeeklyDeclutter(reflections).let {
            WeeklyDeclutter(it.summary, it.recurringThemes, it.carryForwardReflection, it.reflectionQuestion)
        }
}
