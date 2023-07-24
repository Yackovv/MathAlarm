package com.example.myalarm.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myalarm.R

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