package com.example.myalarm.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.domain.enteties.Alarm
import com.example.domain.domain.enteties.Level
import com.example.domain.domain.enteties.Question
import com.example.domain.domain.usecases.EditAlarmUseCase
import com.example.domain.domain.usecases.GenerateQuestionUseCase
import com.example.domain.domain.usecases.GetAlarmUseCase
import com.example.myalarm.logg
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class AlarmSettingViewModel @Inject constructor(
    private val editAlarmUseCase: EditAlarmUseCase,
    private val generateQuestionUseCase: GenerateQuestionUseCase,
    private val getAlarmUseCase: GetAlarmUseCase
) : ViewModel() {

    private lateinit var newAlarm: Alarm
    val alarmFlow = MutableSharedFlow<Alarm>()
    val questionFlow = MutableSharedFlow<Question>(replay = 1)

    fun getAlarm(alarmId: Int) {
        viewModelScope.launch {
            val alarm = getAlarmUseCase.invoke(alarmId)
            alarmFlow.emit(alarm)
            newAlarm = alarm
        }
    }

    fun generateQuestion(level: Level) {
        viewModelScope.launch {
            questionFlow.emit(
                generateQuestionUseCase.invoke(level)
            )
        }
    }

    fun setupLevelAndCountOfQuestion(level: Level, countQuestion: Int) {
        viewModelScope.launch(Dispatchers.Unconfined) {
            val changedAlarm = newAlarm.copy(level = level, countQuestion = countQuestion)
            editAlarmUseCase.invoke(changedAlarm)
            logg("From AlarmSettingViewModel")
            logg("alarmId = ${changedAlarm.id}, level = $level, count question = $countQuestion")
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
        val changedAlarm = newAlarm.copy(
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