package com.example.myalarm.presentation.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myalarm.data.AlarmRepositoryImpl
import com.example.myalarm.domain.enteties.Alarm
import com.example.myalarm.domain.enteties.Level
import com.example.myalarm.domain.enteties.Question
import com.example.myalarm.domain.usecases.EditAlarmUseCase
import com.example.myalarm.domain.usecases.GenerateQuestionUseCase
import com.example.myalarm.domain.usecases.GetAlarmUseCase
import com.example.myalarm.logg
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AlarmSettingViewModel(private val application: Application) : AndroidViewModel(application) {

    private val repository = AlarmRepositoryImpl(application)

    private val editAlarmUseCase = EditAlarmUseCase(repository)
    private val generateQuestionUseCase = GenerateQuestionUseCase(repository)
    private val getAlarmUseCase = GetAlarmUseCase(repository)

    val alarmFlow = MutableStateFlow(Alarm())
    val questionFlow = MutableSharedFlow<Question>(replay = 1)

    fun getAlarm(alarmId: Int) {
        viewModelScope.launch {
            val alarm = getAlarmUseCase.invoke(alarmId)
            alarmFlow.value = alarm
        }
    }

    fun generateQuestion(level: Level) {
        viewModelScope.launch {
            questionFlow.emit(
                generateQuestionUseCase.invoke(level)
            )
        }
    }

    fun setupLevelAndCountOfQuestion(alarmId: Int, level: Level, countQuestion: Int) {
        viewModelScope.launch(Dispatchers.Unconfined) {
            val alarm = getAlarmUseCase.invoke(alarmId)
            val changedAlarm = alarm.copy(level = level, countQuestion = countQuestion)
            editAlarmUseCase.invoke(changedAlarm)
            logg("From AlarmSettingViewModel")
            logg("alarmId = $alarmId, level = $level, count question = $countQuestion")
        }
    }

    fun editAlarm(
        alarmTime: String,
        level: Level,
        countQuestion: Int,
        ringtoneUriString: String,
        vibration: Boolean = true,
        monday: Boolean = false,
        tuesday: Boolean = false,
        wednesday: Boolean = false,
        thursday: Boolean = false,
        friday: Boolean = false,
        saturday: Boolean = false,
        sunday: Boolean = false
    ) {
        val changedAlarm = alarmFlow.value.copy(
            enabled = true,
            alarmTime = alarmTime,
            level = level,
            countQuestion = countQuestion,
            ringtoneUriString = ringtoneUriString,
            vibration = vibration,
            monday = monday,
            tuesday = tuesday,
            wednesday = wednesday,
            thursday = thursday,
            friday = friday,
            saturday = saturday,
            sunday = sunday
        )
        viewModelScope.launch {
            editAlarmUseCase.invoke(changedAlarm)
        }
    }
}