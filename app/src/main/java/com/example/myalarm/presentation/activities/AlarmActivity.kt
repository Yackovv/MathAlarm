package com.example.myalarm.presentation.activities

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Bundle
import android.os.Vibrator
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.example.myalarm.R
import com.example.myalarm.databinding.ActivityAlarmBinding
import com.example.myalarm.presentation.AlarmReceiver
import com.example.myalarm.presentation.viewmodels.AlarmViewModel
import com.example.myalarm.services.AlarmWorker
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

class AlarmActivity : AppCompatActivity() {

    private val bind by lazy {
        ActivityAlarmBinding.inflate(layoutInflater)
    }
    private val viewModel by lazy {
        ViewModelProvider(this)[AlarmViewModel::class.java]
    }
    private val colorRed by lazy {
        ContextCompat.getColor(this, R.color.red_color)
    }
    private val colorGrey by lazy {
        ContextCompat.getColor(this, R.color.setting_background_color)
    }
    private val vibrator by lazy {
        getSystemService(VIBRATOR_SERVICE) as Vibrator
    }
    private val longArray = longArrayOf(100, 200, 400, 800)
    private var alarmId = UNDEFINED_ID
    private var notificationId = UNDEFINED_ID
    private var isExampleEnd = false
    private lateinit var ringtone: Ringtone

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bind.root)

        parseIntent()
        setupMaxVolume()
        setupFlagsForWindow()
        viewModel.getAlarm(alarmId)
        viewModel.generateQuestion()
        cancelNotification()

        lifecycleScope.launch {
            viewModel.vibrationFlow.collect {
                if (it) {
                    vibrator.vibrate(longArray, 1, getAudioAttributes())
                }
            }
        }

        lifecycleScope.launch {
            viewModel.timerFlow.collect {
                bind.tvTimer.text = it
                if (it == "0") {
                    ringtone.play()
                    bind.ivSound.isVisible = true
                    bind.ivNoSound.visibility = View.GONE
                }
            }
        }

        lifecycleScope.launch {
            viewModel.ringtoneUriFlow.collect {
                ringtone = RingtoneManager.getRingtone(this@AlarmActivity, it)
                ringtone.play()
            }
        }

        lifecycleScope.launch {
            viewModel.questionFlow.collect {
                bind.tvExample.text = it.example
            }
        }

        lifecycleScope.launch {
            viewModel.isRightAnswerFlow.collect {
                with(bind) {
                    if (it) {
                        viewModel.generateQuestion()
                        etAnswer.setText("")
                        ivInputAnswer.isVisible = true
                        etAnswer.setBackgroundColor(colorGrey)
                    } else {
                        etAnswer.setBackgroundColor(colorRed)
                        etAnswer.setText("")
                        delay(1_000)
                        etAnswer.setBackgroundColor(colorGrey)
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.countOfQuestionFlow.collect {
                bind.tvCounter.text = it.toString()
                if (it == 0) {
                    isExampleEnd = true
                    ringtone.stop()
                    vibrator.cancel()
                    setupAlarmOnNextDay()
                    finishAffinity()
                }
            }
        }

        bind.ivInputAnswer.setOnClickListener {
            viewModel.checkAnswer(bind.etAnswer.text.toString())
        }

        bind.ivSpeaker.setOnClickListener {
            ringtone.stop()
            viewModel.startTimer()
            vibrator.cancel()
            bind.ivSound.visibility = View.GONE
            bind.ivNoSound.isVisible = true
        }
    }

    private fun setupAlarmOnNextDay() {
        val workManager = WorkManager.getInstance(this@AlarmActivity)
        workManager.enqueueUniqueWork(
            AlarmWorker.WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            AlarmWorker.makeRequest(alarmId)
        )
    }

    private fun restartAlarm() {
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val receiverIntent = AlarmReceiver.newIntentAlarmReceiver(this, alarmId)
        val pendingIntentRestart = PendingIntent.getBroadcast(
            this,
            alarmId,
            receiverIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.SECOND, 30)
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntentRestart
        )
    }

    private fun setupMaxVolume() {
        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL

        val maxCallVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING)
        audioManager.setStreamVolume(
            AudioManager.STREAM_RING,
            maxCallVolume,
            AudioManager.FLAG_SHOW_UI
        )
    }

    private fun setupFlagsForWindow() {
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )
    }

    private fun getAudioAttributes(): AudioAttributes {
        return AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .build()
    }

    private fun cancelNotification() {
        val notificationManager =
            getSystemService(NotificationManager::class.java) as NotificationManager
        notificationManager.cancel(notificationId)
    }

    private fun parseIntent() {

        val intent = intent
        if (!intent.hasExtra(EXTRA_ALARM_ID)) {
            throw RuntimeException("Alarm ID is absent")
        }

        if (!intent.hasExtra(EXTRA_NOTIFICATION_ID)) {
            throw RuntimeException("Notification ID is absent")
        }

        alarmId = intent.getIntExtra(EXTRA_ALARM_ID, UNDEFINED_ID)
        notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, UNDEFINED_ID)
    }

    override fun onPause() {
        super.onPause()
        if (!isExampleEnd) {
            restartAlarm()
        }
    }

    companion object {

        private const val EXTRA_ALARM_ID = "alarm_id"
        private const val UNDEFINED_ID = 0
        private const val EXTRA_NOTIFICATION_ID = "notification_id"

        fun newIntentAlarmActivity(context: Context, alarmId: Int, notificationId: Int): Intent {
            return Intent(context, AlarmActivity::class.java).apply {
                putExtra(EXTRA_ALARM_ID, alarmId)
                putExtra(EXTRA_NOTIFICATION_ID, notificationId)
            }
        }
    }

}