package com.example.myalarm.services

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.util.Log
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.myalarm.data.AlarmRepositoryImpl
import com.example.myalarm.domain.usecases.GetAlarmUseCase
import com.example.myalarm.presentation.AlarmReceiver
import kotlinx.coroutines.runBlocking
import java.util.Calendar

class AlarmWorker(
    private val context: Context,
    private val workerParameters: WorkerParameters
) : Worker(context, workerParameters) {

    private val repository = AlarmRepositoryImpl(applicationContext as Application)
    private val getAlarmUseCase = GetAlarmUseCase(repository)

    override fun doWork(): Result {

        val alarmId = workerParameters.inputData.getInt(ALARM_ID, 0)

        runBlocking {
            val alarm = getAlarmUseCase.invoke(alarmId)
            setupAlarm(alarm.getActiveDay(), alarm.alarmTime, alarm.id)
        }
        log("doWork")
        return Result.success()
    }

    private fun setupAlarm(selectedDayList: List<Int>, alarmTime: String, alarmId: Int) {
        log("setupAlarm")
        val hours = alarmTime.substringBefore(" ").toInt()
        val minutes = alarmTime.substringAfterLast(" ").toInt()
        log(selectedDayList.toString())
        for (i in selectedDayList) {
            log("цикл for: $i")
            val alarmManager =
                context.getSystemService(AlarmManager::class.java) as AlarmManager
            val intent = AlarmReceiver.newIntentAlarmReceiver(context, alarmId)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                alarmId,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
            val calendar = iDoNotNow(i, hours, minutes)
            log("Установленное время: ${calendar.time}")
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }
    }


    private fun iDoNotNow(selectedDay: Int, hours: Int, minutes: Int): Calendar {

        log("iDoNotNow")
        log("time: $hours : $minutes")
        val calendar = Calendar.getInstance()

        val todayDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinutes = calendar.get(Calendar.MINUTE)

        calendar.set(Calendar.HOUR_OF_DAY, hours)
        calendar.set(Calendar.MINUTE, minutes)
        calendar.set(Calendar.SECOND, 0)
        log("time 2 - ${calendar.time}")

        when {
            todayDayOfWeek > selectedDay -> {
                setupDayFutureWeek(selectedDay, calendar)
                sundayCheck(selectedDay, calendar)
                return calendar
            }

            todayDayOfWeek == selectedDay -> {
                if (currentHour == hours) {
                    return if (currentMinutes >= minutes) {
                        setupDayFutureWeek(selectedDay, calendar)
                        sundayCheck(selectedDay, calendar)
                        calendar
                    } else {
                        setupDayCurrentWeek(selectedDay, calendar)
                        calendar
                    }
                } else if (currentHour > hours) {
                    setupDayFutureWeek(selectedDay, calendar)
                    return calendar
                } else {
                    setupDayCurrentWeek(selectedDay, calendar)
                    return calendar
                }
            }

            else -> {
                setupDayCurrentWeek(selectedDay, calendar)
                return calendar
            }
        }
    }

    private fun setupDayCurrentWeek(selectedDay: Int, calendar: Calendar){
        calendar.set(Calendar.DAY_OF_WEEK, selectedDay)
    }

    private fun setupDayFutureWeek(selectedDay: Int, calendar: Calendar){
        calendar.set(Calendar.DAY_OF_WEEK, selectedDay)
        calendar.add(Calendar.DAY_OF_WEEK, 7)
    }

    private fun sundayCheck(selectedDay: Int, calendar: Calendar) {
        if (selectedDay == Calendar.SUNDAY) {
            calendar.add(Calendar.DAY_OF_WEEK, -7)
        }
    }

    private fun log(str: String) {
        Log.d("11111", str)
    }

    companion object {

        private const val ALARM_ID = "alarm_id"
        const val WORK_NAME = "work_name"

        fun makeRequest(alarmId: Int): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<AlarmWorker>()
                .setInputData(workDataOf(ALARM_ID to alarmId))
                .build()
        }
    }
}