package com.example.sendready.core.compression

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.sendready.MainActivity
import com.example.sendready.R
import com.example.sendready.core.domain.model.CompressionProgressStats

class CompressionNotificationManager(private val context: Context) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Video Compression",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Shows progress of video compressions"
        }
        notificationManager.createNotificationChannel(channel)
    }

    fun getBaseNotification(): Notification {
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("SendReady")
            .setContentText("Preparing compression...")
            .setSmallIcon(R.mipmap.ic_launcher) // Use app icon or specific notification icon
            .setOngoing(true)
            .build()
    }

    fun updateProgressNotification(stats: CompressionProgressStats) {
        val remainingSeconds = stats.estimatedRemainingTimeMs / 1000
        val remainingMinutes = remainingSeconds / 60
        val remainingSecs = remainingSeconds % 60
        val timeString = String.format("%dm %02ds", remainingMinutes, remainingSecs)

        val text = "Video ${stats.currentFileIndex} / ${stats.totalFiles} | Remaining: $timeString"

        val cancelIntent = Intent(context, CompressionForegroundService::class.java).apply {
            action = CompressionForegroundService.ACTION_CANCEL
        }
        val cancelPendingIntent = PendingIntent.getService(
            context,
            0,
            cancelIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val openAppIntent = Intent(context, MainActivity::class.java)
        val openAppPendingIntent = PendingIntent.getActivity(
            context,
            1,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Compressing videos (${stats.overallProgressPercent}%)")
            .setContentText(text)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setProgress(100, stats.overallProgressPercent, false)
            .setContentIntent(openAppPendingIntent)
            .addAction(0, "Cancel", cancelPendingIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    fun showFinishedNotification(savedMb: Long) {
        val openAppIntent = Intent(context, MainActivity::class.java)
        val openAppPendingIntent = PendingIntent.getActivity(
            context,
            1,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Compression Complete")
            .setContentText("Saved $savedMb MB. Tap to view results.")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(openAppPendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(FINISHED_NOTIFICATION_ID, notification)
    }

    fun cancelNotification() {
        notificationManager.cancel(NOTIFICATION_ID)
    }

    companion object {
        const val CHANNEL_ID = "compression_channel"
        const val NOTIFICATION_ID = 1
        const val FINISHED_NOTIFICATION_ID = 2
    }
}
