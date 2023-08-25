package com.example.myalarm.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import androidx.work.ListenableWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.domain.domain.usecases.GetAlarmUseCase
import com.example.myalarm.logg
import com.example.myalarm.presentation.AlarmReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

class AlarmWorker(
    private val context: Context,
    private val workerParameters: WorkerParameters,
    private val getAlarmUseCase: GetAlarmUseCase
) : Worker(context, workerParameters) {

    class Factory @Inject constructor(
        private val getAlarmUseCase: GetAlarmUseCase
    ) : ChildWorkerFactory {
        override fun create(
            context: Context,
            workerParameters: WorkerParameters
        ): ListenableWorker {
            return AlarmWorker(context, workerParameters, getAlarmUseCase)
        }
    }

    override fun doWork(): Result {

        val alarmId = workerParameters.inputData.getInt(ALARM_ID, 0)

        CoroutineScope(Dispatchers.Unconfined).launch {
            val alarm = getAlarmUseCase.invoke(alarmId)
            setupAlarm(alarm.getActiveDay(), alarm.getHour(), alarm.getMinute(), alarm.id)
        }
        logg("doWork")
        return Result.success()
    }

    private fun setupAlarm(selectedDayList: List<Int>, hour: Int, minute: Int, alarmId: Int) {
        logg("setupAlarm")
        logg(selectedDayList.toString())

        val alarmManager =
            context.getSystemService(ALARM_SERVICE) as AlarmManager

        val intent = AlarmReceiver.newIntentAlarmReceiver(context, alarmId)
        logg("alarm ID in AlarmWorker: $alarmId")
        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            alarmId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )


        val calendar = Calendar.getInstance()
        val currentTime = calendar.timeInMillis

        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)

        var temp = true
        while (temp) {
            for (i in selectedDayList) {
                calendar.set(Calendar.DAY_OF_WEEK, i)
                if (currentTime < calendar.timeInMillis) {
                    check(calendar, currentTime)
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                    logg("Будильник установлен на: ${calendar.time}")
                    temp = false
                    break
                }
            }
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        logg("--- while end ---")
    }

    private fun check(calendar: Calendar, currentTime: Long) {
        logg("$currentTime, ${calendar.timeInMillis}")
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