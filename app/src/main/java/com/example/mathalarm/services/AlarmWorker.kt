package com.example.mathalarm.services

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
import com.example.mathalarm.presentation.AlarmReceiver
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
        return Result.success()
    }

    private fun setupAlarm(selectedDayList: List<Int>, hour: Int, minute: Int, alarmId: Int) {

        val alarmManager =
            context.getSystemService(ALARM_SERVICE) as AlarmManager

        val intent = AlarmReceiver.newIntentAlarmReceiver(context, alarmId)
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
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                    temp = false
                    break
                }
            }
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
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