package ru.iqsolution.tkoonline.screens.login

import android.app.Application
import com.chibatching.kotpref.bulk
import com.google.gson.Gson
import kotlinx.coroutines.launch
import org.joda.time.DateTimeZone
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.data.local.Preferences
import ru.iqsolution.tkoonline.data.models.QrCode
import ru.iqsolution.tkoonline.data.remote.ServerApi
import ru.iqsolution.tkoonline.screens.BasePresenter

class LoginPresenter(application: Application) : BasePresenter<LoginContract.ContractView>(application),
    LoginContract.ContractPresenter {

    val serverApi: ServerApi by instance()

    val gson: Gson by instance()

    val preferences: Preferences by instance()

    override fun login(data: String) {
        launch {
            val qrCode = gson.fromJson(data, QrCode::class.java)
            val responseAuth = serverApi.login(qrCode.regNum, qrCode.pass, qrCode.carId)
            preferences.bulk {
                accessToken = responseAuth.accessKey
                expiresToken = responseAuth.expire.toDateTime(DateTimeZone.UTC).millis
                allowPhotoRefKp = responseAuth.noKpPhoto == 1
            }
            viewRef.get()?.onAuthorized()
        }
    }
}