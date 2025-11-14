package expo.modules.backgroundcall

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.PermissionChecker

class CallForegroundService : Service() {

  private var wakeLock: PowerManager.WakeLock? = null

  companion object {
    const val ACTION_START = "expo.modules.backgroundcall.START"
    const val ACTION_STOP = "expo.modules.backgroundcall.STOP"
    private const val CHANNEL_ID = "video_call_channel"
    private const val NOTIFICATION_ID = 1001

    @Volatile
    var isRunning = false
      private set
  }

  override fun onCreate() {
    super.onCreate()
    createNotificationChannel()
    acquireWakeLock()
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    when (intent?.action) {
      ACTION_START -> {
        try {
          val title = intent.getStringExtra("title") ?: "Video Call"
          val message = intent.getStringExtra("message") ?: "Call in progress"


          val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val cameraPermission =
              PermissionChecker.checkSelfPermission(this, Manifest.permission.CAMERA)
            val microphonePermission =
              PermissionChecker.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)

            if (cameraPermission == PermissionChecker.PERMISSION_GRANTED && microphonePermission == PermissionChecker.PERMISSION_GRANTED) {
              ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA or ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE
            } else if (cameraPermission == PermissionChecker.PERMISSION_GRANTED) {
              ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA
            } else if (microphonePermission == PermissionChecker.PERMISSION_GRANTED) {
              ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE
              } else {
              0
            }
          } else {
            0
          }

          ServiceCompat.startForeground(this,NOTIFICATION_ID, createNotification(title, message), permissions)
          isRunning = true
        } catch (e: Exception) {
          e.printStackTrace()
        }
      }
      ACTION_STOP -> {
        stopForegroundService()
      }
    }
    return START_STICKY
  }

  override fun onBind(intent: Intent?): IBinder? = null

  override fun onDestroy() {
    super.onDestroy()
    isRunning = false
    releaseWakeLock()
  }

  private fun createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(
        CHANNEL_ID,
        "Video Call Service",
        NotificationManager.IMPORTANCE_HIGH
      ).apply {
        description = "Keeps video call running in background"
        setSound(null, null)
      }

      val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      notificationManager.createNotificationChannel(channel)
    }
  }

  private fun createNotification(title: String, message: String): Notification {
    // Create intent to open app when notification is tapped
    val notificationIntent = packageManager.getLaunchIntentForPackage(packageName)
    val pendingIntent = PendingIntent.getActivity(
      this,
      0,
      notificationIntent,
      PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    // Create end call action
    val stopIntent = Intent(this, CallForegroundService::class.java).apply {
      action = ACTION_STOP
    }
    val stopPendingIntent = PendingIntent.getService(
      this,
      0,
      stopIntent,
      PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    return NotificationCompat.Builder(this, CHANNEL_ID)
      .setContentTitle(title)
      .setContentText(message)
      .setSmallIcon(android.R.drawable.ic_menu_call)
      .setContentIntent(pendingIntent)
      .setOngoing(true)
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setCategory(NotificationCompat.CATEGORY_CALL)
      .addAction(
        android.R.drawable.ic_menu_close_clear_cancel,
        "End Call",
        stopPendingIntent
      )
      .build()
  }

  private fun acquireWakeLock() {
    try {
      val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
      wakeLock = powerManager.newWakeLock(
        PowerManager.PARTIAL_WAKE_LOCK,
        "VideoCallService::WakeLock"
      ).apply {
        acquire(10 * 60 * 1000L) // 10 minutes max
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  private fun releaseWakeLock() {
    try {
      wakeLock?.let {
        if (it.isHeld) {
          it.release()
        }
      }
      wakeLock = null
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  private fun stopForegroundService() {
    stopForeground(STOP_FOREGROUND_REMOVE)
    stopSelf()
  }
}
