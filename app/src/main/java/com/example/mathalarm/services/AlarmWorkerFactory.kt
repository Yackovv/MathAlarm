package com.example.mathalarm.services

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import javax.inject.Inject
import javax.inject.Provider

class AlarmWorkerFactory @Inject constructor(
    private val workersProviders: @JvmSuppressWildcards Map<Class<out ListenableWorker>, Provider<ChildWorkerFactory>>
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            AlarmWorker::class.qualifiedName -> {
                val childWorkerFactory = workersProviders[AlarmWorker::class.java]?.get()
                childWorkerFactory?.create(appContext, workerParameters)
            }

            else -> throw RuntimeException("Unknown worker $workerClassName")
        }
    }
}