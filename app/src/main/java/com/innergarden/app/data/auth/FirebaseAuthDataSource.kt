package com.innergarden.app.data.auth

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class FirebaseAuthDataSource(private val firebaseAuth: FirebaseAuth) : AuthDataSource {
    override fun currentUserId(): String? = firebaseAuth.currentUser?.uid?.takeIf { it.isNotBlank() }

    override suspend fun signInAnonymously(): String {
        val result = firebaseAuth.signInAnonymously().await()
        return result.user?.uid?.takeIf { it.isNotBlank() }
            ?: error("Anonymous authentication completed without a user.")
    }
}
