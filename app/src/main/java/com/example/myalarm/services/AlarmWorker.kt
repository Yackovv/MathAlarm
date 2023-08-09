package com.example.myalarm.services

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
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

        val alarmManager =
            context.getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = AlarmReceiver.newIntentAlarmReceiver(context, alarmId)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance()

        for (i in selectedDayList) {
            log("цикл for: $i")

            if(!iDoNotNow(calendar ,i, hours, minutes)){
                continue
            }

            calendar.set(Calendar.DAY_OF_WEEK, i)
            calendar.set(Calendar.HOUR_OF_DAY, hours)
            calendar.set(Calendar.MINUTE, minutes)
            calendar.set(Calendar.SECOND, 0)

            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            log("Будильник установлен на: ${calendar.time}")
            break
        }
    }


    private fun iDoNotNow(calendar: Calendar ,selectedDay: Int, hours: Int, minutes: Int): Boolean {

        log("iDoNotNow")
        log("time: $hours : $minutes")

        val todayDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinutes = calendar.get(Calendar.MINUTE)
        log("today: $todayDayOfWeek, selectedDay: $selectedDay")

        return when {

            todayDayOfWeek == selectedDay -> {
                log("${currentMinutes < minutes}")
                if (currentHour == hours) {
                    log("${currentMinutes < minutes}")
                    currentMinutes < minutes
                } else currentHour <= hours
            }

            todayDayOfWeek > selectedDay -> {
                false
            }

            else -> {
                true
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