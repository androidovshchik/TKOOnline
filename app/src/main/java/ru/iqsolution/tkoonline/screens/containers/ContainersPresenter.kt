package ru.iqsolution.tkoonline.screens.containers

import android.app.Application
import androidx.collection.SimpleArrayMap
import com.yandex.mapkit.geometry.Point
import kotlinx.coroutines.launch
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.data.models.Container
import ru.iqsolution.tkoonline.data.models.ContainerItem
import ru.iqsolution.tkoonline.data.models.ContainerStatus
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
            val responseContainers = serverApi.getContainers(preferences.authHeader, preferences.serverDay)
            val regulars = SimpleArrayMap<Int, Container>()
            val bunkers = SimpleArrayMap<Int, Container>()
            val without = SimpleArrayMap<Int, Container>()
            val specials = SimpleArrayMap<Int, Container>()
            responseContainers.data.forEach {
                if (it.isValid) {
                    if (it.linkedKpId != null) {
                        when (it.containerType) {
                            ContainerType.REGULAR -> {
                                regulars.putByLinkedId(it)
                            }
                            ContainerType.BUNKER -> {
                                bunkers.putByLinkedId(it)
                            }
                            ContainerType.WITHOUT -> {
                                without.putByLinkedId(it)
                            }
                            ContainerType.SPECIAL -> {
                                specials.putByLinkedId(it)
                            }
                            else -> {
                            }
                        }
                    }
                } else if (it.containerType == ContainerType.UNKNOWN) {
                    Timber.w("Unknown container type for id ${it.kpId}")
                }
            }
            var minLat = Double.MAX_VALUE
            var maxLat = Double.MIN_VALUE
            var minLon = Double.MAX_VALUE
            var maxLon = Double.MIN_VALUE
            val firstItems = arrayListOf<ContainerItem>()
            val otherItems = arrayListOf<ContainerItem>()
            responseContainers.data.forEach {
                if (it.isValid) {
                    if (it.linkedKpId == null) {
                        if (it.latitude < minLat) {
                            minLat = it.latitude
                        } else if (it.latitude > maxLat) {
                            maxLat = it.latitude
                        }
                        if (it.longitude < minLon) {
                            minLon = it.longitude
                        } else if (it.longitude > maxLon) {
                            maxLon = it.longitude
                        }
                        regulars.get(it.kpId)?.let { container ->
                            it.containerRegular.addFrom(container)
                        }
                        bunkers.get(it.kpId)?.let { container ->
                            it.containerBunker.addFrom(container)
                        }
                        without.get(it.kpId)?.let { container ->
                            it.containerWithout.addFrom(container)
                        }
                        specials.get(it.kpId)?.let { container ->
                            it.containerSpecial.addFrom(container)
                        }
                        when (it.status) {
                            ContainerStatus.PENDING, ContainerStatus.NOT_VISITED -> firstItems.add(it)
                            else -> otherItems.add(it)
                        }
                    }
                }
            }
            viewRef.get()?.onReceivedContainers(
                firstItems, otherItems,
                if (responseContainers.data.isNotEmpty()) Point(
                    (maxLat + minLat) / 2,
                    (maxLon + minLon) / 2
                ) else null
            )
            val responseTypes = serverApi.getPhotoTypes(preferences.authHeader)
            viewRef.get()?.onReceivedTypes(responseTypes.data)
        }
    }

    private fun SimpleArrayMap<Int, Container>.putByLinkedId(item: ContainerItem) {
        get(item.linkedKpId)?.addFrom(item) ?: run {
            put(item.linkedKpId, Container(item.containerType).apply {
                addFrom(item)
            })
        }
    }
}