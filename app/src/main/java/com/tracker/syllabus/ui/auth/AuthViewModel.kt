package com.tracker.syllabus.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.tracker.syllabus.data.repository.AuthRepository
import com.tracker.syllabus.di.DependencyProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository = DependencyProvider.authRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    // Email/Password flows complete profile setup atomically at sign up, so this is unused but kept for routing signature compatibility
    private val _isProfileSetupRequired = MutableStateFlow<Boolean?>(null)
    val isProfileSetupRequired: StateFlow<Boolean?> = _isProfileSetupRequired.asStateFlow()

    init {
        checkCurrentAuthSession()
    }

    private fun checkCurrentAuthSession() {
        val user = authRepository.currentUser.value
        if (user != null) {
            _isLoading.value = true
            viewModelScope.launch {
                val hasProfile = authRepository.checkIfProfileExists(user.uid)
                _isLoading.value = false
                if (hasProfile) {
                    _isAuthenticated.value = true
                    _isProfileSetupRequired.value = false
                } else {
                    _isAuthenticated.value = false
                    _isProfileSetupRequired.value = true
                }
            }
        } else {
            _isAuthenticated.value = false
            _isProfileSetupRequired.value = null
        }

        // Listen for future auth state changes
        viewModelScope.launch {
            authRepository.currentUser.collect { firebaseUser ->
                if (firebaseUser == null) {
                    _isAuthenticated.value = false
                    _isProfileSetupRequired.value = null
                } else {
                    val hasProfile = authRepository.checkIfProfileExists(firebaseUser.uid)
                    if (hasProfile) {
                        _isAuthenticated.value = true
                        _isProfileSetupRequired.value = false
                    }
                }
            }
        }
    }

    fun login(email: String, password: String) {
        val emailTrimmed = email.trim()
        val pwdTrimmed = password.trim()
        
        if (emailTrimmed.isBlank() || pwdTrimmed.isBlank()) {
            _error.value = "All fields are required"
            return
        }

        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            val result = authRepository.signIn(emailTrimmed, pwdTrimmed)
            _isLoading.value = false
            if (result.isSuccess) {
                val user = result.getOrNull()
                if (user != null) {
                    val hasProfile = authRepository.checkIfProfileExists(user.uid)
                    if (hasProfile) {
                        _isAuthenticated.value = true
                        _isProfileSetupRequired.value = false
                    } else {
                        _isAuthenticated.value = false
                        _isProfileSetupRequired.value = true
                    }
                }
            } else {
                _error.value = result.exceptionOrNull()?.message ?: "Sign in failed"
            }
        }
    }

    fun signup(email: String, password: String, name: String, mobile: String, loadDefaultSyllabus: Boolean) {
        val emailTrimmed = email.trim()
        val pwdTrimmed = password.trim()
        val nameTrimmed = name.trim()
        val mobileTrimmed = mobile.trim()

        if (emailTrimmed.isBlank() || pwdTrimmed.isBlank() || nameTrimmed.isBlank() || mobileTrimmed.isBlank()) {
            _error.value = "All fields are required"
            return
        }

        _isLoading.value = true
        _error.value = null

        val currentUser = authRepository.currentUser.value
        if (currentUser != null && currentUser.email == emailTrimmed) {
            viewModelScope.launch {
                val result = authRepository.createUserProfileAndSeed(
                    uid = currentUser.uid,
                    name = nameTrimmed,
                    email = emailTrimmed,
                    mobile = mobileTrimmed,
                    loadDefaultSyllabus = loadDefaultSyllabus
                )
                _isLoading.value = false
                if (result.isSuccess) {
                    _isProfileSetupRequired.value = false
                    _isAuthenticated.value = true
                } else {
                    _error.value = result.exceptionOrNull()?.message ?: "Failed to save profile. Try again."
                }
            }
            return
        }

        viewModelScope.launch {
            val result = authRepository.signUp(
                email = emailTrimmed,
                password = pwdTrimmed,
                name = nameTrimmed,
                mobile = mobileTrimmed,
                loadDefaultSyllabus = loadDefaultSyllabus
            )
            _isLoading.value = false
            if (result.isSuccess) {
                _isProfileSetupRequired.value = false
                _isAuthenticated.value = true
            } else {
                _error.value = result.exceptionOrNull()?.message ?: "Sign up failed"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun logout() {
        authRepository.logout()
        _isProfileSetupRequired.value = null
        _isAuthenticated.value = false
    }
}
