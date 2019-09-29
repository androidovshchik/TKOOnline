package ru.iqsolution.tkoonline.screens.platforms

import android.app.Application
import androidx.collection.SimpleArrayMap
import com.google.gson.Gson
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.models.ContainerType
import ru.iqsolution.tkoonline.models.Platform
import ru.iqsolution.tkoonline.models.PlatformStatus
import ru.iqsolution.tkoonline.remote.Server
import ru.iqsolution.tkoonline.screens.base.BasePresenter

class PlatformsPresenter(application: Application) : BasePresenter<PlatformsContract.View>(application),
    PlatformsContract.Presenter {

    val serverApi: Server by instance()

    val gson: Gson by instance()

    override fun loadPlatforms() {
        baseJob.cancelChildren()
        launch {
            val responseTypes = serverApi.getPhotoTypes(preferences.authHeader)
            viewRef.get()?.onReceivedTypes(responseTypes.data)
            val responsePlatforms = serverApi.getPlatforms(preferences.authHeader, preferences.serverDay)
            val regulars = SimpleArrayMap<Int, Platform>()
            val bunkers = SimpleArrayMap<Int, Platform>()
            val bunks = SimpleArrayMap<Int, Platform>()
            val specials = SimpleArrayMap<Int, Platform>()
            responsePlatforms.data.forEach {
                if (it.isValid) {
                    if (it.linkedKpId != null) {
                        when (ContainerType.fromId(it.containerType)) {
                            ContainerType.REGULAR -> {
                                regulars.putLinked(it)
                            }
                            ContainerType.BUNKER -> {
                                bunkers.putLinked(it)
                            }
                            ContainerType.BULK1, ContainerType.BULK2 -> {
                                bunks.putLinked(it)
                            }
                            ContainerType.SPECIAL1, ContainerType.SPECIAL2 -> {
                                specials.putLinked(it)
                            }
                            else -> {
                            }
                        }
                    }
                }
            }
            var minLat = Double.MAX_VALUE
            var maxLat = Double.MIN_VALUE
            var minLon = Double.MAX_VALUE
            var maxLon = Double.MIN_VALUE
            responsePlatforms.data.forEach {
                if (it.isValid) {
                    if (it.linkedKpId == null) {
                        // measuring center
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
                        // platform_info setup
                        regulars.get(it.kpId)?.let { container ->
                            it.containerRegular.addFrom(container)
                        }
                        bunkers.get(it.kpId)?.let { container ->
                            it.containerBunker.addFrom(container)
                        }
                        bunks.get(it.kpId)?.let { container ->
                            it.containerWithout.addFrom(container)
                        }
                        specials.get(it.kpId)?.let { container ->
                            it.containerSpecial.addFrom(container)
                        }
                        when (it.status) {
                            PlatformStatus.PENDING, PlatformStatus.NOT_VISITED -> containers.addPrimaryItem(it)
                            else -> containers.addSecondaryItem(it)
                        }
                    }
                }
            }
            viewRef.get()?.onReceivedContainers(
                containers.getPrimaryItems(), containers.getSecondaryItems(),
                if (responsePlatforms.data.isNotEmpty()) Point(
                    (maxLat + minLat) / 2,
                    (maxLon + minLon) / 2
                ) else null
            )
        }
    }

    private fun SimpleArrayMap<Int, Platform>.putLinked(item: Platform) {
        get(item.linkedKpId)?.addContainer(item) ?: run {
            put(item.linkedKpId, Platform(item.containerType).apply {
                addContainer(item)
            })
        }
    }
}