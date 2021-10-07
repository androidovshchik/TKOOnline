package ru.iqsolution.tkoonline.screens.tasks

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.work.WorkInfo
import com.google.gson.Gson
import kotlinx.coroutines.*
import org.kodein.di.instance
import ru.iqsolution.tkoonline.extensions.authHeader
import ru.iqsolution.tkoonline.local.entities.*
import ru.iqsolution.tkoonline.models.PlatformContainers
import ru.iqsolution.tkoonline.models.PlatformStatus
import ru.iqsolution.tkoonline.patternDate
import ru.iqsolution.tkoonline.remote.Server
import ru.iqsolution.tkoonline.screens.base.user.UserPresenter
import ru.iqsolution.tkoonline.screens.common.map.MapRect
import ru.iqsolution.tkoonline.workers.SendWorker
import timber.log.Timber
import java.net.UnknownHostException

class TasksPresenter(context: Context) : UserPresenter<TasksContract.View>(context),
    TasksContract.Presenter {

    private val server: Server by instance()

    private val gson: Gson by instance(arg = false)

    private var observer: LiveData<WorkInfo>? = null

    @Volatile
    private var init = false

    override fun loadRemoteData(refresh: Boolean) {
        baseJob.cancelChildren()
        val day = preferences.serverDay
        val header = preferences.token.authHeader!!
        launch {
            init = !refresh
            while (true) {
                val contacts = mutableListOf<Contact>()
                try {
                    contacts.addAll(server.getPhones(header).data)
                    if (init) {
                        Timber.i(gson.toJson(contacts))
                    }
                    withContext(Dispatchers.IO) {
                        db.contactDao().safeInsert(contacts)
                    }
                } catch (e: Throwable) {
                    if (e is UnknownHostException) {
                        contacts.addAll(withContext(Dispatchers.IO) {
                            db.contactDao().getAll()
                        })
                    } else if (e is CancellationException) {
                        throw e
                    }
                }
                reference.get()?.onPhonesCount(contacts.size)
                val photoTypes = mutableListOf<PhotoType>()
                try {
                    photoTypes.addAll(server.getPhotoTypes(header).data)
                    if (init) {
                        Timber.i(gson.toJson(photoTypes))
                    }
                    withContext(Dispatchers.IO) {
                        db.photoTypeDao().safeInsert(photoTypes)
                    }
                } catch (e: Throwable) {
                    if (e is UnknownHostException) {
                        photoTypes.addAll(withContext(Dispatchers.IO) {
                            db.photoTypeDao().getAll()
                        })
                        if (photoTypes.isEmpty()) {
                            delay(e)
                            continue
                        }
                    } else if (e is CancellationException) {
                        throw e
                    } else {
                        delay(e)
                        continue
                    }
                }
                reference.get()?.onPhotoTypes(photoTypes)
                val platforms = mutableListOf<Task>()
                try {
                    platforms.addAll(server.getTasks(header, day.format(patternDate)).data
                        .filter { it.isValid }
                        .distinctBy { it.kpId })
                    if (init) {
                        Timber.i(gson.toJson(platforms))
                    }
                    withContext(Dispatchers.IO) {
                        db.platformDao().safeInsert(platforms)
                    }
                } catch (e: Throwable) {
                    if (e is UnknownHostException) {
                        platforms.addAll(withContext(Dispatchers.IO) {
                            db.platformDao().getAll()
                        })
                        if (platforms.isEmpty()) {
                            delay(e)
                            continue
                        }
                    } else if (e is CancellationException) {
                        throw e
                    } else {
                        delay(e)
                        continue
                    }
                }
                val mapRect = MapRect()
                val primary = mutableListOf<PlatformContainers>()
                val secondary = mutableListOf<PlatformContainers>()
                platforms.forEach { item ->
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
                platforms.forEach { item ->
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
                reference.get()?.apply {
                    if (init) {
                        if (platforms.isNotEmpty()) {
                            changeMapBounds(mapRect)
                        }
                    }
                    onReceivedPlatforms(primary, secondary)
                }
                delay()
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
                photoEvents.addAll(db.photoDao().getDayEvents(day.format(patternDate)))
                cleanEvents.addAll(db.cleanDao().getDayEvents(day.format(patternDate)))
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

    private suspend fun delay(e: Throwable? = null) {
        if (e != null) {
            Timber.e(e)
            reference.get()?.onUnhandledError(e)
        }
        init = false
        delay(5 * 60_000L)
    }

    override fun detachView() {
        observer?.removeObserver(this)
        super.detachView()
    }
}