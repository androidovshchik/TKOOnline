package ru.iqsolution.tkoonline.screens.platforms

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.work.WorkInfo
import kotlinx.coroutines.*
import org.kodein.di.generic.instance
import retrofit2.await
import ru.iqsolution.tkoonline.local.entities.CleanEvent
import ru.iqsolution.tkoonline.local.entities.PhotoEvent
import ru.iqsolution.tkoonline.local.entities.Platform
import ru.iqsolution.tkoonline.models.PlatformContainers
import ru.iqsolution.tkoonline.models.PlatformStatus
import ru.iqsolution.tkoonline.remote.Server
import ru.iqsolution.tkoonline.screens.base.BasePresenter
import ru.iqsolution.tkoonline.screens.common.map.MapRect
import ru.iqsolution.tkoonline.workers.SendWorker
import timber.log.Timber

class PlatformsPresenter(context: Context) : BasePresenter<PlatformsContract.View>(context),
    PlatformsContract.Presenter {

    private val server: Server by instance()

    private var observer: LiveData<WorkInfo>? = null

    override fun loadPlatformsTypes(refresh: Boolean) {
        baseJob.cancelChildren()
        val day = preferences.serverDay
        val header = preferences.authHeader.orEmpty()
        launch {
            var init = !refresh
            while (true) {
                val responsePlatforms = try {
                    val responseTypes = server.getPhotoTypes(header)
                    reference.get()?.onReceivedTypes(responseTypes.data)
                    server.getPlatforms(header, day).await()
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Throwable) {
                    Timber.e(e)
                    reference.get()?.onUnhandledError(e)
                    init = false
                    delay(REFRESH_TIME)
                    continue
                }
                val mapRect = MapRect()
                val primary = mutableListOf<PlatformContainers>()
                val secondary = mutableListOf<PlatformContainers>()
                val allPlatforms = mutableListOf<Platform>()
                withContext(Dispatchers.IO) {
                    allPlatforms.addAll(responsePlatforms.data.filter { it.isValid }
                        .distinctBy { it.kpId })
                    db.platformDao().safeInsert(allPlatforms)
                    allPlatforms.forEach { item ->
                        if (item.linkedKpId != null) {
                            return@forEach
                        }
                        if (init) {
                            mapRect.update(item)
                        }
                        when (item.status) {
                            PlatformStatus.PENDING.id, PlatformStatus.NOT_VISITED.id ->
                                primary.add(PlatformContainers(item))
                            else -> secondary.add(PlatformContainers(item))
                        }
                    }
                    allPlatforms.forEach { item ->
                        if (item.linkedKpId == null) {
                            return@forEach
                        }
                        val platform =
                            primary.firstOrNull { it.kpId == item.linkedKpId || it.linkedKpId == item.linkedKpId }
                                ?: secondary.firstOrNull { it.kpId == item.linkedKpId || it.linkedKpId == item.linkedKpId }
                        if (platform != null) {
                            platform.linkedIds.add(item.kpId)
                            if (platform.changeStatus(item.status)) {
                                // check if this container is primary before adding to secondary
                                if (primary.remove(platform)) {
                                    secondary.add(platform)
                                }
                            }
                        } else when (item.status) {
                            PlatformStatus.PENDING.id, PlatformStatus.NOT_VISITED.id ->
                                primary.add(PlatformContainers(item))
                            else -> secondary.add(PlatformContainers(item))
                        }
                    }
                    // top 31 -> 10 bottom
                    secondary.sortByDescending { it.status }
                }
                reference.get()?.apply {
                    if (init) {
                        if (allPlatforms.isNotEmpty()) {
                            changeMapBounds(mapRect)
                        }
                    }
                    onReceivedPlatforms(primary, secondary)
                }
                init = false
                delay(REFRESH_TIME)
                reference.get()?.launchSendWork()
            }
        }
    }

    override fun loadPhotoCleanEvents() {
        val day = preferences.serverDay
        launch {
            val photoEvents = mutableListOf<PhotoEvent>()
            val cleanEvents = mutableListOf<CleanEvent>()
            withContext(Dispatchers.IO) {
                photoEvents.addAll(db.photoDao().getDayEvents(day))
                cleanEvents.addAll(db.cleanDao().getDayEvents(day))
            }
            reference.get()?.onPhotoCleanEvents(photoEvents, cleanEvents)
        }
    }

    override fun logout(send: Boolean, context: Context) {
        observer = SendWorker.launch(context, send, true).also {
            it?.observeForever(this)
        }
    }

    override fun onChanged(t: WorkInfo?) {
        val send = t?.outputData?.getBoolean(SendWorker.PARAM_SEND, false) ?: false
        when (t?.state) {
            WorkInfo.State.SUCCEEDED -> {
                reference.get()?.onLoggedOut(send, true)
            }
            WorkInfo.State.FAILED -> {
                reference.get()?.onLoggedOut(send, false)
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

    companion object {

        private const val REFRESH_TIME = 5 * 60_000L
    }
}