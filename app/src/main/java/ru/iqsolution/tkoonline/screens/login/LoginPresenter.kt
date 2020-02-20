package ru.iqsolution.tkoonline.screens.login

import android.content.Context
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.work.WorkInfo
import com.chibatching.kotpref.bulk
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.*
import org.jetbrains.anko.activityManager
import org.joda.time.DateTime
import org.kodein.di.generic.instance
import retrofit2.HttpException
import ru.iqsolution.tkoonline.BuildConfig
import ru.iqsolution.tkoonline.PATTERN_DATETIME
import ru.iqsolution.tkoonline.extensions.isRunning
import ru.iqsolution.tkoonline.local.FileManager
import ru.iqsolution.tkoonline.local.entities.AccessToken
import ru.iqsolution.tkoonline.models.QrCode
import ru.iqsolution.tkoonline.remote.Server
import ru.iqsolution.tkoonline.screens.base.BasePresenter
import ru.iqsolution.tkoonline.services.TelemetryService
import timber.log.Timber
import java.lang.ref.WeakReference

class LoginPresenter(context: Context) : BasePresenter<LoginContract.View>(context), LoginContract.Presenter {

    private val server: Server by instance()

    private val fileManager: FileManager by instance()

    private val gson: Gson by instance()

    private var observer: LiveData<WorkInfo>? = null

    private var qrCodeJson: String? = null

    private var isExporting = false

    override fun login(context: Context, data: String) {
        // ignoring duplicated values
        if (data == qrCodeJson) {
            return
        }
        // waiting until service will finish job
        if (context.activityManager.isRunning<TelemetryService>()) {
            return
        }
        val qrCode = try {
            gson.fromJson(data, QrCode::class.java)
        } catch (e: JsonSyntaxException) {
            Timber.e(e)
            return
        }
        Timber.d("QrCode: $data")
        qrCodeJson = data
        baseJob.cancelChildren()
        launch {
            try {
                val responseAuth = server.login(
                    qrCode.carId.toString(),
                    qrCode.pass,
                    preferences.lockPassword?.toInt()
                )
                preferences.bulk {
                    accessToken = responseAuth.accessKey
                    expiresWhen = responseAuth.expireTime
                    allowPhotoRefKp = responseAuth.noKpPhoto == 1
                    serverTime = responseAuth.currentTime.toString(PATTERN_DATETIME)
                    elapsedTime = SystemClock.elapsedRealtime()
                    vehicleNumber = qrCode.regNum
                    queName = responseAuth.queName
                    carId = qrCode.carId
                    tokenId = withContext(Dispatchers.IO) {
                        db.tokenDao().insert(AccessToken().apply {
                            token = responseAuth.accessKey
                            queName = responseAuth.queName
                            carId = qrCode.carId
                            expires = DateTime.parse(responseAuth.expireTime, PATTERN_DATETIME)
                        })
                    }
                }
                reference.get()?.onLoggedIn()
            } catch (e: HttpException) {
                if (e.code() == 404) {
                    Timber.e(e)
                    reference.get()?.showError("Сервер не отвечает, проверьте настройки соединения")
                } else {
                    throw e
                }
            }
        }
    }

    override fun reset() {
        launch {
            try {
                // short toast time
                delay(2000)
            } catch (e: CancellationException) {
            }
            qrCodeJson = null
        }
    }

    override fun checkUpdates() {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val response = server.checkVersion()
                if (response.version > BuildConfig.VERSION_CODE) {
                    reference.get()?.onUpdateAvailable()
                }
            } catch (e: Throwable) {
                Timber.e(e)
            }
        }
    }

    override fun export(context: Context) {
        if (isExporting) {
            return
        }
        isExporting = true
        val contextRef = WeakReference(context)
        GlobalScope.launch(Dispatchers.Main) {
            val result = withContext(Dispatchers.IO) {
                db.baseDao().checkpoint(SimpleSQLiteQuery("pragma wal_checkpoint(full)"))
                fileManager.copyDb(contextRef.get())
            }
            reference.get()?.onExported(result)
            isExporting = false
        }
    }

    override fun onChanged(t: WorkInfo?) {
        when (t?.state) {
            WorkInfo.State.SUCCEEDED -> {

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