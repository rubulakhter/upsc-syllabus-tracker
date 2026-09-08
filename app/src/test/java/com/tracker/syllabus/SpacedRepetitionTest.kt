package com.tracker.syllabus

import com.tracker.syllabus.data.model.Topic
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class SpacedRepetitionTest {

    @Test
    fun testNextDueDate_notStarted() {
        val topic = Topic(
            id = "test_topic",
            subjectId = "polity",
            title = "Test Topic",
            completed = false,
            revisionCount = 0,
            firstReadDate = null
        )
        assertNull(topic.getNextDueDate(30))
    }

    @Test
    fun testNextDueDate_day4() {
        val todayStart = LocalDate.now()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val topic = Topic(
            id = "test_topic",
            subjectId = "polity",
            title = "Test Topic",
            completed = false,
            revisionCount = 1,
            firstReadDate = todayStart
        )

        val nextDue = topic.getNextDueDate(30)
        assertNotNull(nextDue)

        val expectedNextDue = LocalDate.now().plusDays(3)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        assertEquals(expectedNextDue, nextDue)
    }

    @Test
    fun testNextDueDate_day7() {
        val todayStart = LocalDate.now()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val topic = Topic(
            id = "test_topic",
            subjectId = "polity",
            title = "Test Topic",
            completed = false,
            revisionCount = 2,
            firstReadDate = todayStart
        )

        val nextDue = topic.getNextDueDate(30)
        assertNotNull(nextDue)

        val expectedNextDue = LocalDate.now().plusDays(6)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        assertEquals(expectedNextDue, nextDue)
    }

    @Test
    fun testNextDueDate_day30() {
        val todayStart = LocalDate.now()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val topic = Topic(
            id = "test_topic",
            subjectId = "polity",
            title = "Test Topic",
            completed = false,
            revisionCount = 3,
            firstReadDate = todayStart
        )

        val nextDue = topic.getNextDueDate(30)
        assertNotNull(nextDue)

        val expectedNextDue = LocalDate.now().plusDays(29)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        assertEquals(expectedNextDue, nextDue)
    }

    @Test
    fun testNextDueDate_subsequentIntervals() {
        val todayStart = LocalDate.now()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val topic = Topic(
            id = "test_topic",
            subjectId = "polity",
            title = "Test Topic",
            completed = false,
            revisionCount = 4, // revisionCount is 4, next is Day 60 (29 + (4-3)*30 = 59 days offset)
            firstReadDate = todayStart
        )

        val nextDue = topic.getNextDueDate(30)
        assertNotNull(nextDue)

        val expectedNextDue = LocalDate.now().plusDays(59)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        assertEquals(expectedNextDue, nextDue)
    }

    @Test
    fun testIsDueForRevision() {
        val fourDaysAgoStart = LocalDate.now().minusDays(4)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val topicDue = Topic(
            id = "test_topic",
            subjectId = "polity",
            title = "Test Topic",
            completed = false,
            revisionCount = 1, // Next due is Day 4 (firstReadDate + 3 days)
            firstReadDate = fourDaysAgoStart // 4 days ago + 3 days = 1 day ago (in the past, so should be due!)
        )

        assertTrue(topicDue.isDueForRevision(30))

        val topicNotDue = Topic(
            id = "test_topic",
            subjectId = "polity",
            title = "Test Topic",
            completed = false,
            revisionCount = 1, // Next due is Day 4 (firstReadDate + 3 days)
            firstReadDate = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() // Today, so due in 3 days. Not due yet!
        )

        assertFalse(topicNotDue.isDueForRevision(30))
    }
}
