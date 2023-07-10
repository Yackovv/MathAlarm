package com.example.myalarm.domain.usecases

import com.example.myalarm.domain.enteties.Alarm
import com.example.myalarm.domain.repository.AlarmRepository

class AddAlarmUseCase(
    private val repository:AlarmRepository
) {
    suspend operator fun invoke(alarm: Alarm){
        repository.addAlarm(alarm)
    }
}