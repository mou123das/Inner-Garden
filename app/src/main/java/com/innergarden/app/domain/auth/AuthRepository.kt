package com.innergarden.app.domain.auth

interface AuthRepository {
    fun currentUserId(): String?
    suspend fun signInAnonymously(): String
}
