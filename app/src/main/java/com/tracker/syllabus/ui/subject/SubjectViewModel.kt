package com.tracker.syllabus.ui.subject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tracker.syllabus.data.model.Subject
import com.tracker.syllabus.data.model.Topic
import com.tracker.syllabus.data.repository.AuthRepository
import com.tracker.syllabus.data.repository.SyllabusRepository
import com.tracker.syllabus.di.DependencyProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SubjectViewModel(
    private val subjectId: String,
    private val authRepository: AuthRepository = DependencyProvider.authRepository,
    private val syllabusRepository: SyllabusRepository = DependencyProvider.syllabusRepository
) : ViewModel() {

    private val _subject = MutableStateFlow<Subject?>(null)
    val subject: StateFlow<Subject?> = _subject.asStateFlow()

    private val _topics = MutableStateFlow<List<Topic>>(emptyList())
    val topics: StateFlow<List<Topic>> = _topics.asStateFlow()

    private val _customInterval = MutableStateFlow(30)
    val customInterval: StateFlow<Int> = _customInterval.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadSubjectAndTopics()
    }

    private fun loadSubjectAndTopics() {
        val currentUser = authRepository.currentUser.value
        if (currentUser != null) {
            viewModelScope.launch {
                // Listen to subjects to find current subject dynamically
                launch {
                    syllabusRepository.getSubjectsFlow(currentUser.uid).collect { allSubjects ->
                        _subject.value = allSubjects.firstOrNull { it.id == subjectId }
                    }
                }

                // Load profile to get customRevisionInterval
                val profile = authRepository.getUserProfile(currentUser.uid)
                if (profile != null) {
                    _customInterval.value = profile.customRevisionInterval
                }

                // Listen to topics
                launch {
                    syllabusRepository.getTopicsFlow(currentUser.uid).collect { allTopics ->
                        _topics.value = allTopics.filter { it.subjectId == subjectId }.sortedBy { it.title }
                        _isLoading.value = false
                    }
                }
            }
        }
    }

    fun toggleTopicCompletion(topicId: String, completed: Boolean) {
        val currentUser = authRepository.currentUser.value ?: return
        viewModelScope.launch {
            syllabusRepository.setTopicCompletion(currentUser.uid, topicId, completed)
        }
    }

    fun markTopicReadToday(topicId: String) {
        val currentUser = authRepository.currentUser.value ?: return
        viewModelScope.launch {
            syllabusRepository.markTopicReadToday(currentUser.uid, topicId)
        }
    }

    fun undoLastRead(topicId: String) {
        val currentUser = authRepository.currentUser.value ?: return
        viewModelScope.launch {
            syllabusRepository.undoLastRead(currentUser.uid, topicId)
        }
    }

    // Topic CRUD Actions
    fun addTopic(title: String) {
        val currentUser = authRepository.currentUser.value ?: return
        viewModelScope.launch {
            syllabusRepository.addTopic(currentUser.uid, subjectId, title)
        }
    }

    fun editTopic(topicId: String, title: String) {
        val currentUser = authRepository.currentUser.value ?: return
        viewModelScope.launch {
            syllabusRepository.updateTopic(currentUser.uid, topicId, title)
        }
    }

    fun deleteTopic(topicId: String) {
        val currentUser = authRepository.currentUser.value ?: return
        viewModelScope.launch {
            syllabusRepository.deleteTopic(currentUser.uid, topicId)
        }
    }

    fun simulateDay5(topicId: String) {
        val currentUser = authRepository.currentUser.value ?: return
        viewModelScope.launch {
            val fourDaysAgo = System.currentTimeMillis() - (4 * 24 * 60 * 60 * 1000L)
            syllabusRepository.simulateTopicProgress(currentUser.uid, topicId, fourDaysAgo, 1)
        }
    }
}
