package com.example.myalarm.presentation.activities

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.example.myalarm.R
import com.example.myalarm.databinding.ActivityAlarmBinding
import com.example.myalarm.presentation.viewmodels.AlarmViewModel
import com.example.myalarm.services.AlarmWorker
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

    private var alarmId = UNDEFINED_ID

    private var notificationId = UNDEFINED_ID

    private lateinit var ringtone: Ringtone

    private fun log(str: String){
        Log.d("11111", str)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bind.root)

        log("onCreate")

        parseIntent()
        viewModel.getAlarm(alarmId)
        viewModel.generateQuestion()
        cancelNotification()

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
                if (it == 0) {
                    ringtone.stop()
                    val workManager = WorkManager.getInstance(this@AlarmActivity)
                    workManager.enqueueUniqueWork(
                        AlarmWorker.WORK_NAME,
                        ExistingWorkPolicy.REPLACE,
                        AlarmWorker.makeRequest(alarmId)
                    )
                    finish()
                }
            }
        }

        bind.ivInputAnswer.setOnClickListener {
            viewModel.checkAnswer(bind.etAnswer.text.toString())
        }

        bind.ivSpeaker.setOnClickListener {
            ringtone.stop()
            viewModel.startTimer()
            bind.ivSound.visibility = View.GONE
            bind.ivNoSound.isVisible = true
        }
    }

    private fun cancelNotification(){
        val notificationManager =
            getSystemService(NotificationManager::class.java) as NotificationManager
        notificationManager.cancel(notificationId)
    }

    private fun parseIntent() {

        val intent = intent
        if (!intent.hasExtra(EXTRA_ALARM_ID)) {
            throw RuntimeException("Alarm ID is absent")
        }

        if (!intent.hasExtra(EXTRA_NOTIFICATION_ID)){
            throw RuntimeException("Notification ID is absent")
        }

        alarmId = intent.getIntExtra(EXTRA_ALARM_ID, UNDEFINED_ID)
        notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, UNDEFINED_ID)
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