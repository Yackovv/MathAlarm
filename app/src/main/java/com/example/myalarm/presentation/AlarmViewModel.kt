package com.example.myalarm.presentation

import android.app.Application
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
    private var numberQuestion = 1
    val questionFlow = MutableSharedFlow<Question>(replay = 1)
    val isRightAnswer = MutableSharedFlow<Boolean>(replay = 1)
    val countOfQuestion = MutableSharedFlow<Int>(replay = 1)

    fun getAlarm(alarmId: Int) {
        viewModelScope.launch {
            val alarm = getAlarmUseCase.invoke(alarmId)
            level = alarm.level
            numberQuestion = alarm.countQuestion
            countOfQuestion.emit(numberQuestion)
        }
    }

    fun generateQuestion() {
        viewModelScope.launch {
            val question = generateQuestionUseCase.invoke(level)
            rightAnswer = question.answer
            questionFlow.emit(question)
            countOfQuestion.emit(--numberQuestion)
        }
    }

    fun checkAnswer(userAnswer: String){
        viewModelScope.launch {
            val answer = userAnswer.trim().toInt()
            isRightAnswer.emit(answer == rightAnswer)
        }
    }

}