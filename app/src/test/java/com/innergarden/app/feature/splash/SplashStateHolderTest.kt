package com.innergarden.app.feature.splash

import com.innergarden.app.domain.auth.AuthRepository
import com.innergarden.app.domain.auth.EnsureAuthenticatedUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SplashStateHolderTest {
    @Test
    fun fastAuthenticationStillWaitsForMinimumDuration() = runBlocking {
        val holder = SplashStateHolder(
            ensureAuthenticated = EnsureAuthenticatedUseCase(FakeAuthRepository()),
            minimumDurationMillis = 30L,
            scope = this
        )
        delay(10L)
        assertTrue(holder.state.value.isAuthenticated)
        assertFalse(holder.state.value.canNavigate)

        delay(35L)
        assertTrue(holder.state.value.canNavigate)
    }

    @Test
    fun slowAuthenticationDoesNotWaitAgainAfterMinimumDuration() = runBlocking {
        val holder = SplashStateHolder(
            ensureAuthenticated = EnsureAuthenticatedUseCase(FakeAuthRepository(signInDelayMillis = 60L)),
            minimumDurationMillis = 20L,
            scope = this
        )
        delay(30L)
        assertTrue(holder.state.value.minimumDurationElapsed)
        assertFalse(holder.state.value.canNavigate)

        delay(50L)
        assertTrue(holder.state.value.canNavigate)
    }

    private class FakeAuthRepository(
        private val signInDelayMillis: Long = 0L
    ) : AuthRepository {
        override fun currentUserId(): String? = null
        override suspend fun signInAnonymously(): String {
            delay(signInDelayMillis)
            return "synthetic-anonymous-user"
        }
    }
}
