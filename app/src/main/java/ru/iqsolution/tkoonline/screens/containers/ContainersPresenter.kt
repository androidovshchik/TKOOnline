package ru.iqsolution.tkoonline.screens.containers

import android.app.Application
import kotlinx.coroutines.launch
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.BuildConfig
import ru.iqsolution.tkoonline.data.models.ContainerStatus
import ru.iqsolution.tkoonline.data.remote.ServerApi
import ru.iqsolution.tkoonline.screens.BasePresenter

class ContainersPresenter(application: Application) : BasePresenter<ContainersContract.View>(application),
    ContainersContract.Presenter {

    val serverApi: ServerApi by instance()

    override val isAllowedPhotoKp = preferences.allowPhotoRefKp

    override fun receiveData() {
        launch {
            try {
                val responseContainers = serverApi.getContainers(preferences.authHeader, preferences.serverDay)
                viewRef.get()?.onReceivedContainers(responseContainers.data.apply {
                    if (!BuildConfig.DEBUG) {
                        filter { it.status != ContainerStatus.NO_TASK }
                    }
                }.sortedBy { it.status })
                val responseTypes = serverApi.getPhotoTypes(preferences.authHeader)
                viewRef.get()?.onReceivedTypes(responseTypes.data)
            } catch (e: Exception) {

            }
        }
    }
}