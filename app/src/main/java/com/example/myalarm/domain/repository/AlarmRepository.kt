package com.example.myalarm.domain.repository

import com.example.myalarm.domain.enteties.Alarm
import com.example.myalarm.domain.enteties.Level
import com.example.myalarm.domain.enteties.Question
import com.example.myalarm.domain.enteties.QuestionSetting
import kotlinx.coroutines.flow.Flow

interface AlarmRepository {

    fun generateQuestion(level: Level): Question

    fun getAlarmList(): Flow<List<Alarm>>

    fun addAlarm(alarm: Alarm)

    fun removeAlarm(alarm: Alarm)

    fun editAlarm(alarm: Alarm)

    fun getAlarm(alarmId: Int): Alarm

    fun getQuestionSettings(level: Level): QuestionSetting
}