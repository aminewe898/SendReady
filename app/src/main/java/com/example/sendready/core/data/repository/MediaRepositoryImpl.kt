package com.example.sendready.core.data.repository

import android.content.Context
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.OpenableColumns
import com.example.sendready.core.domain.model.VideoMetadata
import com.example.sendready.core.domain.repository.MediaRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : MediaRepository {

    override suspend fun getVideoMetadata(uriString: String): VideoMetadata? = withContext(Dispatchers.IO) {
        val uri = Uri.parse(uriString)
        var filename = "Unknown Video"
        var sizeBytes = 0L

        // Get file name and size
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (nameIndex != -1) filename = cursor.getString(nameIndex) ?: filename
                if (sizeIndex != -1) sizeBytes = cursor.getLong(sizeIndex)
            }
        }

        var durationMs = 0L
        var width = 0
        var height = 0
        var bitrate = 0
        var fps = 0f
        var codec = "Unknown"
        var container = context.contentResolver.getType(uri) ?: "video/mp4"

        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(context, uri)
            durationMs = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
            width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toIntOrNull() ?: 0
            height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toIntOrNull() ?: 0
            bitrate = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)?.toIntOrNull() ?: 0
            
            // Try to handle rotation
            val rotation = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)?.toIntOrNull() ?: 0
            if (rotation == 90 || rotation == 270) {
                val temp = width
                width = height
                height = temp
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        } finally {
            retriever.release()
        }

        // Use MediaExtractor for precise codec and FPS information
        val extractor = MediaExtractor()
        try {
            extractor.setDataSource(context, uri, null)
            val numTracks = extractor.trackCount
            for (i in 0 until numTracks) {
                val format = extractor.getTrackFormat(i)
                val mime = format.getString(MediaFormat.KEY_MIME)
                if (mime?.startsWith("video/") == true) {
                    codec = mime.removePrefix("video/")
                    if (format.containsKey(MediaFormat.KEY_FRAME_RATE)) {
                        // Some devices provide integer frame rate, some provide float
                        try {
                            fps = format.getInteger(MediaFormat.KEY_FRAME_RATE).toFloat()
                        } catch (e: Exception) {
                            try {
                                fps = format.getFloat(MediaFormat.KEY_FRAME_RATE)
                            } catch (e2: Exception) {
                                // Ignore
                            }
                        }
                    }
                    break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            extractor.release()
        }

        return@withContext VideoMetadata(
            uriString = uriString,
            filename = filename,
            sizeBytes = sizeBytes,
            durationMs = durationMs,
            width = width,
            height = height,
            bitrate = bitrate,
            fps = fps,
            codec = codec.uppercase(),
            container = container
        )
    }
}
