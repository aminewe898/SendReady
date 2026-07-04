package com.example.sendready.core.domain.repository

import com.example.sendready.core.domain.model.VideoMetadata

interface MediaRepository {
    suspend fun getVideoMetadata(uriString: String): VideoMetadata?
}
