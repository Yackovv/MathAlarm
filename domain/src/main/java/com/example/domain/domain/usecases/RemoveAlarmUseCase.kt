package com.example.domain.domain.usecases

import com.example.domain.domain.repository.AlarmRepository
import javax.inject.Inject

class RemoveAlarmUseCase @Inject constructor(
    private val repository: AlarmRepository
) {
    suspend operator fun invoke(alarmId: Int) {
        repository.removeAlarm(alarmId)
    }
}