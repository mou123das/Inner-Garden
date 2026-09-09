package com.innergarden.app.domain.auth

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EnsureAuthenticatedUseCaseTest {
    @Test
    fun existingUserIsReusedWithoutSigningInAgain() = runBlocking {
        val repository = FakeAuthRepository(current = "existing-anonymous-user")
        val result = EnsureAuthenticatedUseCase(repository)()
        assertEquals("existing-anonymous-user", result.getOrNull())
        assertEquals(0, repository.signInCalls)
    }

    @Test
    fun missingUserIsSignedInAnonymously() = runBlocking {
        val repository = FakeAuthRepository(current = null)
        val result = EnsureAuthenticatedUseCase(repository)()
        assertEquals("new-anonymous-user", result.getOrNull())
        assertEquals(1, repository.signInCalls)
    }

    @Test
    fun authenticationFailureIsReturned() = runBlocking {
        val repository = FakeAuthRepository(current = null, shouldFail = true)
        assertTrue(EnsureAuthenticatedUseCase(repository)().isFailure)
    }

    private class FakeAuthRepository(
        private val current: String?,
        private val shouldFail: Boolean = false
    ) : AuthRepository {
        var signInCalls = 0
        override fun currentUserId(): String? = current
        override suspend fun signInAnonymously(): String {
            signInCalls++
            if (shouldFail) error("Synthetic authentication failure")
            return "new-anonymous-user"
        }
    }
}
