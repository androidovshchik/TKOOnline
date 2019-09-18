package ru.iqsolution.tkoonline.screens.containers

import android.app.Application
import kotlinx.coroutines.launch
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.data.local.Preferences
import ru.iqsolution.tkoonline.data.remote.ServerApi
import ru.iqsolution.tkoonline.screens.BasePresenter

class ContainersPresenter(application: Application) : BasePresenter<ContainersContract.View>(application),
    ContainersContract.Presenter {

    val serverApi: ServerApi by instance()

    val preferences: Preferences by instance()

    override fun receiveData() {
        launch {
            try {
                val responseTypes = serverApi.getPhotoTypes(preferences.authHeader)
                viewRef.get()?.onReceivedTypes(responseTypes.data)
                //val responseContainers = serverApi.getContainers(preferences.authHeader, )
                //viewRef.get()?.onReceivedContainers(responseContainers.data)
            } catch (e: Exception) {

            }
        }
    }
}