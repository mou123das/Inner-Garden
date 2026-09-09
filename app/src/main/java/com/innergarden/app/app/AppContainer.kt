package com.innergarden.app.app

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.innergarden.app.data.auth.AuthRepositoryImpl
import com.innergarden.app.data.auth.FirebaseAuthDataSource
import com.innergarden.app.data.firestore.FirestoreCheckInDataSource
import com.innergarden.app.data.repository.CheckInRepositoryImpl
import com.innergarden.app.domain.auth.EnsureAuthenticatedUseCase
import com.innergarden.app.domain.usecase.CalculateGardenGrowthUseCase
import com.innergarden.app.domain.usecase.CalculateTrendUseCase
import com.innergarden.app.domain.usecase.CalculateWellbeingScoreUseCase
import com.innergarden.app.domain.usecase.GetPlaceholderReflectionGuidanceUseCase
import com.innergarden.app.domain.usecase.GetRecentCheckInsUseCase
import com.innergarden.app.domain.usecase.SaveDailyCheckInUseCase

object AppContainer {
    private val authDataSource = FirebaseAuthDataSource(FirebaseAuth.getInstance())
    private val authRepository = AuthRepositoryImpl(authDataSource)
    private val dataSource = FirestoreCheckInDataSource(FirebaseFirestore.getInstance(), authDataSource)
    private val repository = CheckInRepositoryImpl(dataSource)

    val ensureAuthenticated = EnsureAuthenticatedUseCase(authRepository)
    val getRecentCheckIns = GetRecentCheckInsUseCase(repository)
    val saveDailyCheckIn = SaveDailyCheckInUseCase(repository)
    val calculateWellbeingScore = CalculateWellbeingScoreUseCase()
    val calculateGardenGrowth = CalculateGardenGrowthUseCase()
    val calculateTrend = CalculateTrendUseCase()
    val getPlaceholderReflectionGuidance = GetPlaceholderReflectionGuidanceUseCase()
}
