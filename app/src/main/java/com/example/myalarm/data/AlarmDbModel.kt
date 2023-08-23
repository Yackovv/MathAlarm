package com.example.myalarm.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.myalarm.domain.enteties.Level

@Entity(tableName = "alarms")
data class AlarmDbModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val enabled: Boolean = true,
    val alarmTime: String = "00:00",
    val level: Level = Level.EASY,
    val countQuestion: Int = 1,
    val ringtoneUriString: String = "",
    val vibration: Boolean = true,

    val monday: Boolean = false,
    val tuesday: Boolean = false,
    val wednesday: Boolean = false,
    val thursday: Boolean = false,
    val friday: Boolean = false,
    val saturday: Boolean = false,
    val sunday: Boolean = false
)
