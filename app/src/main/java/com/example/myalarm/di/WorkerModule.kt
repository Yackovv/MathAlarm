package com.example.myalarm.di

import com.example.myalarm.services.AlarmWorker
import com.example.myalarm.services.ChildWorkerFactory
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.multibindings.IntoMap

@Module
@InstallIn(ActivityComponent::class)
interface WorkerModule {

    @Binds
    @IntoMap
    @WorkerKey(AlarmWorker::class)
    fun bindWorker(impl: AlarmWorker.Factory): ChildWorkerFactory
}