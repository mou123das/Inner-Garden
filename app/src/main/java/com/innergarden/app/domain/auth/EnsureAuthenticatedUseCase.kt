package com.innergarden.app.domain.auth

class EnsureAuthenticatedUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(): Result<String> {
        val existingUserId = repository.currentUserId()
        if (!existingUserId.isNullOrBlank()) return Result.success(existingUserId)
        return runCatching { repository.signInAnonymously() }
            .mapCatching { it.takeIf(String::isNotBlank) ?: error("Authentication did not provide a user.") }
    }
}
