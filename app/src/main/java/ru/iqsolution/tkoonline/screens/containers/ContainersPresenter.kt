package ru.iqsolution.tkoonline.screens.containers

import android.app.Application
import androidx.collection.SimpleArrayMap
import com.yandex.mapkit.geometry.Point
import kotlinx.coroutines.launch
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.data.models.Container
import ru.iqsolution.tkoonline.data.models.ContainerItem
import ru.iqsolution.tkoonline.data.models.ContainerType
import ru.iqsolution.tkoonline.data.remote.ServerApi
import ru.iqsolution.tkoonline.screens.BasePresenter
import timber.log.Timber

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
            val regulars = SimpleArrayMap<Int, Container>()
            val bunkers = SimpleArrayMap<Int, Container>()
            val without = SimpleArrayMap<Int, Container>()
            val specials = SimpleArrayMap<Int, Container>()
            val containers = arrayListOf<ContainerItem>()
            val responseContainers = serverApi.getContainers(preferences.authHeader, preferences.serverDay)
            responseContainers.data.forEach {
                if (it.isValid) {
                    if (it.longitude > minLon) {

                    } else if (it.longitude > minLon) {

                    }
                    it.linkedKpId?.let { id ->
                        when (it.containerType) {
                            ContainerType.REGULAR -> {
                            }
                            ContainerType.BUNKER -> {
                            }
                            ContainerType.WITHOUT -> {
                            }
                            ContainerType.SPECIAL -> {
                            }
                            else -> Timber.e("")
                        }
                        regulars.put(id, map.get(id, 0))
                        bunkers.put(id, map.get(id, 0))
                        without.put(id, map.get(id, 0))
                        specials.put(id, map.get(id, 0))
                    }
                }
            }
            responseContainers.data.forEach {
                if (it.isValid) {
                    if (it.linkedKpId == null) {
                        it.containerCount += map.get(it.kpId, 0)
                        containers.add(it)
                    }
                }
            }
            viewRef.get()?.onReceivedContainers(
                containers.sortedBy { it },
                Point((maxLat + minLat) / 2, (maxLon + minLon) / 2)
            )
            val responseTypes = serverApi.getPhotoTypes(preferences.authHeader)
            viewRef.get()?.onReceivedTypes(responseTypes.data)
        }
    }
}