package com.example.domain.domain.repository

import com.example.domain.domain.enteties.Alarm
import com.example.domain.domain.enteties.Level
import com.example.domain.domain.enteties.Question
import com.example.domain.domain.enteties.QuestionSetting
import kotlinx.coroutines.flow.Flow

interface AlarmRepository {

    fun generateQuestion(level: Level): Question

    fun getAlarmList(): Flow<List<Alarm>>

    suspend fun addAlarm(alarm: Alarm): Long

    suspend fun removeAlarm(alarmId: Int)

    suspend fun editAlarm(alarm: Alarm)

    suspend fun getAlarm(alarmId: Int): Alarm

    fun getQuestionSettings(level: Level): QuestionSetting
}