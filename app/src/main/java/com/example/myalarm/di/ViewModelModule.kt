package com.example.myalarm.di

import androidx.lifecycle.ViewModel
import com.example.myalarm.presentation.viewmodels.AlarmListViewModel
import com.example.myalarm.presentation.viewmodels.AlarmSettingViewModel
import com.example.myalarm.presentation.viewmodels.AlarmViewModel
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.multibindings.IntoMap

@Module
@InstallIn(ActivityComponent::class)
interface ViewModelModule {

    @Binds
    @ViewModelKey(AlarmListViewModel::class)
    @IntoMap
    fun bindAlarmListViewModel(impl: AlarmListViewModel): ViewModel

    @Binds
    @ViewModelKey(AlarmSettingViewModel::class)
    @IntoMap
    fun bindAlarmSettingViewModel(impl: AlarmSettingViewModel): ViewModel

    @Binds
    @ViewModelKey(AlarmViewModel::class)
    @IntoMap
    fun bindAlarmViewModel(impl: AlarmViewModel): ViewModel
}