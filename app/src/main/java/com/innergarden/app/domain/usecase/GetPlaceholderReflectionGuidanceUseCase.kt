package com.innergarden.app.domain.usecase

import com.innergarden.app.domain.model.ReflectionGuidance

class GetPlaceholderReflectionGuidanceUseCase {
    operator fun invoke() = ReflectionGuidance(
        summary = "Thanks for taking a moment to check in with yourself today.",
        affirmation = "Small moments of awareness can still be meaningful.",
        reflectionQuestion = "What is one thing from today that you would like to carry forward?",
        wellnessActivity = "Take a few quiet minutes away from your screen."
    )
}
