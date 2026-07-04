package com.example.sendready.core.compression

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.ServiceCompat
import com.example.sendready.core.domain.model.CompressionState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class CompressionForegroundService : Service() {

    @Inject
    lateinit var compressionManager: CompressionManager

    private lateinit var notificationManager: CompressionNotificationManager
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        notificationManager = CompressionNotificationManager(this)
        
        // Listen to state changes to update notification or stop service
        compressionManager.state.onEach { state ->
            when (state) {
                is CompressionState.Idle -> {
                    // Nothing to do
                }
                is CompressionState.Compressing -> {
                    notificationManager.updateProgressNotification(state.stats)
                }
                is CompressionState.Finished -> {
                    notificationManager.showFinishedNotification(
                        (state.totalOriginalSizeBytes - state.totalCompressedSizeBytes) / (1024 * 1024)
                    )
                    stopSelf()
                }
                is CompressionState.Failed -> {
                    notificationManager.cancelNotification()
                    notificationManager.showFailedNotification(state.error)
                    stopSelf()
                }
                is CompressionState.Paused -> {
                    // Handle paused state in notification if needed
                }
            }
        }.launchIn(serviceScope)
        
        compressionManager.onQueueFinished = {
            stopSelf()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                startForegroundServiceCompat()
            }
            ACTION_CANCEL -> {
                compressionManager.cancel()
                notificationManager.cancelNotification()
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    private fun startForegroundServiceCompat() {
        val notification = notificationManager.getBaseNotification()
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // API 34+
            ServiceCompat.startForeground(
                this,
                CompressionNotificationManager.NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // API 29+
            ServiceCompat.startForeground(
                this,
                CompressionNotificationManager.NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            startForeground(CompressionNotificationManager.NOTIFICATION_ID, notification)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_CANCEL = "ACTION_CANCEL"
    }
}
