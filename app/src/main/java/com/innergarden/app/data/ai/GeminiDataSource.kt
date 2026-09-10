package com.innergarden.app.data.ai

data class GeminiReflectionGuidance(
    val summary: String,
    val affirmation: String,
    val reflectionQuestion: String,
    val wellnessActivity: String
)

data class GeminiWeeklyDeclutter(
    val summary: String,
    val recurringThemes: List<String>,
    val carryForwardReflection: String,
    val reflectionQuestion: String
)

interface GeminiDataSource {
    suspend fun generateReflectionGuidance(reflection: String): GeminiReflectionGuidance
    suspend fun generateWeeklyDeclutter(reflections: List<String>): GeminiWeeklyDeclutter
}
