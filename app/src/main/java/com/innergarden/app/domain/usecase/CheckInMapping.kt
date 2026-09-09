package com.innergarden.app.domain.usecase

import com.innergarden.app.data.local.CheckInEntity
import com.innergarden.app.domain.model.DailyCheckIn
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

internal fun CheckInEntity.toDomain(zoneId: ZoneId): DailyCheckIn = DailyCheckIn(
    id = id,
    timestamp = Instant.ofEpochMilli(timestampEpochMillis).atZone(zoneId).toLocalDateTime(),
    mood = mood,
    stress = stress,
    energy = energy,
    sleep = sleep,
    reflection = reflection,
    localDate = runCatching { LocalDate.parse(localDate) }.getOrElse {
        Instant.ofEpochMilli(timestampEpochMillis).atZone(zoneId).toLocalDate()
    }
)

internal fun DailyCheckIn.toEntity(zoneId: ZoneId): CheckInEntity = CheckInEntity(
    id = id,
    timestampEpochMillis = timestamp.atZone(zoneId).toInstant().toEpochMilli(),
    mood = mood,
    stress = stress,
    energy = energy,
    sleep = sleep,
    reflection = reflection,
    localDate = date.toString()
)
