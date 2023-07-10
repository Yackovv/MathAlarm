package com.example.myalarm.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {

    @Query("select * from alarms")
    fun getAlarmList(): Flow<List<AlarmDbModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAlarm(alarmDbModel: AlarmDbModel)

    @Query("delete from alarms where id =:alarmId")
    suspend fun removeAlarm(alarmId: Int)

    @Query("select * from alarms where id =:alarmId limit 1")
    suspend fun getAlarm(alarmId: Int): AlarmDbModel
}