package com.example.myalarm.presentation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.AUDIO_SERVICE
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.example.myalarm.R
import com.example.myalarm.presentation.activities.AlarmActivity
import com.example.myalarm.services.AlarmWorker

class AlarmReceiver : BroadcastReceiver() {

    private var alarmId = UNDEFINED_ID

    override fun onReceive(context: Context, intent: Intent) {
        parseIntent(intent)

        val notificationManager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val audioManager = context.getSystemService(AUDIO_SERVICE) as AudioManager

        if (audioManager.ringerMode == AudioManager.RINGER_MODE_SILENT) {
            if (notificationManager.isNotificationPolicyAccessGranted) {
                createNotification(notificationManager, context)
            } else {
                setupAlarmOnNextDay(context)
            }
        } else {
            createNotification(notificationManager, context)
        }
    }

    private fun setupAlarmOnNextDay(context: Context) {
        val workManager = WorkManager.getInstance(context)
        workManager.enqueueUniqueWork(
            AlarmWorker.WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            AlarmWorker.makeRequest(alarmId)
        )
    }

    private fun createNotification(
        notificationManager: NotificationManager,
        context: Context
    ) {
        createNotificationChannel(notificationManager)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentText(context.getString(R.string.notification_text))
            .setContentTitle(context.getString(R.string.app_name))
            .setFullScreenIntent(getPendingIntent(context), true)
            .setOngoing(true)
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