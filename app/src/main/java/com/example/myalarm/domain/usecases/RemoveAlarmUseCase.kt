package com.example.myalarm.domain.usecases

import com.example.myalarm.domain.enteties.Alarm
import com.example.myalarm.domain.repository.AlarmRepository

class RemoveAlarmUseCase(
    private val repository: AlarmRepository
) {
    operator fun invoke(alarm: Alarm) {
        repository.removeAlarm(alarm)
    }
}