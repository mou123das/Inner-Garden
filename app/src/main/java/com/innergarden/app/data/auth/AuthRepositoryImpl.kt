package com.innergarden.app.data.auth

import com.innergarden.app.domain.auth.AuthRepository

class AuthRepositoryImpl(private val dataSource: AuthDataSource) : AuthRepository {
    override fun currentUserId(): String? = dataSource.currentUserId()
    override suspend fun signInAnonymously(): String = dataSource.signInAnonymously()
}
