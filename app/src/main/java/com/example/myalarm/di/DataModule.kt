package com.example.myalarm.di

import android.app.Application
import com.example.myalarm.data.AlarmDao
import com.example.myalarm.data.AlarmRepositoryImpl
import com.example.myalarm.data.AppDatabase
import com.example.myalarm.domain.repository.AlarmRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
interface DataModule {

    @Binds
    @ApplicationScope
    fun bindAlarmRepository(impl: AlarmRepositoryImpl): AlarmRepository

    companion object {

        @Provides
        @ApplicationScope
        fun provideAlarmDao(application: Application): AlarmDao {
            return AppDatabase.getInstance(application).alarmDao()
        }
    }
}