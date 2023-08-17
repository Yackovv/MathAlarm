package com.example.myalarm.presentation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.myalarm.R
import com.example.myalarm.presentation.activities.AlarmActivity

class AlarmReceiver : BroadcastReceiver() {

    private var alarmId = UNDEFINED_ID

    override fun onReceive(context: Context, intent: Intent) {

        parseIntent(intent)

        val notificationManager =
            context.getSystemService(NotificationManager::class.java) as NotificationManager

        createNotificationChannel(notificationManager)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setFullScreenIntent(getPendingIntent(context), true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }

    }

    private fun getPendingIntent(context: Context): PendingIntent {

        val intent = AlarmActivity.newIntentAlarmActivity(context, alarmId, NOTIFICATION_ID)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK

        return PendingIntent.getActivity(
            context,
            PENDING_INTENT_RC,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    private fun parseIntent(intent: Intent) {
        if (!intent.hasExtra(EXTRA_ALARM_ID)) {
            throw RuntimeException("Alarm id is absent")
        }
        alarmId = intent.getIntExtra(EXTRA_ALARM_ID, 0)
    }

    companion object {
        private const val PENDING_INTENT_RC = 10
        private const val NOTIFICATION_ID = 100
        private const val CHANNEL_ID = "channel_id"
        private const val CHANNEL_NAME = "channel_name"
        private const val UNDEFINED_ID = 0
        private const val EXTRA_ALARM_ID = "alarm_id"

        fun newIntentAlarmReceiver(context: Context, alarmId: Int): Intent {
            return Intent(context, AlarmReceiver::class.java).apply {
                putExtra(EXTRA_ALARM_ID, alarmId)
            }
        }
    }

}