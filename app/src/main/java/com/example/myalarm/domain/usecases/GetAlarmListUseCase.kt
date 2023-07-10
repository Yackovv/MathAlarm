package com.example.myalarm.domain.usecases

import com.example.myalarm.domain.enteties.Alarm
import com.example.myalarm.domain.repository.AlarmRepository
import kotlinx.coroutines.flow.Flow

class GetAlarmListUseCase(
    private val repository: AlarmRepository
) {
    operator fun invoke(): Flow<List<Alarm>> {
        return repository.getAlarmList()
    }
}