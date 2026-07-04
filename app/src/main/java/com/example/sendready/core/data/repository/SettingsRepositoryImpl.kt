package com.example.sendready.core.data.repository

import com.example.sendready.core.data.local.datastore.SettingsDataStore
import com.example.sendready.core.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : SettingsRepository {
    override val isDarkMode: Flow<Boolean> = settingsDataStore.isDarkMode

    override suspend fun setDarkMode(enabled: Boolean) {
        settingsDataStore.setDarkMode(enabled)
    }
}
