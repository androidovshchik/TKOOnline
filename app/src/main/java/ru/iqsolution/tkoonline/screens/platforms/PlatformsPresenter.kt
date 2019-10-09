package ru.iqsolution.tkoonline.screens.platforms

import android.content.Context
import androidx.collection.SimpleArrayMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.work.WorkInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.local.entities.CleanEvent
import ru.iqsolution.tkoonline.local.entities.PhotoEvent
import ru.iqsolution.tkoonline.models.Container
import ru.iqsolution.tkoonline.models.ContainerType
import ru.iqsolution.tkoonline.models.PlatformContainers
import ru.iqsolution.tkoonline.models.PlatformStatus
import ru.iqsolution.tkoonline.remote.Server
import ru.iqsolution.tkoonline.screens.base.BasePresenter
import ru.iqsolution.tkoonline.services.workers.SendWorker

class PlatformsPresenter : BasePresenter<PlatformsContract.View>(), PlatformsContract.Presenter, Observer<WorkInfo> {

    val server: Server by instance()

    private var observer: LiveData<WorkInfo>? = null

    override fun loadPlatformsTypes(refresh: Boolean) {
        baseJob.cancelChildren()
        launch {
            val responseTypes = server.getPhotoTypes(preferences.authHeader)
            reference.get()?.onReceivedTypes(responseTypes.data)
            val responsePlatforms = server.getPlatforms(preferences.authHeader, preferences.serverDay)
            var minLat = Double.MAX_VALUE
            var maxLat = Double.MIN_VALUE
            var minLon = Double.MAX_VALUE
            var maxLon = Double.MIN_VALUE
            val regulars = SimpleArrayMap<Int, Container>()
            val bunkers = SimpleArrayMap<Int, Container>()
            val bulks = SimpleArrayMap<Int, Container>()
            val specials = SimpleArrayMap<Int, Container>()
            val unknown = SimpleArrayMap<Int, Container>()
            val primary = arrayListOf<PlatformContainers>()
            val secondary = arrayListOf<PlatformContainers>()
            responsePlatforms.data.forEach {
                if (!it.isValid) {
                    return@forEach
                }
                if (it.linkedKpId != null) {
                    return@forEach
                }
                if (!refresh) {
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
                }
                when (it.status) {
                    PlatformStatus.PENDING.id, PlatformStatus.NOT_VISITED.id -> primary.add(PlatformContainers(it))
                    else -> secondary.add(PlatformContainers(it))
                }
            }
            responsePlatforms.data.forEach { item ->
                if (!item.isValid) {
                    return@forEach
                }
                if (item.linkedKpId == null) {
                    return@forEach
                }
                if (primary.indexOfFirst { it.kpId == item.linkedKpId } < 0) {
                    return@forEach
                }
                if (secondary.indexOfFirst { it.kpId == item.linkedKpId } < 0) {
                    return@forEach
                }
                when (item.containerType) {
                    ContainerType.REGULAR.id -> regulars.put(item.linkedKpId, item)
                    ContainerType.BUNKER.id -> bunkers.put(item.linkedKpId, item)
                    ContainerType.BULK1.id, ContainerType.BULK2.id -> bulks.put(item.linkedKpId, item)
                    ContainerType.SPECIAL1.id, ContainerType.SPECIAL2.id -> specials.put(item.linkedKpId, item)
                    else -> unknown.put(item.linkedKpId, item)
                }
            }
            primary.forEach {
                it.apply {
                    setFromEqual(regulars.get(it.kpId))
                    setFromEqual(bunkers.get(it.kpId))
                    setFromEqual(bulks.get(it.kpId))
                    setFromEqual(specials.get(it.kpId))
                    setFromEqual(unknown.get(it.kpId))
                }
            }
            secondary.forEach {
                it.apply {
                    setFromEqual(regulars.get(it.kpId))
                    setFromEqual(bunkers.get(it.kpId))
                    setFromEqual(bulks.get(it.kpId))
                    setFromEqual(specials.get(it.kpId))
                    setFromEqual(unknown.get(it.kpId))
                }
            }
            reference.get()?.apply {
                if (!refresh && responsePlatforms.data.isNotEmpty()) {
                    changeMapPosition((maxLat + minLat) / 2, (maxLon + minLon) / 2)
                }
                onReceivedPlatforms(primary, secondary)
            }
        }
    }

    override fun loadPhotoCleanEvents() {
        launch {
            val photoEvents = arrayListOf<PhotoEvent>()
            val cleanEvents = arrayListOf<CleanEvent>()
            withContext(Dispatchers.IO) {
                photoEvents.addAll(db.photoDao().getDayEvents(preferences.serverDay))
                cleanEvents.addAll(db.cleanDao().getDayEvents(preferences.serverDay))
            }
            reference.get()?.onPhotoCleanEvents(photoEvents, cleanEvents)
        }
    }

    override fun logout(context: Context) {
        observer = SendWorker.launch(context).also {
            it?.observeForever(this)
        }
    }

    override fun onChanged(t: WorkInfo?) {
        when (t?.state) {
            WorkInfo.State.SUCCEEDED -> {
                reference.get()?.onLoggedOut()
            }
            WorkInfo.State.CANCELLED -> {
                observer?.removeObserver(this)
            }
            else -> {
            }
        }
    }

    override fun detachView() {
        observer?.removeObserver(this)
        super.detachView()
    }
}