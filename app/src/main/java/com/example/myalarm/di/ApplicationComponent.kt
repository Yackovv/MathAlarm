package com.example.myalarm.di

import android.app.Application
import com.example.myalarm.presentation.AlarmApplication
import com.example.myalarm.presentation.activities.AlarmActivity
import com.example.myalarm.presentation.fragments.AlarmListFragment
import com.example.myalarm.presentation.fragments.AlarmSelectLevelFragment
import com.example.myalarm.presentation.fragments.AlarmSettingFragment
import dagger.BindsInstance
import dagger.Component

@ApplicationScope
@Component(modules = [DataModule::class, ViewModelModule::class, WorkerModule::class])
interface ApplicationComponent {

    fun inject(fragment: AlarmListFragment)
    fun inject(fragment: AlarmSelectLevelFragment)
    fun inject(fragment: AlarmSettingFragment)
    fun inject(application: AlarmApplication)
    fun inject(activity: AlarmActivity)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): ApplicationComponent
    }
}