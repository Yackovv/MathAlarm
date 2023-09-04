package com.example.mathalarm.di

import android.app.Application
import com.example.mathalarm.presentation.AlarmApplication
import com.example.mathalarm.presentation.activities.AlarmActivity
import com.example.mathalarm.presentation.fragments.AlarmListFragment
import com.example.mathalarm.presentation.fragments.AlarmSelectLevelFragment
import com.example.mathalarm.presentation.fragments.AlarmSettingFragment
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