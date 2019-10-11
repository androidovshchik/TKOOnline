package ru.iqsolution.tkoonline.screens.platforms

import android.content.Context
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
            val primary = arrayListOf<PlatformContainers>()
            val secondary = arrayListOf<PlatformContainers>()
            val allPlatforms = responsePlatforms.data.distinctBy { it.kpId }
            allPlatforms.forEach { item ->
                if (!item.isValid) {
                    return@forEach
                }
                if (item.linkedKpId != null) {
                    return@forEach
                }
                if (!refresh) {
                    if (item.latitude < minLat) {
                        minLat = item.latitude
                    } else if (item.latitude > maxLat) {
                        maxLat = item.latitude
                    }
                    if (item.longitude < minLon) {
                        minLon = item.longitude
                    } else if (item.longitude > maxLon) {
                        maxLon = item.longitude
                    }
                }
                when (item.status) {
                    PlatformStatus.PENDING.id, PlatformStatus.NOT_VISITED.id -> primary.add(PlatformContainers(item))
                    else -> secondary.add(PlatformContainers(item))
                }
            }
            allPlatforms.forEach { item ->
                if (!item.isValid) {
                    return@forEach
                }
                if (item.linkedKpId == null) {
                    return@forEach
                }
                val platform = primary.firstOrNull { it.kpId == item.linkedKpId }
                    ?: secondary.firstOrNull { it.kpId == item.linkedKpId }
                platform?.apply {
                    setFromEqual(item)
                }
            }
            reference.get()?.apply {
                if (!refresh) {
                    if (responsePlatforms.data.isNotEmpty()) {
                        changeMapPosition((maxLat + minLat) / 2, (maxLon + minLon) / 2)
                    }
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
        observer = SendWorker.launch(context, true).also {
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