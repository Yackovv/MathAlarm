package com.example.myalarm.domain.usecases

import com.example.myalarm.domain.enteties.Level
import com.example.myalarm.domain.enteties.Question
import com.example.myalarm.domain.repository.AlarmRepository

class GenerateQuestionUseCase(
    private val repository: AlarmRepository
) {
    operator fun invoke(level: Level): Question{
        return repository.generateQuestion(level)
    }
}