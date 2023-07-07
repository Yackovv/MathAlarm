package com.example.myalarm.domain.usecases

import com.example.myalarm.domain.enteties.Alarm
import com.example.myalarm.domain.repository.AlarmRepository

class GetAlarmListUseCase(
    private val repository: AlarmRepository
) {
    operator fun invoke(): List<Alarm>{
        return repository.getAlarmList()
    }
}