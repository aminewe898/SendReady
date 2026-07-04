package com.example.sendready.core.compression

import androidx.media3.transformer.ExportException

fun Throwable.toUserFriendlyMessage(): String {
    if (this is ExportException) {
        return when (this.errorCode) {
            ExportException.ERROR_CODE_ENCODING_FORMAT_UNSUPPORTED -> 
                "The selected video or audio format is not supported on your device."
            else -> "Compression failed with an unexpected error (Code: ${this.errorCode})."
        }
    }
    return this.localizedMessage ?: "An unknown error occurred during compression."
}
