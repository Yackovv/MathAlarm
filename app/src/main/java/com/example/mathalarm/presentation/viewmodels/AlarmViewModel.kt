package com.example.mathalarm.presentation.viewmodels

import android.net.Uri
import android.os.CountDownTimer
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.domain.enteties.Level
import com.example.domain.domain.enteties.Question
import com.example.domain.domain.usecases.GenerateQuestionUseCase
import com.example.domain.domain.usecases.GetAlarmUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class AlarmViewModel @Inject constructor(
    private val generateQuestionUseCase: GenerateQuestionUseCase,
    private val getAlarmUseCase: GetAlarmUseCase
) : ViewModel() {


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
    val vibrationFlow = MutableSharedFlow<Boolean>()

    fun getAlarm(alarmId: Int) {
        viewModelScope.launch {
            val alarm = getAlarmUseCase.invoke(alarmId)
            level = alarm.level
            numberQuestion = alarm.countQuestion
            countOfQuestionFlow.emit(numberQuestion)
            ringtoneUriFlow.emit(alarm.ringtoneUriString.toUri())
            vibrationFlow.emit(alarm.vibration)
            generateQuestion()
        }
    }

    fun generateQuestion() {
        viewModelScope.launch {
            if (isNumberQuestionZero()) {
                val question = generateQuestionUseCase.invoke(level)
                rightAnswer = question.answer
                questionFlow.emit(question)
            }
            timer.cancel()
        }
    }

    fun checkAnswer(userAnswer: String) {
        viewModelScope.launch {
            try {
                val answer = userAnswer.trim().toInt()
                if (answer == rightAnswer) {
                    isRightAnswerFlow.emit(true)
                    countOfQuestionFlow.emit(--numberQuestion)
                } else {
                    isRightAnswerFlow.emit(false)
                }
            } catch (_: NumberFormatException) {
            }
        }
    }

    fun startTimer() {
        if (isNumberQuestionZero()) {
            timer.cancel()
            timer.start()
        }
    }

    private fun isNumberQuestionZero(): Boolean {
        return numberQuestion > 0
    }

    private fun temp(mills: Long) = (mills / 1000).toString()

}