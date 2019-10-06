package ru.iqsolution.tkoonline.screens.login

import android.os.SystemClock
import com.chibatching.kotpref.bulk
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.*
import org.joda.time.DateTime
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.PATTERN_DATETIME
import ru.iqsolution.tkoonline.local.entities.AccessToken
import ru.iqsolution.tkoonline.models.QrCode
import ru.iqsolution.tkoonline.remote.Server
import ru.iqsolution.tkoonline.screens.base.BasePresenter
import timber.log.Timber

class LoginPresenter : BasePresenter<LoginContract.View>(), LoginContract.Presenter {

    val server: Server by instance()

    private var qrCodeJson: String? = null

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
        Timber.d("QrCodeJson: $data")
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
            } catch (e: CancellationException) {
            } catch (e: Throwable) {
                try {
                    // short toast time
                    delay(2000)
                } catch (e: CancellationException) {
                }
                qrCodeJson = null
                throw e
            }
        }
    }
}