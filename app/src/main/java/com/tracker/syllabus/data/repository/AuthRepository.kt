package com.tracker.syllabus.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.tracker.syllabus.data.model.UserProfile
import com.tracker.syllabus.data.model.SyllabusSeedData
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser

    init {
        auth.addAuthStateListener { firebaseAuth ->
            _currentUser.value = firebaseAuth.currentUser
        }
    }

    // 1. Sign in with Email and Password
    suspend fun signIn(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception("User was null after sign in")
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 2. Sign up with Email, Password, Name, Mobile, and conditional default syllabus seeding
    suspend fun signUp(
        email: String,
        password: String,
        name: String,
        mobile: String,
        loadDefaultSyllabus: Boolean
    ): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception("User was null after registration")
            
            // Create user profile in Firestore
            val userProfile = UserProfile(
                uid = user.uid,
                mobile = mobile,
                name = name,
                email = email,
                customRevisionInterval = 30
            )
            firestore.collection("users").document(user.uid).set(userProfile).await()

            if (loadDefaultSyllabus) {
                val batch = firestore.batch()
                val subjects = SyllabusSeedData.getSeedSubjects()
                subjects.forEach { subject ->
                    val docRef = firestore.collection("users")
                        .document(user.uid)
                        .collection("subjects")
                        .document(subject.id)
                    batch.set(docRef, subject)
                }

                val topics = SyllabusSeedData.getSeedTopics()
                topics.forEach { topic ->
                    val docRef = firestore.collection("users")
                        .document(user.uid)
                        .collection("topics")
                        .document(topic.id)
                    batch.set(docRef, topic)
                }
                batch.commit().await()
            }
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 3. Check if Firestore user document exists
    suspend fun checkIfProfileExists(uid: String): Boolean {
        return try {
            val doc = firestore.collection("users").document(uid).get().await()
            doc.exists()
        } catch (e: Exception) {
            false
        }
    }

    // 4. Create user profile in Firestore and seed default syllabus conditionally (used for onboarding recovery)
    suspend fun createUserProfileAndSeed(
        uid: String,
        name: String,
        email: String,
        mobile: String,
        loadDefaultSyllabus: Boolean
    ): Result<Unit> {
        return try {
            val userProfile = UserProfile(
                uid = uid,
                mobile = mobile,
                name = name,
                email = email,
                customRevisionInterval = 30
            )
            firestore.collection("users").document(uid).set(userProfile).await()
            
            if (loadDefaultSyllabus) {
                val batch = firestore.batch()
                val subjects = SyllabusSeedData.getSeedSubjects()
                subjects.forEach { subject ->
                    val docRef = firestore.collection("users")
                        .document(uid)
                        .collection("subjects")
                        .document(subject.id)
                    batch.set(docRef, subject)
                }

                val topics = SyllabusSeedData.getSeedTopics()
                topics.forEach { topic ->
                    val docRef = firestore.collection("users")
                        .document(uid)
                        .collection("topics")
                        .document(topic.id)
                    batch.set(docRef, topic)
                }
                batch.commit().await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
    }

    suspend fun getUserProfile(uid: String): UserProfile? {
        return try {
            val doc = firestore.collection("users").document(uid).get().await()
            doc.toObject(UserProfile::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateCustomInterval(uid: String, days: Int): Result<Unit> {
        return try {
            firestore.collection("users").document(uid)
                .update("customRevisionInterval", days)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProfile(
        uid: String,
        name: String,
        email: String,
        mobile: String,
        customInterval: Int,
        themeMode: String,
        appTheme: String
    ): Result<Unit> {
        return try {
            // Update email in Firebase Auth credentials if modified
            val currentUser = auth.currentUser
            if (currentUser != null && currentUser.email != email.trim()) {
                currentUser.updateEmail(email.trim()).await()
            }

            val updates = mapOf(
                "name" to name,
                "email" to email,
                "mobile" to mobile,
                "customRevisionInterval" to customInterval,
                "themeMode" to themeMode,
                "appTheme" to appTheme
            )
            firestore.collection("users").document(uid)
                .update(updates)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
