package com.innergarden.app.domain.usecase

import com.innergarden.app.domain.model.DailyCheckIn
import kotlin.math.roundToInt

class CalculateWellbeingScoreUseCase {
    /**
     * This is an Inner Garden product wellbeing indicator based only on user-entered check-in
     * values. It is NOT a psychological assessment, medical score, diagnostic score, or clinical
     * measurement. Gemini must never calculate or alter it.
     */
    operator fun invoke(checkIn: DailyCheckIn): Int {
        require(listOf(checkIn.mood, checkIn.stress, checkIn.energy, checkIn.sleep).all { it in 1..5 })
        val stressAdjusted = 6 - checkIn.stress
        val average = (checkIn.mood + stressAdjusted + checkIn.energy + checkIn.sleep) / 4.0
        return (average / 5.0 * 100.0).roundToInt()
    }
}
