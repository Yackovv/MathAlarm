package com.example.domain.domain.usecases

import com.example.domain.domain.enteties.Alarm
import com.example.domain.domain.repository.AlarmRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAlarmListUseCase @Inject constructor(
    private val repository: AlarmRepository
) {
    operator fun invoke(): Flow<List<Alarm>> {
        return repository.getAlarmList()
    }
}