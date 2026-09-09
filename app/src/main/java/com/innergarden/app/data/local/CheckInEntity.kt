package com.innergarden.app.data.local

data class CheckInEntity(
    val id: String,
    val timestampEpochMillis: Long,
    val mood: Int,
    val stress: Int,
    val energy: Int,
    val sleep: Int,
    val reflection: String?
)
