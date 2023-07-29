package com.example.myalarm.presentation

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.myalarm.R
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    lateinit var fragment: AlarmSettingFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragment = AlarmListFragment()
        supportFragmentManager.beginTransaction().replace(
            R.id.main_container, fragment
        ).setReorderingAllowed(true)
            .commit()

    }
}