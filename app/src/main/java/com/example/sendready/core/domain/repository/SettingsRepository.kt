package com.example.sendready.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val isDarkMode: Flow<Boolean>
    suspend fun setDarkMode(enabled: Boolean)
}
