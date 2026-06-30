package com.example.mathalarm.alarm

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.mathalarm.R
import com.example.mathalarm.presentation.alarmring.AlarmRingActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AlarmNotificationHelper @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    fun showAlarmNotification(
        alarmId: Long
    ) {
        createAlarmNotificationChannel()

        val fullScreenIntent = Intent(
            context,
            AlarmRingActivity::class.java
        ).apply {
            putExtra(AlarmRingActivity.EXTRA_ALARM_ID, alarmId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val fullScreenPendingIntent = PendingIntent.getActivity(
            context,
            alarmId.toInt(),
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(
            context,
            CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Math Alarm")
            .setContentText("Wake up! Solve the math challenge.")
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .setAutoCancel(false)
            .setContentIntent(fullScreenPendingIntent)
            .setFullScreenIntent(
                fullScreenPendingIntent,
                true
            )
            .build()

        if (canPostNotifications()) {
            showNotification(
                alarmId = alarmId,
                notification = notification
            )
        } else {
            context.startActivity(fullScreenIntent)
        }
    }

    fun cancelAlarmNotification(
        alarmId: Long
    ) {
        NotificationManagerCompat
            .from(context)
            .cancel(alarmId.toInt())
    }

    private fun createAlarmNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }

        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Alarm ringing alerts"
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            enableVibration(true)
        }

        val notificationManager = context.getSystemService(
            NotificationManager::class.java
        )

        notificationManager.createNotificationChannel(channel)
    }

    private fun canPostNotifications(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.checkSelfPermission(
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    @SuppressLint("MissingPermission")
    private fun showNotification(
        alarmId: Long,
        notification: Notification
    ) {
        NotificationManagerCompat
            .from(context)
            .notify(
                alarmId.toInt(),
                notification
            )
    }

    companion object {
        const val CHANNEL_ID = "alarm_full_screen_channel"
        const val CHANNEL_NAME = "Alarm alerts"
    }
}