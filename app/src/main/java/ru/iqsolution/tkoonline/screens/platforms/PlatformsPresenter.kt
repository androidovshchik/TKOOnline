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
import org.joda.time.DateTimeZone
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.EXTRA_WORKER_MESSAGE
import ru.iqsolution.tkoonline.local.Database
import ru.iqsolution.tkoonline.models.*
import ru.iqsolution.tkoonline.remote.Server
import ru.iqsolution.tkoonline.screens.base.BasePresenter
import ru.iqsolution.tkoonline.services.workers.SendWorker
import java.util.*

class PlatformsPresenter : BasePresenter<PlatformsContract.View>(), PlatformsContract.Presenter, Observer<WorkInfo> {

    val server: Server by instance()

    val db: Database by instance()

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
            reference.get()?.apply {
                if (!refresh && responsePlatforms.data.isNotEmpty()) {
                    changeMapPosition(SimpleLocation((maxLat + minLat) / 2, (maxLon + minLon) / 2))
                }
                onReceivedPrimary(primary)
            }
            reference.get()?.apply {
                onReceivedSecondary(secondary)
                updateMapMarkers(gson.toJson(primary), gson.toJson(secondary))
            }
        }
    }

    override fun sortPlatforms() {
        launch {
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
            }
        }
    }

    override fun logout(context: Context) {
        reference.get()?.showLoading()
        observer = SendWorker.launch(context).apply {
            observeForever(this@PlatformsPresenter)
        }
    }

    override fun onChanged(t: WorkInfo) {
        when (t.state) {
            WorkInfo.State.SUCCEEDED -> {
                launch {
                    try {
                        withContext(Dispatchers.IO) {
                            server.logout(preferences.authHeader)
                        }
                    } catch (e: Throwable) {
                        reference.get()?.hideLoading()
                        throw e
                    }
                    reference.get()?.onLoggedOut()
                }
            }
            WorkInfo.State.FAILED -> {
                reference.get()?.apply {
                    hideLoading()
                    showError(t.outputData.getString(EXTRA_WORKER_MESSAGE))
                }
            }
            WorkInfo.State.BLOCKED -> {
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