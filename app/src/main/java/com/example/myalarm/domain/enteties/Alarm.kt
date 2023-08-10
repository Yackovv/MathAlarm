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

    fun getActiveDay(): List<Int>{
        val activeDayList = mutableListOf<Int>()
        if(monday) activeDayList.add(2)
        if(tuesday) activeDayList.add(3)
        if(wednesday) activeDayList.add(4)
        if(thursday) activeDayList.add(5)
        if(friday) activeDayList.add(6)
        if(saturday) activeDayList.add(7)
        if(sunday) activeDayList.add(1)
        return activeDayList.toList()
    }

    companion object {
        private const val UNDEFINED_ID = 0
        private val DEFAULT_URI_RINGTONE =
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString()
    }
}
