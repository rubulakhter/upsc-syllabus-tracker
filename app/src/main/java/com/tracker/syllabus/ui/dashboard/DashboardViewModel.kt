package com.tracker.syllabus.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tracker.syllabus.data.model.Subject
import com.tracker.syllabus.data.model.Topic
import com.tracker.syllabus.data.model.UserProfile
import com.tracker.syllabus.data.repository.AuthRepository
import com.tracker.syllabus.data.repository.SyllabusRepository
import com.tracker.syllabus.di.DependencyProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val authRepository: AuthRepository = DependencyProvider.authRepository,
    private val syllabusRepository: SyllabusRepository = DependencyProvider.syllabusRepository
) : ViewModel() {

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Listen to all user subjects dynamically
    private val _subjects = MutableStateFlow<List<Subject>>(emptyList())
    val subjects: StateFlow<List<Subject>> = _subjects.asStateFlow()

    // Listen to all user topics dynamically
    private val _topics = MutableStateFlow<List<Topic>>(emptyList())
    val topics: StateFlow<List<Topic>> = _topics.asStateFlow()

    init {
        loadUserProfileAndTopics()
    }

    private fun loadUserProfileAndTopics() {
        val currentUser = authRepository.currentUser.value
        if (currentUser != null) {
            viewModelScope.launch {
                // Fetch profile
                val profile = authRepository.getUserProfile(currentUser.uid)
                _userProfile.value = profile

                // Listen to subjects
                launch {
                    syllabusRepository.getSubjectsFlow(currentUser.uid).collect { subjectsList ->
                        _subjects.value = subjectsList.sortedBy { it.name }
                    }
                }

                // Listen to topics
                launch {
                    syllabusRepository.getTopicsFlow(currentUser.uid).collect { topicsList ->
                        _topics.value = topicsList
                        _isLoading.value = false
                    }
                }
            }
        }
    }

    // Expose subject progress percentages (Subject ID -> Progress Float [0..100])
    val subjectProgress: StateFlow<Map<String, Float>> = _topics.combine(_subjects) { topicList, subjectList ->
        val progressMap = mutableMapOf<String, Float>()
        subjectList.forEach { subject ->
            val subjectTopics = topicList.filter { it.subjectId == subject.id }
            if (subjectTopics.isEmpty()) {
                progressMap[subject.id] = 0f
            } else {
                val completedCount = subjectTopics.count { it.completed }
                progressMap[subject.id] = (completedCount.toFloat() / subjectTopics.size) * 100f
            }
        }
        progressMap
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyMap())

    // Overall Progress: Average of all subject percentages
    val overallProgress: StateFlow<Float> = subjectProgress.combine(_subjects) { progressMap, subjectList ->
        if (subjectList.isEmpty() || progressMap.isEmpty()) {
            0f
        } else {
            val totalPercentage = subjectList.sumOf { progressMap[it.id]?.toDouble() ?: 0.0 }
            (totalPercentage / subjectList.size).toFloat()
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0f)

    // Calculate due topics dynamically for notifications
    val dueTopics: StateFlow<List<Topic>> = combine(_topics, _userProfile) { topicList, profile ->
        val customInterval = profile?.customRevisionInterval ?: 30
        topicList.filter { it.isDueForRevision(customInterval) }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Subject CRUD Actions
    fun addSubject(name: String, description: String) {
        val currentUser = authRepository.currentUser.value ?: return
        viewModelScope.launch {
            syllabusRepository.addSubject(currentUser.uid, name, description)
        }
    }

    fun editSubject(subjectId: String, name: String, description: String) {
        val currentUser = authRepository.currentUser.value ?: return
        viewModelScope.launch {
            syllabusRepository.updateSubject(currentUser.uid, subjectId, name, description)
        }
    }

    fun deleteSubject(subjectId: String) {
        val currentUser = authRepository.currentUser.value ?: return
        viewModelScope.launch {
            syllabusRepository.deleteSubject(currentUser.uid, subjectId)
        }
    }

    // Direct mark read action from notification panel
    fun markTopicReadToday(topicId: String) {
        val currentUser = authRepository.currentUser.value ?: return
        viewModelScope.launch {
            syllabusRepository.markTopicReadToday(currentUser.uid, topicId)
        }
    }
}
