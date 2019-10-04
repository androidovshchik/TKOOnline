package ru.iqsolution.tkoonline.services

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import org.kodein.di.KodeinAware
import ru.iqsolution.tkoonline.MainApp

abstract class BaseWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params), KodeinAware {

    override val kodein = MainApp.instance.kodein
}