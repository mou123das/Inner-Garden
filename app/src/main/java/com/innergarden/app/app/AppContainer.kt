package com.innergarden.app.app

import com.innergarden.app.data.local.InMemoryCheckInDataSource
import com.innergarden.app.data.local.SyntheticCheckInData
import com.innergarden.app.data.repository.CheckInRepositoryImpl
import com.innergarden.app.domain.usecase.CalculateGardenGrowthUseCase
import com.innergarden.app.domain.usecase.CalculateTrendUseCase
import com.innergarden.app.domain.usecase.CalculateWellbeingScoreUseCase
import com.innergarden.app.domain.usecase.GetPlaceholderReflectionGuidanceUseCase
import com.innergarden.app.domain.usecase.GetRecentCheckInsUseCase
import com.innergarden.app.domain.usecase.SaveDailyCheckInUseCase

object AppContainer {
    private val dataSource = InMemoryCheckInDataSource(SyntheticCheckInData.create())
    private val repository = CheckInRepositoryImpl(dataSource)

    val getRecentCheckIns = GetRecentCheckInsUseCase(repository)
    val saveDailyCheckIn = SaveDailyCheckInUseCase(repository)
    val calculateWellbeingScore = CalculateWellbeingScoreUseCase()
    val calculateGardenGrowth = CalculateGardenGrowthUseCase()
    val calculateTrend = CalculateTrendUseCase()
    val getPlaceholderReflectionGuidance = GetPlaceholderReflectionGuidanceUseCase()
}
