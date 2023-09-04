package com.example.mathalarm.presentation

import android.app.Application
import androidx.work.Configuration
import com.example.mathalarm.di.DaggerApplicationComponent
import com.example.mathalarm.services.AlarmWorkerFactory
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