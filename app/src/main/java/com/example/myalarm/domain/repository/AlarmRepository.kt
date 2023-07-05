package com.example.myalarm.domain.repository

import com.example.myalarm.domain.enteties.Alarm
import com.example.myalarm.domain.enteties.Level
import com.example.myalarm.domain.enteties.Question
import com.example.myalarm.domain.enteties.QuestionSetting

interface AlarmRepository {

    fun generateQuestion(level: Level): Question

    fun getAlarmList(): List<Alarm>

    fun addAlarm(alarm: Alarm)

    fun removeAlarm(alarm: Alarm)

    fun editAlarm(alarm: Alarm)

    fun getAlarm(alarmId: Int): Alarm

    fun getQuestionSettings(level: Level): QuestionSetting
}