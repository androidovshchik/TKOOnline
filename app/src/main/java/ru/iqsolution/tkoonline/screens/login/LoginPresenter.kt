package ru.iqsolution.tkoonline.screens.login

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.work.WorkInfo
import com.chibatching.kotpref.bulk
import com.google.gson.Gson
import kotlinx.coroutines.*
import org.jetbrains.anko.activityManager
import org.joda.time.DateTime
import org.kodein.di.generic.instance
import retrofit2.awaitResponse
import ru.iqsolution.tkoonline.BuildConfig
import ru.iqsolution.tkoonline.PATTERN_DATETIME_ZONE
import ru.iqsolution.tkoonline.extensions.isRunning
import ru.iqsolution.tkoonline.local.FileManager
import ru.iqsolution.tkoonline.local.entities.AccessToken
import ru.iqsolution.tkoonline.models.QrCode
import ru.iqsolution.tkoonline.remote.Server
import ru.iqsolution.tkoonline.screens.base.BasePresenter
import ru.iqsolution.tkoonline.services.telemetry.TelemetryService
import ru.iqsolution.tkoonline.services.workers.UpdateWorker
import timber.log.Timber
import java.lang.ref.WeakReference

class LoginPresenter(context: Context) : BasePresenter<LoginContract.View>(context), LoginContract.Presenter {

    private val server: Server by instance()

    private val fileManager: FileManager by instance()

    private val gson: Gson by instance(arg = false)

    private val activityManager = context.activityManager

    private var observer: LiveData<WorkInfo>? = null

    private var updateUrl: String? = null

    private var isExportingDb = false

    override fun login(data: String) {
        // waiting until service will finish job
        if (activityManager.isRunning<TelemetryService>()) {
            return
        }
        val qrCode = try {
            gson.fromJson(data, QrCode::class.java)
        } catch (e: Throwable) {
            Timber.e(e)
            return
        }
        Timber.d("QrCode: $data")
        baseJob.cancelChildren()
        val header = preferences.authHeader
        val lockPassword = preferences.lockPassword?.toInt()
        launch {
            if (header != null) {
                try {
                    server.logout(header).awaitResponse()
                } catch (e: Throwable) {
                    reference.get()?.showError("Не удалось сбросить предыдущую авторизацию")
                    throw e
                }
                preferences.accessToken = null
            }
            val responseAuth = server.login(qrCode.carId.toString(), qrCode.pass, lockPassword)
            preferences.bulk {
                accessToken = responseAuth.accessKey
                expiresWhen = responseAuth.expireTime
                allowPhotoRefKp = responseAuth.noKpPhoto == 1
                serverTime = responseAuth.currentTime.toString(PATTERN_DATETIME_ZONE)
                //elapsedTime = SystemClock.elapsedRealtime()
                vehicleNumber = qrCode.regNum
                queName = responseAuth.queName
                carId = qrCode.carId
                tokenId = withContext(Dispatchers.IO) {
                    db.tokenDao().insert(AccessToken().apply {
                        token = responseAuth.accessKey
                        queName = responseAuth.queName
                        carId = qrCode.carId
                        expires = DateTime.parse(responseAuth.expireTime, PATTERN_DATETIME_ZONE)
                    })
                }
                // telemetry
                mileage = 0f
                packageId = 0
            }
            reference.get()?.onLoggedIn()
        }
    }

    override fun checkUpdates() {
        if (updateUrl != null) {
            return
        }
        updateUrl = ""
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val response = server.checkVersion()
                if (response.version.toInt() > BuildConfig.VERSION_CODE) {
                    updateUrl = response.url
                    reference.get()?.onUpdateAvailable()
                }
            } catch (e: Throwable) {
                Timber.e(e)
                updateUrl = null
            }
        }
    }

    override fun installUpdate(context: Context) {
        observer = UpdateWorker.launch(context, updateUrl ?: return).also {
            it.observeForever(this)
        }
    }

    override fun exportDb(context: Context) {
        if (isExportingDb) {
            return
        }
        isExportingDb = true
        val contextRef = WeakReference(context)
        GlobalScope.launch(Dispatchers.Main) {
            val result = withContext(Dispatchers.IO) {
                db.baseDao().checkpoint(SimpleSQLiteQuery("pragma wal_checkpoint(full)"))
                fileManager.copyDb(contextRef.get())
            }
            reference.get()?.onExportedDb(result)
            isExportingDb = false
        }
    }

    override fun onChanged(t: WorkInfo?) {
        when (t?.state) {
            WorkInfo.State.SUCCEEDED -> {
                reference.get()?.onUpdateEnd(true)
            }
            WorkInfo.State.FAILED -> {
                reference.get()?.onUpdateEnd(false)
            }
            WorkInfo.State.CANCELLED -> {
                observer?.removeObserver(this)
            }
            else -> {
            }
        }
    }

    override fun detachView() {
        observer?.removeObserver(this)
        super.detachView()
    }
}