package com.innergarden.app.domain.usecase

import java.time.LocalDate

class GetRecentReflectionsUseCase(
    private val getRecentCheckIns: GetRecentCheckInsUseCase,
    private val today: () -> LocalDate = LocalDate::now
) {
    suspend operator fun invoke(): List<String> {
        val endDate = today()
        val startDate = endDate.minusDays(6)
        return getRecentCheckIns.load()
            .filter { !it.date.isBefore(startDate) && !it.date.isAfter(endDate) }
            .sortedBy { it.timestamp }
            .mapNotNull { it.reflection?.trim()?.takeIf(String::isNotBlank) }
    }
}
