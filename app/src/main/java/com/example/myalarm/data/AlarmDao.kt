package com.example.myalarm.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AlarmDao {

    @Query("select * from alarms")
    fun getAlarmList(): List<AlarmDbModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAlarm(alarmDbModel: AlarmDbModel)

    @Query("delete from alarms where id =:alarmId")
    fun removeAlarm(alarmId: Int)

    @Query("select * from alarms where id =:alarmId limit 1")
    fun getAlarm(alarmId: Int): AlarmDbModel
}