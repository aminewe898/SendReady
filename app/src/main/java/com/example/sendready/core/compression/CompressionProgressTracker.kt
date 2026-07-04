package com.example.sendready.core.compression

import com.example.sendready.core.domain.model.CompressionJob
import com.example.sendready.core.domain.model.CompressionProgressStats
import java.util.concurrent.ConcurrentHashMap

class CompressionProgressTracker {
    private val fileProgressMap = ConcurrentHashMap<String, Int>()
    private var startTimeMs: Long = 0
    private var currentJobs: List<CompressionJob> = emptyList()
    private var bytesSavedSoFar: Long = 0
    
    fun startTracking(jobs: List<CompressionJob>) {
        currentJobs = jobs
        fileProgressMap.clear()
        jobs.forEach { fileProgressMap[it.id] = 0 }
        startTimeMs = System.currentTimeMillis()
        bytesSavedSoFar = 0
    }
    
    fun updateProgress(jobId: String, percent: Int) {
        fileProgressMap[jobId] = percent
    }
    
    fun addBytesSaved(saved: Long) {
        bytesSavedSoFar += saved
    }
    
    fun getStats(currentJobId: String): CompressionProgressStats {
        if (currentJobs.isEmpty()) return CompressionProgressStats()
        
        var totalProgressSum = 0
        fileProgressMap.values.forEach { totalProgressSum += it }
        
        val overallProgressPercent = if (currentJobs.isNotEmpty()) {
            totalProgressSum / currentJobs.size
        } else 0
        
        val currentJobIndex = currentJobs.indexOfFirst { it.id == currentJobId }
        val currentJob = currentJobs.getOrNull(currentJobIndex)
        
        val currentFileProgressPercent = fileProgressMap[currentJobId] ?: 0
        
        val elapsedTimeMs = System.currentTimeMillis() - startTimeMs
        
        // Calculate speed based on elapsed time and overall progress
        // This is a naive estimate: Assuming total progress correlates to total bytes (it doesn't perfectly, but it's a good approximation)
        val totalBytes = currentJobs.sumOf { it.metadata.sizeBytes }
        val processedBytes = (totalBytes * (overallProgressPercent / 100f)).toLong()
        
        val speedBytesPerSecond = if (elapsedTimeMs > 0) {
            (processedBytes * 1000L) / elapsedTimeMs
        } else 0L
        
        val remainingBytes = totalBytes - processedBytes
        val estimatedRemainingTimeMs = if (speedBytesPerSecond > 0) {
            (remainingBytes * 1000L) / speedBytesPerSecond
        } else 0L
        
        val jobProgressList = currentJobs.mapIndexed { index, job ->
            val progress = fileProgressMap[job.id] ?: 0
            val status = when {
                progress == 100 -> com.example.sendready.core.domain.model.JobStatus.COMPLETED
                index < currentJobIndex -> com.example.sendready.core.domain.model.JobStatus.COMPLETED
                index == currentJobIndex -> com.example.sendready.core.domain.model.JobStatus.COMPRESSING
                else -> com.example.sendready.core.domain.model.JobStatus.PENDING
            }
            com.example.sendready.core.domain.model.JobProgress(
                id = job.id,
                name = job.metadata.filename,
                originalSizeBytes = job.metadata.sizeBytes,
                status = status,
                progressPercent = progress
            )
        }

        return CompressionProgressStats(
            overallProgressPercent = overallProgressPercent,
            currentFileProgressPercent = currentFileProgressPercent,
            currentFileIndex = currentJobIndex + 1,
            totalFiles = currentJobs.size,
            currentFileName = currentJob?.metadata?.filename ?: "",
            currentFileOriginalSizeBytes = currentJob?.metadata?.sizeBytes ?: 0L,
            speedBytesPerSecond = speedBytesPerSecond,
            estimatedRemainingTimeMs = estimatedRemainingTimeMs,
            elapsedTimeMs = elapsedTimeMs,
            bytesSavedSoFar = bytesSavedSoFar,
            jobProgressList = jobProgressList
        )
    }
}
