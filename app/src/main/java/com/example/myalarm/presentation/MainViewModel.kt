package com.example.myalarm.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.myalarm.data.AlarmRepositoryImpl
import com.example.myalarm.domain.usecases.EditAlarmUseCase
import com.example.myalarm.domain.usecases.GetAlarmListUseCase
import com.example.myalarm.domain.usecases.GetAlarmUseCase
import com.example.myalarm.domain.usecases.RemoveAlarmUseCase

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AlarmRepositoryImpl(application)

    private val getAlarmListUseCase = GetAlarmListUseCase(repository)
    private val getAlarmUseCase = GetAlarmUseCase(repository)
    private val editAlarmUseCase = EditAlarmUseCase(repository)
    private val removeAlarmUseCase = RemoveAlarmUseCase(repository)

    val alarmList = getAlarmListUseCase.invoke()
}