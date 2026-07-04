package com.example.sendready.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sendready.core.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = settingsRepository.isDarkMode
        .map { isDarkMode ->
            SettingsUiState(isDarkMode = isDarkMode)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsUiState(isLoading = true)
        )

    fun onAction(action: SettingsUiAction) {
        when (action) {
            is SettingsUiAction.ToggleDarkMode -> {
                viewModelScope.launch {
                    settingsRepository.setDarkMode(action.enabled)
                }
            }
        }
    }
}
