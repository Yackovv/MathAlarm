package com.example.myalarm.domain.enteties

data class Alarm(
    val alarmId: Int,
    val enabled: Boolean,
    val alarmTime: String,
    val level: Level,
    val countQuestion: Int,
    val daysOfWeek: DaysOfWeek
)
