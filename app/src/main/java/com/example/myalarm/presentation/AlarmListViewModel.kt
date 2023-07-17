package com.example.myalarm.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myalarm.data.AlarmRepositoryImpl
import com.example.myalarm.domain.enteties.Alarm
import com.example.myalarm.domain.usecases.EditAlarmUseCase
import com.example.myalarm.domain.usecases.GetAlarmListUseCase
import com.example.myalarm.domain.usecases.RemoveAlarmUseCase
import kotlinx.coroutines.launch

class AlarmListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AlarmRepositoryImpl(application)

    private val getAlarmListUseCase = GetAlarmListUseCase(repository)

    //    private val getAlarmUseCase = GetAlarmUseCase(repository)
    private val editAlarmUseCase = EditAlarmUseCase(repository)
    private val removeAlarmUseCase = RemoveAlarmUseCase(repository)

    val alarmList = getAlarmListUseCase.invoke()

    fun removeAlarm(alarm: Alarm) {
        viewModelScope.launch {
            removeAlarmUseCase.invoke(alarm)
        }
    }

    fun changeEnabledState(alarm: Alarm, isEnabled: Boolean) {
        viewModelScope.launch {
            val alarmCopy = alarm.copy(enabled = isEnabled)
            editAlarmUseCase.invoke(alarmCopy)
        }
    }


//    fun getAlarm(alarmId: Int) {
//        viewModelScope.launch {
//            getAlarmUseCase.invoke(alarmId)
//        }
}