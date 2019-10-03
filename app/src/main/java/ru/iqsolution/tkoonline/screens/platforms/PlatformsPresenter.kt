package ru.iqsolution.tkoonline.screens.platforms

import android.app.Application
import androidx.collection.SimpleArrayMap
import kotlinx.coroutines.*
import org.joda.time.DateTimeZone
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.local.Database
import ru.iqsolution.tkoonline.models.*
import ru.iqsolution.tkoonline.remote.Server
import ru.iqsolution.tkoonline.screens.base.BasePresenter
import java.util.*

class PlatformsPresenter(application: Application) : BasePresenter<PlatformsContract.View>(application),
    PlatformsContract.Presenter {

    val server: Server by instance()

    val db: Database by instance()

    override fun loadPlatformsTypes(refresh: Boolean) {
        baseJob.cancelChildren()
        launch {
            try {
                val responseTypes = server.getPhotoTypes(preferences.authHeader)
                viewRef.get()?.onReceivedTypes(responseTypes.data)
                val responsePlatforms = server.getPlatforms(preferences.authHeader, preferences.serverDay)
                var minLat = Double.MAX_VALUE
                var maxLat = Double.MIN_VALUE
                var minLon = Double.MAX_VALUE
                var maxLon = Double.MIN_VALUE
                val regulars = SimpleArrayMap<Int, Container>()
                val bunkers = SimpleArrayMap<Int, Container>()
                val bunks = SimpleArrayMap<Int, Container>()
                val specials = SimpleArrayMap<Int, Container>()
                val unknown = SimpleArrayMap<Int, Container>()
                val primary = arrayListOf<PlatformContainers>()
                val secondary = arrayListOf<PlatformContainers>()
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
                            when (it.status) {
                                PlatformStatus.PENDING, PlatformStatus.NOT_VISITED -> primary.add(PlatformContainers(it))
                                else -> secondary.add(PlatformContainers(it))
                            }
                        } else {
                            when (it.toContainerType()) {
                                ContainerType.REGULAR -> regulars.putLinked(it)
                                ContainerType.BUNKER -> bunkers.putLinked(it)
                                ContainerType.BULK1, ContainerType.BULK2 -> bunks.putLinked(it)
                                ContainerType.SPECIAL1, ContainerType.SPECIAL2 -> specials.putLinked(it)
                                else -> unknown.putLinked(it)
                            }
                        }
                    }
                }
                primary.forEach {
                    it.apply {
                        addContainer(regulars.get(it.kpId))
                        addContainer(bunkers.get(it.kpId))
                        addContainer(bunks.get(it.kpId))
                        addContainer(specials.get(it.kpId))
                        addContainer(unknown.get(it.kpId))
                    }
                }
                viewRef.get()?.apply {
                    if (!refresh && responsePlatforms.data.isNotEmpty()) {
                        changeMapPosition(SimpleLocation((maxLat + minLat) / 2, (maxLon + minLon) / 2))
                    }
                    onReceivedPrimary(primary)
                }
                withContext(Dispatchers.IO) {
                    val errorNames = SimpleArrayMap<Int, String>()
                    val timeZone = DateTimeZone.forTimeZone(TimeZone.getDefault())
                    responseTypes.data.forEach {
                        if (it.isError == 1) {
                            errorNames.put(it.id, it.shortName)
                        }
                    }
                    db.photoDao().getDayEvents(preferences.serverDay).forEach {
                        for (platform in secondary) {
                            if (it.kpId == platform.kpId) {
                                if (platform.timestamp == 0L) {
                                    platform.timestamp = it.whenTime.withZone(timeZone).millis
                                }
                                errorNames.get(it.type)?.run {
                                    platform.addError(this)
                                }
                            }
                        }
                    }
                    db.cleanDao().getDayEvents(preferences.serverDay).forEach {
                        for (platform in secondary) {
                            if (it.kpId == platform.kpId) {
                                val millis = it.whenTime.withZone(timeZone).millis
                                if (platform.timestamp < millis) {
                                    platform.timestamp = millis
                                }
                                break
                            }
                        }
                    }
                    secondary.apply {
                        sortByDescending { it.timestamp }
                        forEach {
                            it.apply {
                                addContainer(regulars.get(it.kpId))
                                addContainer(bunkers.get(it.kpId))
                                addContainer(bunks.get(it.kpId))
                                addContainer(specials.get(it.kpId))
                                addContainer(unknown.get(it.kpId))
                            }
                        }
                    }
                }
                viewRef.get()?.apply {
                    onReceivedSecondary(secondary)
                    updateMapMarkers(gson.toJson(primary), gson.toJson(secondary))
                }
            } catch (e: CancellationException) {
            }
        }
    }

    private fun SimpleArrayMap<Int, Container>.putLinked(item: Platform) {
        get(item.linkedKpId)?.addContainer(item) ?: run {
            put(item.linkedKpId, item)
        }
    }
}