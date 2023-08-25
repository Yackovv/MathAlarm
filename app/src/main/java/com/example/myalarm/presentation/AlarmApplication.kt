package com.example.myalarm.presentation

import android.app.Application
import androidx.work.Configuration
import com.example.myalarm.di.DaggerApplicationComponent
import com.example.myalarm.services.AlarmWorkerFactory
import javax.inject.Inject

class AlarmApplication : Application(), Configuration.Provider {

    val component by lazy {
        DaggerApplicationComponent.factory().create(this)
    }

    @Inject
    lateinit var workerFactory: AlarmWorkerFactory

    override fun onCreate() {
        super.onCreate()
        component.inject(this)
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }
}