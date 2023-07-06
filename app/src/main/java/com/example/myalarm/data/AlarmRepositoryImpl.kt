package com.example.myalarm.data

import com.example.myalarm.domain.enteties.Alarm
import com.example.myalarm.domain.enteties.Level
import com.example.myalarm.domain.enteties.Question
import com.example.myalarm.domain.enteties.QuestionSetting
import com.example.myalarm.domain.repository.AlarmRepository

object AlarmRepositoryImpl: AlarmRepository {

    override fun generateQuestion(level: Level): Question {
        TODO("Not yet implemented")
    }

    override fun getAlarmList(): List<Alarm> {
        TODO("Not yet implemented")
    }

    override fun addAlarm(alarm: Alarm) {
        TODO("Not yet implemented")
    }

    override fun removeAlarm(alarm: Alarm) {
        TODO("Not yet implemented")
    }

    override fun editAlarm(alarm: Alarm) {
        TODO("Not yet implemented")
    }

    override fun getAlarm(alarmId: Int): Alarm {
        TODO("Not yet implemented")
    }

    override fun getQuestionSettings(level: Level): QuestionSetting {
        TODO("Not yet implemented")
    }
}