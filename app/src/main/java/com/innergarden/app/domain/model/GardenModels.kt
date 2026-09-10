package com.innergarden.app.domain.model

enum class GardenStage(val displayName: String) {
    SEED("Seed"),
    SPROUT("Sprout"),
    YOUNG_TREE("Young Tree"),
    GROWING_TREE("Growing Tree"),
    BLOOMING_TREE("Blooming Tree")
}

data class GardenState(
    val stage: GardenStage,
    val totalCheckInDays: Int,
    val currentStreak: Int
)

data class WellbeingTrend(
    val moodAverage: Double,
    val stressAverage: Double,
    val energyAverage: Double,
    val sleepAverage: Double,
    val checkInCount: Int
)

data class ReflectionGuidance(
    val summary: String,
    val affirmation: String,
    val reflectionQuestion: String,
    val wellnessActivity: String
)

data class WeeklyDeclutter(
    val summary: String,
    val recurringThemes: List<String>,
    val carryForwardReflection: String,
    val reflectionQuestion: String
)
