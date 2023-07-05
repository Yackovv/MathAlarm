package com.example.myalarm.domain.usecases

import com.example.myalarm.domain.enteties.Level
import com.example.myalarm.domain.enteties.QuestionSetting
import com.example.myalarm.domain.repository.AlarmRepository

class GetQuestionSettingUseCase(
    private val repository: AlarmRepository
) {
    operator fun invoke(level: Level): QuestionSetting{
        return repository.getQuestionSettings(level)
    }
}