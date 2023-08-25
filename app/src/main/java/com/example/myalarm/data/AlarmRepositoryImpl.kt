package com.example.myalarm.data

import com.example.myalarm.domain.enteties.Alarm
import com.example.myalarm.domain.enteties.Level
import com.example.myalarm.domain.enteties.Question
import com.example.myalarm.domain.enteties.QuestionSetting
import com.example.myalarm.domain.repository.AlarmRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.random.Random

class AlarmRepositoryImpl @Inject constructor(
    private val alarmDao: AlarmDao
) : AlarmRepository {

    private val actionExample1 = listOf("+", "-", "*", "/")
    private val actionExample2 = listOf("+", "-")

    override fun getAlarmList(): Flow<List<Alarm>> {
        return alarmDao.getAlarmList().map {
            AlarmMapper.mapListDbModelToListEntity(it)
        }
    }

    override suspend fun addAlarm(alarm: Alarm): Long {
        return alarmDao.addAlarm(AlarmMapper.mapEntityToDbModel(alarm))
    }

    override suspend fun removeAlarm(alarm: Alarm) {
        alarmDao.removeAlarm(alarm.id)
    }

    override suspend fun editAlarm(alarm: Alarm) {
        alarmDao.addAlarm(AlarmMapper.mapEntityToDbModel(alarm))
    }

    override suspend fun getAlarm(alarmId: Int): Alarm {
        return AlarmMapper.mapDbModelToEntity(alarmDao.getAlarm(alarmId))
    }

    override fun getQuestionSettings(level: Level): QuestionSetting {
        return when (level) {
            Level.EASY -> QuestionSetting(5, 5, 10)
            Level.NORMAL -> QuestionSetting(10, 10, 50)
            Level.HARD -> QuestionSetting(20, 20, 100)
            Level.PRO -> QuestionSetting(30, 30, 200)
        }
    }

    override fun generateQuestion(level: Level): Question {
        val questionSetting = getQuestionSettings(level)

        while (true) {

            val num1 = Random.nextInt(questionSetting.maxValueNum1)
            val num2 = Random.nextInt(1, questionSetting.maxValueNum2)
            val num3 = Random.nextInt(questionSetting.maxValueNum3)
            val action1 = actionExample1.random()
            val action2 = actionExample2.random()

            return if (action1 == "/") {
                if (divTest(num1, num2)) {
                    val numTemp = doAction(num1, num2, action1)
                    val answer = doAction(numTemp, num3, action2)

                    Question(num1, num2, num3, answer, action1, action2)
                } else {
                    continue
                }
            } else {
                val numTemp = doAction(num1, num2, action1)
                val answer = doAction(numTemp, num3, action2)

                Question(num1, num2, num3, answer, action1, action2)
            }
        }
    }

    private fun doAction(num1: Int, num2: Int, action1: String): Int {
        return when (action1) {
            "+" -> num1 + num2
            "-" -> num1 - num2
            "*" -> num1 * num2
            "/" -> num1 / num2
            else -> throw RuntimeException("Unknown character $action1")
        }
    }

    private fun divTest(num1: Int, num2: Int) = num1 % num2 == 0
}