package com.tracker.syllabus.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tracker.syllabus.data.model.UserProfile
import com.tracker.syllabus.data.repository.AuthRepository
import com.tracker.syllabus.di.DependencyProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val authRepository: AuthRepository = DependencyProvider.authRepository
) : ViewModel() {

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _saveSuccess = MutableStateFlow<Boolean?>(null)
    val saveSuccess: StateFlow<Boolean?> = _saveSuccess.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val currentUser = authRepository.currentUser.value
        if (currentUser != null) {
            viewModelScope.launch {
                val profile = authRepository.getUserProfile(currentUser.uid)
                _userProfile.value = profile
                _isLoading.value = false
            }
        }
    }

    fun updateRevisionInterval(interval: Int) {
        val currentUser = authRepository.currentUser.value ?: return
        if (interval <= 0) {
            _error.value = "Interval must be at least 1 day"
            return
        }
        _isSaving.value = true
        _error.value = null
        viewModelScope.launch {
            val result = authRepository.updateCustomInterval(currentUser.uid, interval)
            _isSaving.value = false
            if (result.isSuccess) {
                _userProfile.value = _userProfile.value?.copy(customRevisionInterval = interval)
            } else {
                _error.value = result.exceptionOrNull()?.message ?: "Failed to update interval"
            }
        }
    }

    fun saveProfile(name: String, email: String, mobile: String, interval: Int, themeMode: String, appTheme: String) {
        val currentUser = authRepository.currentUser.value ?: return
        if (name.isBlank()) {
            _error.value = "Name cannot be empty"
            _saveSuccess.value = false
            return
        }
        if (email.isBlank()) {
            _error.value = "Email cannot be empty"
            _saveSuccess.value = false
            return
        }
        if (mobile.isBlank()) {
            _error.value = "Mobile number cannot be empty"
            _saveSuccess.value = false
            return
        }
        if (interval <= 0) {
            _error.value = "Interval must be at least 1 day"
            _saveSuccess.value = false
            return
        }
        _isSaving.value = true
        _error.value = null
        _saveSuccess.value = null
        viewModelScope.launch {
            val result = authRepository.updateProfile(
                uid = currentUser.uid,
                name = name.trim(),
                email = email.trim(),
                mobile = mobile.trim(),
                customInterval = interval,
                themeMode = themeMode,
                appTheme = appTheme
            )
            _isSaving.value = false
            if (result.isSuccess) {
                _userProfile.value = _userProfile.value?.copy(
                    name = name.trim(),
                    email = email.trim(),
                    mobile = mobile.trim(),
                    customRevisionInterval = interval,
                    themeMode = themeMode,
                    appTheme = appTheme
                )
                _saveSuccess.value = true
            } else {
                _error.value = result.exceptionOrNull()?.message ?: "Failed to update profile"
                _saveSuccess.value = false
            }
        }
    }

    fun setError(msg: String) {
        _error.value = msg
    }

    fun clearSaveSuccess() {
        _saveSuccess.value = null
    }

    fun clearError() {
        _error.value = null
    }

    fun logout() {
        authRepository.logout()
    }
}
