package com.example.myalarm.di

import android.app.Application
import com.example.data.data.AlarmRepositoryImpl
import com.example.domain.domain.repository.AlarmRepository
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
        fun provideAlarmDao(application: Application): com.example.data.data.AlarmDao {
            return com.example.data.data.AppDatabase.getInstance(application).alarmDao()
        }
    }
}