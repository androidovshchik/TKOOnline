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
import ru.iqsolution.tkoonline.EXTRA_WORKER_MESSAGE
import ru.iqsolution.tkoonline.local.Database
import ru.iqsolution.tkoonline.local.entities.CleanEvent
import ru.iqsolution.tkoonline.local.entities.PhotoEvent
import ru.iqsolution.tkoonline.models.*
import ru.iqsolution.tkoonline.remote.Server
import ru.iqsolution.tkoonline.screens.base.BasePresenter
import ru.iqsolution.tkoonline.services.workers.SendWorker

class PlatformsPresenter : BasePresenter<PlatformsContract.View>(), PlatformsContract.Presenter, Observer<WorkInfo?> {

    val server: Server by instance()

    val db: Database by instance()

    private var observer: LiveData<WorkInfo?>? = null

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
            reference.get()?.apply {
                if (!refresh && responsePlatforms.data.isNotEmpty()) {
                    changeMapPosition(SimpleLocation((maxLat + minLat) / 2, (maxLon + minLon) / 2))
                }
                onReceivedPrimary(primary)
            }
            secondary.apply {
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
            reference.get()?.apply {
                onReceivedSecondary(secondary)
                updateMapMarkers(gson.toJson(primary), gson.toJson(secondary))
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
            reference.get()?.apply {
                onPhotoEvents(photoEvents)
                onCleanEvents(cleanEvents)
            }
        }
    }

    override fun logout(context: Context) {
        reference.get()?.showLoading()
        observer = SendWorker.launch(context, true).also {
            it.observeForever(this)
        }
    }

    override fun onChanged(t: WorkInfo?) {
        when (t?.state) {
            WorkInfo.State.SUCCEEDED -> {
                reference.get()?.onLoggedOut()
            }
            WorkInfo.State.FAILED -> {
                reference.get()?.apply {
                    hideLoading()
                    showError(t.outputData.getString(EXTRA_WORKER_MESSAGE))
                }
            }
            null, WorkInfo.State.BLOCKED -> {
                reference.get()?.apply {
                    hideLoading()
                    showError("Попробуйте позже")
                }
            }
            else -> {
            }
        }
    }

    override fun detachView() {
        observer?.removeObserver(this)
        super.detachView()
    }

    private fun SimpleArrayMap<Int, Container>.putLinked(item: Platform) {
        get(item.linkedKpId)?.addContainer(item) ?: run {
            put(item.linkedKpId, item)
        }
    }
}