package ru.iqsolution.tkoonline.screens.login

import android.content.Context
import android.os.SystemClock
import androidx.sqlite.db.SimpleSQLiteQuery
import com.chibatching.kotpref.bulk
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.*
import org.joda.time.DateTime
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.PATTERN_DATETIME
import ru.iqsolution.tkoonline.local.FileManager
import ru.iqsolution.tkoonline.local.entities.AccessToken
import ru.iqsolution.tkoonline.models.QrCode
import ru.iqsolution.tkoonline.remote.Server
import ru.iqsolution.tkoonline.screens.base.BasePresenter
import timber.log.Timber
import java.lang.ref.WeakReference

class LoginPresenter : BasePresenter<LoginContract.View>(), LoginContract.Presenter {

    val server: Server by instance()

    val fileManager: FileManager by instance()

    private var qrCodeJson: String? = null

    private var isExporting = false

    override fun export(context: Context) {
        if (isExporting) {
            return
        }
        isExporting = true
        val contextRef = WeakReference(context)
        launch {
            val result = withContext(Dispatchers.IO) {
                db.baseDao().checkpoint(SimpleSQLiteQuery("pragma wal_checkpoint(full)"))
                fileManager.copyDb(contextRef.get())
            }
            reference.get()?.onExported(result)
            isExporting = false
        }
    }

    override fun login(data: String) {
        if (data == qrCodeJson) {
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
}