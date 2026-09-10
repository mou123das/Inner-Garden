package com.innergarden.app.domain.ai

import com.innergarden.app.domain.model.ReflectionGuidance
import com.innergarden.app.domain.model.WeeklyDeclutter

interface AiRepository {
    suspend fun generateReflectionGuidance(reflection: String): ReflectionGuidance
    suspend fun generateWeeklyDeclutter(reflections: List<String>): WeeklyDeclutter
}
