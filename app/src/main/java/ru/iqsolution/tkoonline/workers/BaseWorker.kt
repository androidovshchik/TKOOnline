package ru.iqsolution.tkoonline.workers

import android.content.Context
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI

abstract class BaseWorker(context: Context, params: WorkerParameters) : Worker(context, params),
    DIAware {

    override val di by closestDI(context)

    protected fun success(params: Map<String, Any>) = Result.success(
        Data.Builder()
            .putAll(params)
            .build()
    )

    protected fun failure(params: Map<String, Any>) = Result.failure(
        Data.Builder()
            .putAll(params)
            .build()
    )
}