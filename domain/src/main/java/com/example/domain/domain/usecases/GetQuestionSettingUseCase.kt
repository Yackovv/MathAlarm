package com.example.domain.domain.usecases

import com.example.domain.domain.enteties.Level
import com.example.domain.domain.enteties.QuestionSetting
import com.example.domain.domain.repository.AlarmRepository

class GetQuestionSettingUseCase(
    private val repository: AlarmRepository
) {
    operator fun invoke(level: Level): QuestionSetting {
        return repository.getQuestionSettings(level)
    }
}