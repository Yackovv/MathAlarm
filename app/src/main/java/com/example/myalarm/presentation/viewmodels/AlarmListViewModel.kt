package com.example.myalarm.presentation.viewmodels

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.example.domain.domain.enteties.Alarm
import com.example.domain.domain.usecases.AddAlarmUseCase
import com.example.domain.domain.usecases.EditAlarmUseCase
import com.example.domain.domain.usecases.GetAlarmListUseCase
import com.example.domain.domain.usecases.RemoveAlarmUseCase
import com.example.myalarm.logg
import com.example.myalarm.presentation.AlarmReceiver
import com.example.myalarm.services.AlarmWorker
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class AlarmListViewModel @Inject constructor(
    private val getAlarmListUseCase: GetAlarmListUseCase,
    private val addAlarmUseCase: AddAlarmUseCase,
    private val editAlarmUseCase: EditAlarmUseCase,
    private val removeAlarmUseCase: RemoveAlarmUseCase
) : ViewModel() {

    val alarmList = getAlarmListUseCase.invoke()
    val newAlarmId = MutableSharedFlow<Int>()

    fun removeAlarm(alarm: Alarm) {
        viewModelScope.launch {
            removeAlarmUseCase.invoke(alarm)
        }
    }

    fun addAlarm() {
        viewModelScope.launch {
            logg("AlarmListViewModel call emit")
            newAlarmId.emit(addAlarmUseCase.invoke(Alarm()).toInt())
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
        logg("отмена pendingIntent")
        logg("alarm ID на AlarmListFragment: $alarmId")
        val cancelIntent = AlarmReceiver.newIntentAlarmReceiver(context, alarmId)
        val cancelPendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId,
            cancelIntent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        cancelPendingIntent?.let {
            val alarmManager =
                context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(it)
        }
    }
}