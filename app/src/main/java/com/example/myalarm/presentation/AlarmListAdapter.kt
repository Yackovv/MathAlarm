package com.example.myalarm.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import com.example.domain.domain.enteties.Alarm
import com.example.myalarm.R
import com.example.myalarm.databinding.ItemAlarmBinding

class AlarmListAdapter : ListAdapter<Alarm, AlarmViewHolder>(AlarmDiffCallback()) {

    var onClickListener: ((Alarm) -> Unit)? = null
    var onSwitchCompatListener: ((Alarm, Boolean) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {

        val bind = ItemAlarmBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AlarmViewHolder(bind)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val alarm = getItem(position)
        val bind = holder.bind
        bind.tvTimeItem.text = alarm.alarmTime
        bind.alarmSwitch.isChecked = alarm.enabled

        val blueColor = ContextCompat.getColor(bind.root.context, R.color.blue_color)
        val whiteColor = ContextCompat.getColor(bind.root.context, R.color.white_color)

        with(bind) {
            if (alarm.monday) tvMonday.setTextColor(blueColor) else tvMonday.setTextColor(whiteColor)
            if (alarm.tuesday) tvTuesday.setTextColor(blueColor) else tvTuesday.setTextColor(
                whiteColor
            )
            if (alarm.wednesday) tvWednesday.setTextColor(blueColor) else tvWednesday.setTextColor(
                whiteColor
            )
            if (alarm.thursday) tvThursday.setTextColor(blueColor) else tvThursday.setTextColor(
                whiteColor
            )
            if (alarm.friday) tvFriday.setTextColor(blueColor) else tvFriday.setTextColor(whiteColor)
            if (alarm.saturday) tvSaturday.setTextColor(blueColor) else tvSaturday.setTextColor(
                whiteColor
            )
            if (alarm.sunday) tvSunday.setTextColor(blueColor) else tvSunday.setTextColor(whiteColor)
        }
        
        holder.itemView.setOnClickListener {
            onClickListener?.invoke(alarm)
        }

        bind.alarmSwitch.setOnClickListener {
            val isEnabled = holder.bind.alarmSwitch.isChecked
            onSwitchCompatListener?.invoke(alarm, isEnabled)
        }
    }
}