package com.tracker.syllabus.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.tracker.syllabus.data.model.Topic
import com.tracker.syllabus.data.model.Subject
import com.tracker.syllabus.data.model.SyllabusSeedData
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.time.ZoneId
import java.time.LocalDate
import java.util.UUID

class SyllabusRepository {
    private val firestore = FirebaseFirestore.getInstance()

    /**
     * Listens in real-time to the subjects of a specific user.
     */
    fun getSubjectsFlow(userId: String): Flow<List<Subject>> = callbackFlow {
        val collectionRef = firestore.collection("users")
            .document(userId)
            .collection("subjects")

        val subscription: ListenerRegistration = collectionRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val subjects = snapshot.toObjects(Subject::class.java)
                trySend(subjects)
            }
        }

        awaitClose { subscription.remove() }
    }

    /**
     * Adds a new subject to Firestore.
     */
    suspend fun addSubject(userId: String, name: String, description: String): Result<Unit> {
        return try {
            val id = UUID.randomUUID().toString()
            val subject = Subject(id = id, name = name, description = description)
            firestore.collection("users")
                .document(userId)
                .collection("subjects")
                .document(id)
                .set(subject)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Updates an existing subject in Firestore.
     */
    suspend fun updateSubject(userId: String, subjectId: String, name: String, description: String): Result<Unit> {
        return try {
            val updates = mapOf(
                "name" to name,
                "description" to description
            )
            firestore.collection("users")
                .document(userId)
                .collection("subjects")
                .document(subjectId)
                .update(updates)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Deletes a subject and cleans up all associated topics.
     */
    suspend fun deleteSubject(userId: String, subjectId: String): Result<Unit> {
        return try {
            val batch = firestore.batch()

            // Reference to the subject document
            val subjectRef = firestore.collection("users")
                .document(userId)
                .collection("subjects")
                .document(subjectId)
            batch.delete(subjectRef)

            // Query all topics of this user under the subject
            val topicsQuery = firestore.collection("users")
                .document(userId)
                .collection("topics")
                .whereEqualTo("subjectId", subjectId)
                .get()
                .await()

            // Delete each topic
            topicsQuery.documents.forEach { doc ->
                batch.delete(doc.reference)
            }

            batch.commit().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Listens in real-time to the topics of a specific user.
     */
    fun getTopicsFlow(userId: String): Flow<List<Topic>> = callbackFlow {
        val collectionRef = firestore.collection("users")
            .document(userId)
            .collection("topics")

        val subscription: ListenerRegistration = collectionRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val topics = snapshot.toObjects(Topic::class.java)
                trySend(topics)
            }
        }

        awaitClose { subscription.remove() }
    }

    /**
     * Adds a new topic under a subject.
     */
    suspend fun addTopic(userId: String, subjectId: String, title: String): Result<Unit> {
        return try {
            val id = UUID.randomUUID().toString()
            val topic = Topic(id = id, subjectId = subjectId, title = title)
            firestore.collection("users")
                .document(userId)
                .collection("topics")
                .document(id)
                .set(topic)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Renames an existing topic in Firestore.
     */
    suspend fun updateTopic(userId: String, topicId: String, title: String): Result<Unit> {
        return try {
            firestore.collection("users")
                .document(userId)
                .collection("topics")
                .document(topicId)
                .update("title", title)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Deletes a topic from Firestore.
     */
    suspend fun deleteTopic(userId: String, topicId: String): Result<Unit> {
        return try {
            firestore.collection("users")
                .document(userId)
                .collection("topics")
                .document(topicId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Marks a topic as completed (100%) or incomplete (0%).
     */
    suspend fun setTopicCompletion(userId: String, topicId: String, completed: Boolean): Result<Unit> {
        return try {
            firestore.collection("users")
                .document(userId)
                .collection("topics")
                .document(topicId)
                .update("completed", completed)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Marks a topic as read today (Day 1, Day 4, etc.).
     * Increments the revision count, sets firstReadDate if not set, and adds today's date to revision history.
     */
    suspend fun markTopicReadToday(userId: String, topicId: String): Result<Unit> {
        return try {
            val docRef = firestore.collection("users")
                .document(userId)
                .collection("topics")
                .document(topicId)

            val doc = docRef.get().await()
            val topic = doc.toObject(Topic::class.java) ?: throw Exception("Topic not found")

            val nowMillis = Instant.now().toEpochMilli()
            val updatedRevisionDates = (topic.revisionDates ?: emptyList()) + nowMillis
            val newRevisionCount = topic.revisionCount + 1
            val newFirstReadDate = topic.firstReadDate ?: nowMillis

            val updates = mapOf(
                "firstReadDate" to newFirstReadDate,
                "lastRevisionDate" to nowMillis,
                "revisionCount" to newRevisionCount,
                "revisionDates" to updatedRevisionDates,
                "completed" to true
            )

            docRef.update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun simulateTopicProgress(userId: String, topicId: String, firstReadDate: Long, revisionCount: Int): Result<Unit> {
        return try {
            val docRef = firestore.collection("users")
                .document(userId)
                .collection("topics")
                .document(topicId)
            val updates = mapOf(
                "firstReadDate" to firstReadDate,
                "lastRevisionDate" to firstReadDate,
                "revisionCount" to revisionCount,
                "revisionDates" to listOf(firstReadDate),
                "completed" to true
            )
            docRef.update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Undoes the last logged read/revision action.
     * Removes the latest timestamp, decrements revision count, and resets firstReadDate/completed if count becomes 0.
     */
    suspend fun undoLastRead(userId: String, topicId: String): Result<Unit> {
        return try {
            val docRef = firestore.collection("users")
                .document(userId)
                .collection("topics")
                .document(topicId)

            val doc = docRef.get().await()
            val topic = doc.toObject(Topic::class.java) ?: throw Exception("Topic not found")

            val currentDates = topic.revisionDates ?: emptyList()
            if (currentDates.isEmpty()) {
                return Result.success(Unit) // Nothing to undo
            }

            // Remove the last timestamp
            val updatedRevisionDates = currentDates.dropLast(1)
            val newRevisionCount = (topic.revisionCount - 1).coerceAtLeast(0)
            
            val newLastRevisionDate = if (updatedRevisionDates.isNotEmpty()) {
                updatedRevisionDates.last()
            } else {
                null
            }

            val newFirstReadDate = if (newRevisionCount > 0) {
                topic.firstReadDate
            } else {
                null
            }

            val newCompleted = newRevisionCount > 0

            val updates = mutableMapOf<String, Any?>(
                "firstReadDate" to newFirstReadDate,
                "lastRevisionDate" to newLastRevisionDate,
                "revisionCount" to newRevisionCount,
                "revisionDates" to updatedRevisionDates,
                "completed" to newCompleted
            )

            docRef.update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
