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
import ru.iqsolution.tkoonline.EXTRA_TELEMETRY_TASK
import ru.iqsolution.tkoonline.local.entities.CleanEvent
import ru.iqsolution.tkoonline.local.entities.PhotoEvent
import ru.iqsolution.tkoonline.models.PlatformContainers
import ru.iqsolution.tkoonline.models.PlatformStatus
import ru.iqsolution.tkoonline.remote.Server
import ru.iqsolution.tkoonline.screens.base.BasePresenter
import ru.iqsolution.tkoonline.services.TelemetryService
import ru.iqsolution.tkoonline.services.workers.SendWorker

class PlatformsPresenter(context: Context) : BasePresenter<PlatformsContract.View>(context),
    PlatformsContract.Presenter, Observer<WorkInfo> {

    private val server: Server by instance()

    private var observer: LiveData<WorkInfo>? = null

    override fun loadPlatformsTypes(refresh: Boolean) {
        baseJob.cancelChildren()
        val header = preferences.authHeader
        launch {
            val responseTypes = server.getPhotoTypes(header)
            reference.get()?.onReceivedTypes(responseTypes.data)
            val responsePlatforms = server.getPlatforms(header, preferences.serverDay)
            var minLat = Double.MAX_VALUE
            var maxLat = Double.MIN_VALUE
            var minLon = Double.MAX_VALUE
            var maxLon = Double.MIN_VALUE
            val primary = arrayListOf<PlatformContainers>()
            val secondary = arrayListOf<PlatformContainers>()
            val allPlatforms = responsePlatforms.data.distinctBy { it.kpId }
                .filter { it.isValid }
            db.platformDao().deleteAll()
            db.platformDao().insert(allPlatforms)
            allPlatforms.forEach { item ->
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
        val day = preferences.serverDay
        launch {
            val photoEvents = arrayListOf<PhotoEvent>()
            val cleanEvents = arrayListOf<CleanEvent>()
            withContext(Dispatchers.IO) {
                photoEvents.addAll(db.photoDao().getDayEvents(day))
                cleanEvents.addAll(db.cleanDao().getDayEvents(day))
            }
            reference.get()?.onPhotoCleanEvents(photoEvents, cleanEvents)
        }
    }

    override fun logout(context: Context) {
        TelemetryService.start(context, EXTRA_TELEMETRY_TASK to false)
        observer = SendWorker.launch(context, true).also {
            it?.observeForever(this)
        }
    }

    override fun cancelExit(context: Context) {
        SendWorker.cancel(context)
        TelemetryService.start(context, EXTRA_TELEMETRY_TASK to true)
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