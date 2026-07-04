package com.example.sendready.core.domain.repository

import kotlinx.coroutines.flow.StateFlow

interface VideoQueueRepository {
    val queue: StateFlow<List<String>> // List of URI strings
    
    fun addToQueue(uris: List<String>)
    fun removeFromQueue(uri: String)
    fun clearQueue()
    fun reorderQueue(fromIndex: Int, toIndex: Int)
}
