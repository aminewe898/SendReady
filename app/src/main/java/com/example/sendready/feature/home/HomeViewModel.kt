package com.example.sendready.feature.home

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sendready.core.domain.repository.VideoQueueRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val videoQueueRepository: VideoQueueRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun handleUris(uris: List<Uri>) {
        if (uris.isEmpty()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val contentResolver: ContentResolver = context.contentResolver
            val uriStrings = mutableListOf<String>()

            for (uri in uris) {
                try {
                    contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    uriStrings.add(uri.toString())
                } catch (e: SecurityException) {
                    // Log or handle failure
                    e.printStackTrace()
                }
            }

            videoQueueRepository.clearQueue()
            videoQueueRepository.addToQueue(uriStrings)

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                selectedVideoUri = uriStrings.firstOrNull() // Keep for backward compatibility if needed
            )
        }
    }
}
