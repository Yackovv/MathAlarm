package com.example.myalarm.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.myalarm.R
import com.example.myalarm.databinding.ActivityAlarmBinding
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

    private var alarmId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bind.root)

        parseIntent()
        viewModel.getAlarm(alarmId)

        lifecycleScope.launch {
            viewModel.questionFlow.collect {
                bind.tvExample.text = it.example
            }
        }

        lifecycleScope.launch {
            viewModel.isRightAnswer.collect {
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
            viewModel.countOfQuestion.collect {
                if (it == 0) {
                    finish()
                }
            }
        }

        bind.ivInputAnswer.setOnClickListener {
            viewModel.checkAnswer(bind.etAnswer.text.toString())
        }


    }

    private fun parseIntent() {

        val args = intent
        if (!args.hasExtra(EXTRA_ALARM_ID)) {
            throw RuntimeException("Alarm ID is absent")
        }
        alarmId = args.getIntExtra(EXTRA_ALARM_ID, UNDEFINED_ID)

    }

    companion object {

        private const val EXTRA_ALARM_ID = "alarm_id"
        private const val UNDEFINED_ID = 0

        fun newIntentAlarmActivity(context: Context, alarmId: Int): Intent {
            return Intent(context, AlarmActivity::class.java).apply {
                putExtra(EXTRA_ALARM_ID, alarmId)
            }
        }
    }

}