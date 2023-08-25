package com.example.domain.domain.usecases

import com.example.domain.domain.enteties.Alarm
import com.example.domain.domain.repository.AlarmRepository
import javax.inject.Inject

class GetAlarmUseCase @Inject constructor(
    private val repository: AlarmRepository
) {
    suspend operator fun invoke(alarmId: Int): Alarm {
        return repository.getAlarm(alarmId)
    }
}