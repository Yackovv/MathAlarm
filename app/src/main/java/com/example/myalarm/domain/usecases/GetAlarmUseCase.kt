package com.example.myalarm.domain.usecases

import com.example.myalarm.domain.enteties.Alarm
import com.example.myalarm.domain.repository.AlarmRepository

class GetAlarmUseCase(
    private val repository: AlarmRepository
) {
    suspend operator fun invoke(alarmId: Int): Alarm {
        return repository.getAlarm(alarmId)
    }
}