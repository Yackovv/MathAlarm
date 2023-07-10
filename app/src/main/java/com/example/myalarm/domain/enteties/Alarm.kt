package com.example.myalarm.domain.enteties

data class Alarm(
    val Id: Int = UNDEFINED_ID,
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
) {
    companion object {
        const val UNDEFINED_ID = 0
    }
}
