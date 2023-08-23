package com.example.myalarm.presentation

import androidx.recyclerview.widget.DiffUtil
import com.example.myalarm.domain.enteties.Alarm

class AlarmDiffCallback: DiffUtil.ItemCallback<Alarm>() {

    override fun areItemsTheSame(oldItem: Alarm, newItem: Alarm): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Alarm, newItem: Alarm): Boolean {
        return oldItem == newItem
    }
}