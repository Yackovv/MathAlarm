package com.example.domain.domain.usecases

import com.example.domain.domain.enteties.Level
import com.example.domain.domain.enteties.Question
import com.example.domain.domain.repository.AlarmRepository
import javax.inject.Inject

class GenerateQuestionUseCase @Inject constructor(
    private val repository: AlarmRepository
) {
    operator fun invoke(level: Level): Question {
        return repository.generateQuestion(level)
    }
}