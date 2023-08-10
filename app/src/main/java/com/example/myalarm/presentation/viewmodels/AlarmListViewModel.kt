package com.example.myalarm.presentation.viewmodels

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.example.myalarm.data.AlarmRepositoryImpl
import com.example.myalarm.domain.enteties.Alarm
import com.example.myalarm.domain.usecases.EditAlarmUseCase
import com.example.myalarm.domain.usecases.GetAlarmListUseCase
import com.example.myalarm.domain.usecases.RemoveAlarmUseCase
import com.example.myalarm.presentation.AlarmReceiver
import com.example.myalarm.services.AlarmWorker
import kotlinx.coroutines.launch

class AlarmListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AlarmRepositoryImpl(application)

    private val getAlarmListUseCase = GetAlarmListUseCase(repository)

    //    private val getAlarmUseCase = GetAlarmUseCase(repository)
    private val editAlarmUseCase = EditAlarmUseCase(repository)
    private val removeAlarmUseCase = RemoveAlarmUseCase(repository)

    val alarmList = getAlarmListUseCase.invoke()

    fun removeAlarm(alarm: Alarm) {
        viewModelScope.launch {
            removeAlarmUseCase.invoke(alarm)
        }
    }

    fun changeEnabledState(alarm: Alarm, isEnabled: Boolean) {
        viewModelScope.launch {
            val alarmCopy = alarm.copy(enabled = isEnabled)
            editAlarmUseCase.invoke(alarmCopy)
        }
    }

    fun turnOnAlarm(context: Context, alarmId: Int) {
        val workerManager = WorkManager.getInstance(context.applicationContext)
        workerManager.enqueueUniqueWork(
            AlarmWorker.WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            AlarmWorker.makeRequest(alarmId)
        )
    }

    fun turnOffAlarm(context: Context, alarmId: Int) {
        Log.d("11111", "отмена pendingIntent")
        Log.d("11111", "alarm ID на AlarmListFragment: $alarmId")
        val cancelIntent = AlarmReceiver.newIntentAlarmReceiver(context, alarmId)
        val cancelPendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId,
            cancelIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )

        cancelPendingIntent?.let {
            val alarmManager =
                context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(it)
        }
    }


//    fun getAlarm(alarmId: Int) {
//        viewModelScope.launch {
//            getAlarmUseCase.invoke(alarmId)
//        }
}