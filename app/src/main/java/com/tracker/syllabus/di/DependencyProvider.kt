package com.tracker.syllabus.di

import com.tracker.syllabus.data.repository.AuthRepository
import com.tracker.syllabus.data.repository.SyllabusRepository

object DependencyProvider {
    val authRepository: AuthRepository by lazy { AuthRepository() }
    val syllabusRepository: SyllabusRepository by lazy { SyllabusRepository() }
}
