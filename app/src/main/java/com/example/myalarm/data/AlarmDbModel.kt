package com.example.myalarm.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.myalarm.domain.enteties.Level

@Entity(tableName = "alarms")
data class AlarmDbModel(
    @PrimaryKey(autoGenerate = true)
    val Id: Int = 0,
    val enabled: Boolean,
    val alarmTime: String,
    val level: Level,
    val countQuestion: Int,

    val monday: Boolean = false,
    val tuesday: Boolean = false,
    val wednesday: Boolean = false,
    val thursday: Boolean = false,
    val friday: Boolean = false,
    val saturday: Boolean = false,
    val sunday: Boolean = false
)
