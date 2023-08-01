package com.example.myalarm.presentation

import android.app.Application
import android.net.Uri
import android.os.CountDownTimer
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myalarm.data.AlarmRepositoryImpl
import com.example.myalarm.domain.enteties.Level
import com.example.myalarm.domain.enteties.Question
import com.example.myalarm.domain.usecases.GenerateQuestionUseCase
import com.example.myalarm.domain.usecases.GetAlarmUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class AlarmViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AlarmRepositoryImpl(application)

    private val generateQuestionUseCase = GenerateQuestionUseCase(repository)
    private val getAlarmUseCase = GetAlarmUseCase(repository)

    private var level = Level.EASY
    private var rightAnswer: Int = 0
    private var numberQuestion = 10

    private val timer by lazy {
        object : CountDownTimer(15_000, 1_000) {
            override fun onTick(second: Long) {
                viewModelScope.launch {
                    timerFlow.emit(temp((second)))
                }
            }

            override fun onFinish() {
                viewModelScope.launch {
                    timerFlow.emit("0")
                    cancel()
                }
            }
        }
    }

    val questionFlow = MutableSharedFlow<Question>(replay = 1)
    val isRightAnswerFlow = MutableSharedFlow<Boolean>(replay = 1)
    val countOfQuestionFlow = MutableSharedFlow<Int>(replay = 1)
    val ringtoneUriFlow = MutableSharedFlow<Uri>(replay = 1)
    val timerFlow = MutableSharedFlow<String>()

    fun getAlarm(alarmId: Int) {
        viewModelScope.launch {
            Log.d("11111", "Alarm ID: $alarmId")
            val alarm = getAlarmUseCase.invoke(alarmId)
            level = alarm.level
            numberQuestion = alarm.countQuestion
            countOfQuestionFlow.emit(numberQuestion)
            ringtoneUriFlow.emit(alarm.ringtoneUriString.toUri())
        }
    }

    fun generateQuestion() {
        viewModelScope.launch {
            val question = generateQuestionUseCase.invoke(level)
            rightAnswer = question.answer
            questionFlow.emit(question)
            Log.d("11111", question.example)
            countOfQuestionFlow.emit(--numberQuestion)
        }
    }

    fun checkAnswer(userAnswer: String) {
        viewModelScope.launch {
            try {
                val answer = userAnswer.trim().toInt()
                isRightAnswerFlow.emit(answer == rightAnswer)
            } catch (_: NumberFormatException) {}
        }
    }

    fun startTimer() {
        timer.cancel()
        timer.start()
    }

    private fun temp(mills: Long) = (mills / 1000).toString()

}