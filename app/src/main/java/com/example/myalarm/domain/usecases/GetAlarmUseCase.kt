package com.example.myalarm.domain.usecases

import com.example.myalarm.domain.enteties.Alarm
import com.example.myalarm.domain.repository.AlarmRepository
import javax.inject.Inject

class GetAlarmUseCase @Inject constructor(
    private val repository: AlarmRepository
) {
    suspend operator fun invoke(alarmId: Int): Alarm {
        return repository.getAlarm(alarmId)
    }
}