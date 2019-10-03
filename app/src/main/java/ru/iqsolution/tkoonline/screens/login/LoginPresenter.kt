package ru.iqsolution.tkoonline.screens.login

import android.app.Application
import android.os.SystemClock
import com.chibatching.kotpref.bulk
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.*
import org.joda.time.DateTime
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.PATTERN_DATETIME
import ru.iqsolution.tkoonline.local.Database
import ru.iqsolution.tkoonline.local.entities.AccessToken
import ru.iqsolution.tkoonline.models.QrCode
import ru.iqsolution.tkoonline.remote.Server
import ru.iqsolution.tkoonline.screens.base.BasePresenter
import timber.log.Timber

class LoginPresenter(application: Application) : BasePresenter<LoginContract.View>(application),
    LoginContract.Presenter {

    val server: Server by instance()

    val gson: Gson by instance()

    val db: Database by instance()

    private var loginJson: String? = null

    override fun login(data: String) {
        if (data == loginJson) {
            return
        }
        val qrCode = try {
            gson.fromJson(data, QrCode::class.java)
        } catch (e: JsonSyntaxException) {
            Timber.e(e)
            return
        }
        Timber.d("Qr code json: $data")
        loginJson = data
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
                }
                withContext(Dispatchers.IO) {
                    preferences.tokenId = db.tokenDao().insert(AccessToken().apply {
                        token = responseAuth.accessKey
                        queName = responseAuth.queName
                        carId = qrCode.carId
                        expires = DateTime.parse(responseAuth.expireTime, PATTERN_DATETIME)
                    })
                }
                viewRef.get()?.onAuthorized()
            } catch (e: CancellationException) {
            } catch (e: Exception) {
                try {
                    delay(2000)
                } catch (e: CancellationException) {
                }
                loginJson = null
                throw e
            }
        }
    }
}