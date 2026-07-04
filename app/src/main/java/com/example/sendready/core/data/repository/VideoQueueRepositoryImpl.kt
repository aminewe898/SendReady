package com.example.sendready.core.data.repository

import com.example.sendready.core.domain.repository.VideoQueueRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoQueueRepositoryImpl @Inject constructor() : VideoQueueRepository {
    private val _queue = MutableStateFlow<List<String>>(emptyList())
    override val queue: StateFlow<List<String>> = _queue.asStateFlow()

    override fun addToQueue(uris: List<String>) {
        _queue.update { current ->
            // Prevent duplicates but maintain order
            (current + uris).distinct()
        }
    }

    override fun removeFromQueue(uri: String) {
        _queue.update { current ->
            current.filter { it != uri }
        }
    }

    override fun clearQueue() {
        _queue.value = emptyList()
    }

    override fun reorderQueue(fromIndex: Int, toIndex: Int) {
        _queue.update { current ->
            if (fromIndex !in current.indices || toIndex !in current.indices) {
                return@update current
            }
            val list = current.toMutableList()
            val item = list.removeAt(fromIndex)
            list.add(toIndex, item)
            list
        }
    }
}
