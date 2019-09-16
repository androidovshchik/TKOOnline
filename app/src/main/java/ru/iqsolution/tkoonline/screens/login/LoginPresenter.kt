package ru.iqsolution.tkoonline.screens.login

import android.app.Application
import kotlinx.coroutines.launch
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.data.remote.ServerApi
import ru.iqsolution.tkoonline.screens.BasePresenter

class LoginPresenter(application: Application) : BasePresenter<LoginContract.ContractView>(application),
    LoginContract.ContractPresenter {

    val serverApi: ServerApi by instance()

    override fun login(data: String) {
        launch {
            serverApi.login()
        }
    }
}