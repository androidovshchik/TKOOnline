package ru.iqsolution.tkoonline.services

import android.app.Application
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import org.kodein.di.KodeinAware
import ru.iqsolution.tkoonline.MainApp

abstract class BaseWorker(app: Application, params: WorkerParameters) : CoroutineWorker(app.applicationContext, params),
    KodeinAware {

    override val kodein = (app as MainApp).kodein
}