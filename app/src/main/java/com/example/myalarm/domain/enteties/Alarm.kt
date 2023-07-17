package com.example.myalarm.domain.enteties

data class Alarm(
    val id: Int = UNDEFINED_ID,
    val enabled: Boolean = true,
    val alarmTime: String = "00:00",
    val level: Level = Level.EASY,
    val countQuestion: Int = 1,

    val monday: Boolean = false,
    val tuesday: Boolean = false,
    val wednesday: Boolean = false,
    val thursday: Boolean = false,
    val friday: Boolean = false,
    val saturday: Boolean = false,
    val sunday: Boolean = false
) {
    companion object {
        const val UNDEFINED_ID = 0
    }
}
