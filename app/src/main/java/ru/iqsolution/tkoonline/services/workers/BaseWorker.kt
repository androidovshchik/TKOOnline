package ru.iqsolution.tkoonline.services.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein

abstract class BaseWorker(context: Context, params: WorkerParameters) : Worker(context, params), KodeinAware {

    override val kodein by closestKodein(context)
}