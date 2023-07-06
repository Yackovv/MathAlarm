package com.example.myalarm.data

import com.example.myalarm.domain.enteties.Alarm
import com.example.myalarm.domain.enteties.Level
import com.example.myalarm.domain.enteties.Question
import com.example.myalarm.domain.enteties.QuestionSetting
import com.example.myalarm.domain.repository.AlarmRepository

class AlarmRepositoryImpl(
    private val alarmDao: AlarmDao
) : AlarmRepository {

    private val actionExample1 = listOf("+", "-", "*", "/")
    private val actionExample2 = listOf("+", "-")

    override fun getAlarmList(): List<Alarm> {
        return AlarmMapper.mapListDbModelToListEntity(alarmDao.getAlarmList())
    }

    override fun addAlarm(alarm: Alarm) {
        alarmDao.addAlarm(AlarmMapper.mapEntityToDbModel(alarm))
    }

    override fun removeAlarm(alarm: Alarm) {
        alarmDao.removeAlarm(alarm.Id)
    }

    override fun editAlarm(alarm: Alarm) {
        alarmDao.addAlarm(AlarmMapper.mapEntityToDbModel(alarm))
    }

    override fun getAlarm(alarmId: Int): Alarm {
        return AlarmMapper.mapDbModelToEntity(alarmDao.getAlarm(alarmId))
    }

    override fun getQuestionSettings(level: Level): QuestionSetting {
        return when(level){
            Level.EASY -> QuestionSetting(5, 5, 10)
            Level.PRENORMAL -> QuestionSetting(10, 10, 50)
            Level.NORMAL -> QuestionSetting(20, 20, 100)
            Level.HARD -> QuestionSetting(30, 30, 200)
        }
    }

    override fun generateQuestion(level: Level): Question {
        TODO("Not yet implemented")
    }
}