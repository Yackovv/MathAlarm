package com.example.myalarm.data

import com.example.myalarm.domain.enteties.Alarm

object AlarmMapper {

    fun mapEntityToDbModel(alarm: Alarm) = AlarmDbModel(
        enabled = alarm.enabled,
        alarmTime = alarm.alarmTime,
        level = alarm.level,
        countQuestion = alarm.countQuestion
    )

    fun mapDbModelToEntity(alarmDbModel: AlarmDbModel) = Alarm(
        enabled = alarmDbModel.enabled,
        alarmTime = alarmDbModel.alarmTime,
        level = alarmDbModel.level,
        countQuestion = alarmDbModel.countQuestion
    )

    fun mapListDbModelToListEntity(alarmDbModelList: List<AlarmDbModel>): List<Alarm>{
        return alarmDbModelList.map { mapDbModelToEntity(it) }
    }
}