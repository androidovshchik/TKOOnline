package ru.iqsolution.tkoonline.screens.login

import android.app.Activity
import android.app.Application
import com.chibatching.kotpref.bulk
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.data.local.Preferences
import ru.iqsolution.tkoonline.data.models.QrCode
import ru.iqsolution.tkoonline.data.remote.ServerApi
import ru.iqsolution.tkoonline.screens.BasePresenter
import ru.iqsolution.tkoonline.services.AdminManager
import timber.log.Timber

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
        if (loginJson == OMIT_FIRST_DATA) {
            loginJson = null
            return
        }
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
            val responseAuth = serverApi.login(qrCode.carId.toString(), qrCode.pass, preferences.lockPassword?.toInt())
            preferences.bulk {
                accessToken = responseAuth.accessKey
                expiresToken = responseAuth.expire
                allowPhotoRefKp = responseAuth.noKpPhoto == 1
                serverTimeDiff = System.currentTimeMillis() - responseAuth.currentTime.millis
            }
            viewRef.get()?.onAuthorized()
        }
    }

    override fun resetQrCode() {
        loginJson = OMIT_FIRST_DATA
    }

    companion object {

        private const val OMIT_FIRST_DATA = "OMIT_FIRST_DATA"
    }
}