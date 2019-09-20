package ru.iqsolution.tkoonline.screens.containers

import android.app.Application
import com.yandex.mapkit.geometry.Point
import kotlinx.coroutines.launch
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.BuildConfig
import ru.iqsolution.tkoonline.data.models.ContainerItem
import ru.iqsolution.tkoonline.data.models.ContainerStatus
import ru.iqsolution.tkoonline.data.remote.ServerApi
import ru.iqsolution.tkoonline.screens.BasePresenter

class ContainersPresenter(application: Application) : BasePresenter<ContainersContract.View>(application),
    ContainersContract.Presenter {

    val serverApi: ServerApi by instance()

    override val isAllowedPhotoKp = preferences.allowPhotoRefKp

    override fun receiveData() {
        launch {
            var minLat = 0.0
            var maxLat = 0.0
            var minLon = 0.0
            var maxLon = 0.0
            val containers = arrayListOf<ContainerItem>()
            val responseContainers = serverApi.getContainers(preferences.authHeader, preferences.serverDay)
            responseContainers.data.forEach {
                if (BuildConfig.DEBUG || it.status != ContainerStatus.NO_TASK) {
                    if (it.longitude > minLon) {

                    } else if (it.longitude > minLon) {

                    }
                }
            }
            viewRef.get()?.onReceivedContainers(containers, Point((maxLat + minLat) / 2, (maxLon + minLon) / 2))
            val responseTypes = serverApi.getPhotoTypes(preferences.authHeader)
            viewRef.get()?.onReceivedTypes(responseTypes.data)
        }
    }
}