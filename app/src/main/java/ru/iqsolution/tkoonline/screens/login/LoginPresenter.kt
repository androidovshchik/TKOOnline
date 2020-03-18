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
import ru.iqsolution.tkoonline.exceptions.LogoutException
import ru.iqsolution.tkoonline.extensions.PATTERN_DATETIME_ZONE
import ru.iqsolution.tkoonline.extensions.isRunning
import ru.iqsolution.tkoonline.extensions.parseErrors
import ru.iqsolution.tkoonline.local.FileManager
import ru.iqsolution.tkoonline.local.entities.AccessToken
import ru.iqsolution.tkoonline.models.QrCode
import ru.iqsolution.tkoonline.remote.Server
import ru.iqsolution.tkoonline.screens.base.BasePresenter
import ru.iqsolution.tkoonline.telemetry.TelemetryService
import ru.iqsolution.tkoonline.workers.UpdateWorker
import timber.log.Timber

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
        Timber.d("Qr code: $data")
        val lockPassword = preferences.lockPassword?.toInt()
        launch {
            try {
                makeLogout()
            } catch (e: Throwable) {
                reference.get()?.showError("Не удалось сбросить предыдущую авторизацию")
                throw e
            }
            val response = server.login(qrCode.carId.toString(), qrCode.pass, lockPassword)
                .awaitResponse()
            val responseAuth = response.body()
            if (!response.isSuccessful || responseAuth == null) {
                val errors = response.parseErrors(gson)
                val firstError = errors.firstOrNull()
                val codes = errors.map { it.code }
                var message: String? = null
                try {
                    message = when (response.code()) {
                        400, 500 -> firstError?.print()
                        401 -> {
                            when {
                                codes.contains("fail to auth") -> "Неверный логин или пароль"
                                codes.contains("car already taken") -> "Данная ТС уже авторизована в системе - Обратитесь к Вашему администратору"
                                else -> firstError?.print()
                            }
                        }
                        403 -> "Доступ запрещен, обратитесь к администратору"
                        404 -> "Сервер не отвечает, проверьте настройки соединения"
                        else -> firstError?.print(true)
                    }
                } finally {
                    if (message != null) {
                        reference.get()?.showError("Не удалось сбросить предыдущую авторизацию")
                    }
                }
                return@launch
            }
            try {
                val now = DateTime.now()
                require(
                    DateTime.parse(responseAuth.expireTime, PATTERN_DATETIME_ZONE)
                        .withZone(now.zone).millis >= now.millis
                )
            } catch (e: Throwable) {
                // todo
                reference.get()?.authHeader = responseAuth.accessKey
                reference.get()?.showError("Невалидное системное время")
                throw e
            }
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

    override fun logout() {
        launch {
            try {
                makeLogout()
            } catch (e: Throwable) {
            }
        }
    }

    private suspend fun makeLogout() {
        val header = reference.get()?.authHeader
        if (header != null) {
            val response = server.logout(header).awaitResponse()
            if (!response.isSuccessful) {
                Timber.e("Login response code: ${response.code()}")
                throw LogoutException()
            }
            reference.get()?.authHeader = null
        }
    }

    override fun checkUpdates() {
        baseJob.cancelChildren()
        launch {
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

    override fun exportDb() {
        if (isExportingDb) {
            return
        }
        isExportingDb = true
        GlobalScope.launch(Dispatchers.Main) {
            val result = withContext(Dispatchers.IO) {
                db.baseDao().checkpoint(SimpleSQLiteQuery("pragma wal_checkpoint(full)"))
                fileManager.copyDb()
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