package ru.iqsolution.tkoonline.screens.login

import android.app.Activity
import android.app.Application
import com.chibatching.kotpref.bulk
import com.google.gson.Gson
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import org.joda.time.DateTimeZone
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.data.local.Preferences
import ru.iqsolution.tkoonline.data.models.QrCode
import ru.iqsolution.tkoonline.data.remote.ServerApi
import ru.iqsolution.tkoonline.screens.BasePresenter
import ru.iqsolution.tkoonline.services.AdminManager

class LoginPresenter(application: Application) : BasePresenter<LoginContract.View>(application),
    LoginContract.Presenter {

    val serverApi: ServerApi by instance()

    val gson: Gson by instance()

    val preferences: Preferences by instance()

    val adminManager: AdminManager by instance()

    private var loginJson: String? = null

    override fun clearAuthorization() {
        preferences.accessToken = null
    }

    override fun setKioskMode(activity: Activity, enable: Boolean?) {
        if (enable != null) {
            adminManager.setKioskMode(activity, enable)
        } else if (preferences.enableLock) {
            adminManager.setKioskMode(activity, true)
        }
    }

    override fun login(data: String) {
        if (data == loginJson) {
            return
        }
        loginJson = data
        baseJob.cancelChildren()
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