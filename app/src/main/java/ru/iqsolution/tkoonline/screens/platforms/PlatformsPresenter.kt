package ru.iqsolution.tkoonline.screens.platforms

import android.app.Application
import androidx.collection.SimpleArrayMap
import com.google.gson.Gson
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.models.Container
import ru.iqsolution.tkoonline.models.ContainerType
import ru.iqsolution.tkoonline.models.Platform
import ru.iqsolution.tkoonline.models.PlatformContainers
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
            var minLat = Double.MAX_VALUE
            var maxLat = Double.MIN_VALUE
            var minLon = Double.MAX_VALUE
            var maxLon = Double.MIN_VALUE
            val platforms = arrayListOf<PlatformContainers>()
            val regulars = SimpleArrayMap<Int, Container>()
            val bunkers = SimpleArrayMap<Int, Container>()
            val bunks = SimpleArrayMap<Int, Container>()
            val specials = SimpleArrayMap<Int, Container>()
            val unknown = SimpleArrayMap<Int, Container>()
            responsePlatforms.data.forEach {
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
                        platforms.add(PlatformContainers(it))
                    } else {
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
            platforms.forEach {
                regulars.get(it.kpId)?.let { container ->
                    it.regular.addContainer(container)
                }
                bunkers.get(it.kpId)?.let { container ->
                    it.bunker.addContainer(container)
                }
                bunks.get(it.kpId)?.let { container ->
                    it.bunk.addContainer(container)
                }
                specials.get(it.kpId)?.let { container ->
                    it.special.addContainer(container)
                }
                unknown.get(it.kpId)?.let { container ->
                    it.unknown.addContainer(container)
                }
            }
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