package com.example.myalarm.domain.usecases

import com.example.myalarm.domain.enteties.Level
import com.example.myalarm.domain.enteties.Question
import com.example.myalarm.domain.repository.AlarmRepository
import javax.inject.Inject

class GenerateQuestionUseCase @Inject constructor(
    private val repository: AlarmRepository
) {
    operator fun invoke(level: Level): Question{
        return repository.generateQuestion(level)
    }
}