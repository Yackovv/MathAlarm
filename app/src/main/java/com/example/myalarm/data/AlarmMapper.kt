package com.example.myalarm.data

import com.example.myalarm.domain.enteties.Alarm

object AlarmMapper {

    fun mapEntityToDbModel(alarm: Alarm) = AlarmDbModel(
        id = alarm.id,
        enabled = alarm.enabled,
        alarmTime = alarm.alarmTime,
        level = alarm.level,
        countQuestion = alarm.countQuestion,
        ringtoneUriString = alarm.ringtoneUriString,
        vibration = alarm.vibration,
        monday = alarm.monday,
        tuesday = alarm.tuesday,
        wednesday = alarm.wednesday,
        thursday = alarm.thursday,
        friday = alarm.friday,
        saturday = alarm.saturday,
        sunday = alarm.sunday
    )

    fun mapDbModelToEntity(alarmDbModel: AlarmDbModel) = Alarm(
        id = alarmDbModel.id,
        enabled = alarmDbModel.enabled,
        alarmTime = alarmDbModel.alarmTime,
        level = alarmDbModel.level,
        countQuestion = alarmDbModel.countQuestion,
        ringtoneUriString = alarmDbModel.ringtoneUriString,
        vibration = alarmDbModel.vibration,
        monday = alarmDbModel.monday,
        tuesday = alarmDbModel.tuesday,
        wednesday = alarmDbModel.wednesday,
        thursday = alarmDbModel.thursday,
        friday = alarmDbModel.friday,
        saturday = alarmDbModel.saturday,
        sunday = alarmDbModel.sunday
    )

    fun mapListDbModelToListEntity(alarmDbModelList: List<AlarmDbModel>): List<Alarm> {
        return alarmDbModelList.map { mapDbModelToEntity(it) }
    }
}