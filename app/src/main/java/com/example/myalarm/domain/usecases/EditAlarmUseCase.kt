package com.example.myalarm.domain.usecases

import com.example.myalarm.domain.enteties.Alarm
import com.example.myalarm.domain.repository.AlarmRepository

class EditAlarmUseCase(
    private val repository: AlarmRepository
) {

    suspend operator fun invoke(alarm: Alarm){
        repository.editAlarm(alarm)
    }
}