package com.tracker.syllabus.data.model

import androidx.annotation.Keep
import java.time.Instant
import java.time.ZoneId
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Keep
data class UserProfile(
    val uid: String = "",
    val mobile: String = "",
    val name: String = "",
    val email: String = "",
    val customRevisionInterval: Int = 30,
    val themeMode: String = "system",
    val appTheme: String = "gold"
)

@Keep
data class Subject(
    val id: String = "",
    val name: String = "",
    val description: String = ""
)

@Keep
data class Topic(
    val id: String = "",
    val subjectId: String = "",
    val title: String = "",
    val completed: Boolean = false,
    val revisionCount: Int = 0,
    val firstReadDate: Long? = null,
    val lastRevisionDate: Long? = null,
    val revisionDates: List<Long>? = emptyList()
) {
    /**
     * Calculates the next due date for revision.
     * Returns null if the topic hasn't been read yet (revisionCount == 0).
     */
    fun getNextDueDate(customIntervalDays: Int): Long? {
        val firstRead = firstReadDate ?: return null
        val firstReadLocalDate = Instant.ofEpochMilli(firstRead)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        
        val uniqueDays = if (revisionDates.isNullOrEmpty() && revisionCount > 0) {
            revisionCount
        } else {
            revisionDates?.map { timestamp ->
                Instant.ofEpochMilli(timestamp)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
            }?.distinct()?.size ?: 0
        }

        val daysToAdd = when (uniqueDays) {
            0 -> 0 // Not started
            1 -> 3 // Day 4 (Day 1 + 3 days)
            2 -> 6 // Day 7 (Day 1 + 6 days)
            3 -> 29 // Day 30 (Day 1 + 29 days)
            else -> 29 + (uniqueDays - 3) * customIntervalDays
        }
        
        return firstReadLocalDate.plusDays(daysToAdd.toLong())
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }

    /**
     * Determines if the topic is currently due for revision.
     */
    fun isDueForRevision(customIntervalDays: Int): Boolean {
        val nextDue = getNextDueDate(customIntervalDays) ?: return false
        val todayStart = LocalDate.now()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        return todayStart >= nextDue
    }
}
