package com.example.mathalarm.di

import com.example.mathalarm.services.AlarmWorker
import com.example.mathalarm.services.ChildWorkerFactory
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