package ru.iqsolution.tkoonline.screens.platforms

import android.app.Application
import androidx.collection.SimpleArrayMap
import com.google.gson.Gson
import kotlinx.coroutines.*
import org.joda.time.DateTimeZone
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.local.Database
import ru.iqsolution.tkoonline.local.entities.AccessToken
import ru.iqsolution.tkoonline.models.*
import ru.iqsolution.tkoonline.remote.Server
import ru.iqsolution.tkoonline.screens.base.BasePresenter
import java.util.*

class PlatformsPresenter(application: Application) : BasePresenter<PlatformsContract.View>(application),
    PlatformsContract.Presenter {

    val server: Server by instance()

    val gson: Gson by instance()

    val db: Database by instance()

    override fun saveAccessToken() {
        GlobalScope.launch(Dispatchers.IO) {
            db.tokenDao().insert(AccessToken(preferences))
        }
    }

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
                val errorTypes = SimpleArrayMap<Int, String>()
                val primary = arrayListOf<PlatformContainers>()
                val secondary = arrayListOf<PlatformContainers>()
                responseTypes.data.forEach {
                    if (it.isError == 1) {
                        errorTypes.put(it.type, it.shortName)
                    }
                }
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
                if (responsePlatforms.data.isNotEmpty()) {
                    viewRef.get()?.changeMapPosition(SimpleLocation((maxLat + minLat) / 2, (maxLon + minLon) / 2))
                }
                withContext(Dispatchers.IO) {
                    val timeZone = DateTimeZone.forTimeZone(TimeZone.getDefault())
                    db.photoDao().getEvents().forEach {
                        for (platform in secondary) {
                            if (it.photo.kpId == platform.kpId) {
                                if (platform.timestamp == 0L) {
                                    platform.timestamp = it.photo.whenTime.withZone(timeZone).millis
                                }
                                errorTypes.get(it.photo.typeId)?.run {
                                    platform.addError(this)
                                }
                            }
                        }
                    }
                    db.cleanDao().getEvents().forEach {
                        for (platform in secondary) {
                            if (it.clean.kpId == platform.kpId) {
                                val millis = it.clean.whenTime.withZone(timeZone).millis
                                if (platform.timestamp < millis) {
                                    platform.timestamp = millis
                                }
                                break
                            }
                        }
                    }
                    secondary.sortByDescending { it.timestamp }
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
                secondary.forEach {
                    it.apply {
                        addContainer(regulars.get(it.kpId))
                        addContainer(bunkers.get(it.kpId))
                        addContainer(bunks.get(it.kpId))
                        addContainer(specials.get(it.kpId))
                        addContainer(unknown.get(it.kpId))
                    }
                }
                viewRef.get()?.apply {
                    onReceivedPlatforms(primary, secondary)
                    updateMapMarkers(gson.toJson(primary), gson.toJson(secondary))
                }
            } catch (e: CancellationException) {
            }
        }
    }

    override fun platformToJson(platform: PlatformContainers): String {
        return gson.toJson(platform)
    }

    private fun SimpleArrayMap<Int, Container>.putLinked(item: Platform) {
        get(item.linkedKpId)?.addContainer(item) ?: run {
            put(item.linkedKpId, item)
        }
    }
}