package com.innergarden.app.data.auth

interface AuthDataSource {
    fun currentUserId(): String?
    suspend fun signInAnonymously(): String
}
