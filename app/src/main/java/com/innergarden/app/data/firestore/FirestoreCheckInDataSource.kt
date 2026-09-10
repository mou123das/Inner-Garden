package com.innergarden.app.data.firestore

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.innergarden.app.data.auth.AuthDataSource
import com.innergarden.app.data.local.CheckInDataSource
import com.innergarden.app.data.local.CheckInEntity
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.Date
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreCheckInDataSource(
    private val firestore: FirebaseFirestore,
    private val authDataSource: AuthDataSource,
    private val zoneId: ZoneId = ZoneId.systemDefault()
) : CheckInDataSource {
    override fun observeAll(): Flow<List<CheckInEntity>> = callbackFlow {
        val userId = requireUserId()
        val registration = checkIns(userId).addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val entities = snapshot?.documents.orEmpty()
                .mapNotNull(::toEntity)
                .sortedByDescending { it.localDate }
            trySend(entities)
        }
        awaitClose { registration.remove() }
    }

    override suspend fun loadAll(): List<CheckInEntity> {
        val userId = requireUserId()
        return checkIns(userId).get().await().documents
            .mapNotNull(::toEntity)
            .sortedByDescending { it.localDate }
    }

    override suspend fun save(checkIn: CheckInEntity) {
        val userId = requireUserId()
        val localDate = checkIn.localDate.takeIf { runCatching { LocalDate.parse(it) }.isSuccess }
            ?: error("A valid local date is required.")
        val document = hashMapOf<String, Any?>(
            "localDate" to localDate,
            "mood" to checkIn.mood,
            "stress" to checkIn.stress,
            "energy" to checkIn.energy,
            "sleepQuality" to checkIn.sleep,
            "reflection" to checkIn.reflection,
            "timestamp" to Timestamp(Date(checkIn.timestampEpochMillis)),
            "createdAt" to FieldValue.serverTimestamp()
        )
        checkIns(userId).document(localDate).set(document).await()
    }

    private fun checkIns(userId: String) =
        firestore.collection("users").document(userId).collection("checkIns")

    private fun requireUserId(): String =
        authDataSource.currentUserId()?.takeIf { it.isNotBlank() }
            ?: error("An authenticated user is required.")

    private fun toEntity(document: DocumentSnapshot): CheckInEntity? {
        val localDate = document.getString("localDate") ?: document.id
        val parsedDate = runCatching { LocalDate.parse(localDate) }.getOrNull() ?: return null
        val mood = document.getLong("mood")?.toInt() ?: return null
        val stress = document.getLong("stress")?.toInt() ?: return null
        val energy = document.getLong("energy")?.toInt() ?: return null
        val sleep = document.getLong("sleepQuality")?.toInt() ?: return null
        if (listOf(mood, stress, energy, sleep).any { it !in 1..5 }) return null
        val fallbackMillis = parsedDate.atTime(LocalTime.NOON).atZone(zoneId).toInstant().toEpochMilli()
        return CheckInEntity(
            id = document.id,
            timestampEpochMillis = document.getTimestamp("timestamp")?.toDate()?.time ?: fallbackMillis,
            mood = mood,
            stress = stress,
            energy = energy,
            sleep = sleep,
            reflection = document.getString("reflection"),
            localDate = localDate
        )
    }
}
