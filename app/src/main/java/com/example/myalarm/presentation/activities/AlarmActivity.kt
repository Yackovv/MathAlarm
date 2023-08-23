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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
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
    private val vibrator by lazy {
        getSystemService(VIBRATOR_SERVICE) as Vibrator
    }
    private val longArray = longArrayOf(100, 200, 400, 800)
    private var isSecondOnStop = false
    private var alarmId = UNDEFINED_ID
    private var notificationId = UNDEFINED_ID
    private var isExampleEnd = false
    private lateinit var ringtone: Ringtone

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bind.root)

        parseIntent()
        setupMaxVolume()
        viewModel.getAlarm(alarmId)
        viewModel.generateQuestion()
        cancelNotification()
        flowCollects()
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
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

    private fun flowCollects() {
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
                    } else {
                        etAnswer.setText("")
                        ivInputAnswer.isVisible = false
                        ivWrongAnswer.isVisible = true
                        delay(1_000)
                        ivInputAnswer.isVisible = true
                        ivWrongAnswer.isVisible = false
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
                    finish()
                    startActivity(MainActivity.newIntentMainActivity(this@AlarmActivity))
                }
            }
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
        calendar.add(Calendar.SECOND, ALARM_RESTART_SECONDS)
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

    override fun onStop() {
        super.onStop()
        if (!isExampleEnd && isSecondOnStop) {
            restartAlarm()
        }
        isSecondOnStop = true
    }

    companion object {

        private const val EXTRA_ALARM_ID = "alarm_id"
        private const val UNDEFINED_ID = 0
        private const val ALARM_RESTART_SECONDS = 60
        private const val EXTRA_NOTIFICATION_ID = "notification_id"

        fun newIntentAlarmActivity(context: Context, alarmId: Int, notificationId: Int): Intent {
            return Intent(context, AlarmActivity::class.java).apply {
                putExtra(EXTRA_ALARM_ID, alarmId)
                putExtra(EXTRA_NOTIFICATION_ID, notificationId)
            }
        }
    }

}