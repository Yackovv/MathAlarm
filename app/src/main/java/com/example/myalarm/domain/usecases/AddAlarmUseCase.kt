package com.example.myalarm.domain.usecases

import com.example.myalarm.domain.enteties.Alarm
import com.example.myalarm.domain.repository.AlarmRepository
import javax.inject.Inject

class AddAlarmUseCase @Inject constructor(
    private val repository:AlarmRepository
) {
    suspend operator fun invoke(alarm: Alarm): Long{
        return repository.addAlarm(alarm)
    }
}