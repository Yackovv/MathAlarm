package com.example.myalarm.domain.enteties

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class Level: Parcelable {
    EASY, NORMAL, HARD, PRO;
}