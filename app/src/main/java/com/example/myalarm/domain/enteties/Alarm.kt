package com.example.myalarm.domain.enteties

import android.media.RingtoneManager

data class Alarm(
    val id: Int = UNDEFINED_ID,
    val enabled: Boolean = true,
    val alarmTime: String = "00:00",
    val level: Level = Level.EASY,
    val countQuestion: Int = 1,
    val ringtoneUriString: String = DEFAULT_URI_RINGTONE,
    val vibration: Boolean = true,

    val monday: Boolean = false,
    val tuesday: Boolean = false,
    val wednesday: Boolean = false,
    val thursday: Boolean = false,
    val friday: Boolean = false,
    val saturday: Boolean = false,
    val sunday: Boolean = false
) {
    companion object {
        private const val UNDEFINED_ID = 0
        private val DEFAULT_URI_RINGTONE =
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString()
    }
}
