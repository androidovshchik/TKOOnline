package ru.iqsolution.tkoonline.screens.tasks

import android.content.Intent
import android.os.Bundle
import androidx.collection.SimpleArrayMap
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.LocationSettingsStates
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_tasks.*
import kotlinx.coroutines.CancellationException
import org.kodein.di.instance
import ru.iqsolution.tkoonline.*
import ru.iqsolution.tkoonline.extensions.startActivityNoop
import ru.iqsolution.tkoonline.local.entities.CleanEvent
import ru.iqsolution.tkoonline.local.entities.LocationEvent
import ru.iqsolution.tkoonline.local.entities.PhotoEvent
import ru.iqsolution.tkoonline.local.entities.PhotoType
import ru.iqsolution.tkoonline.models.PlatformContainers
import ru.iqsolution.tkoonline.models.SimpleLocation
import ru.iqsolution.tkoonline.screens.base.AppAlertDialog
import ru.iqsolution.tkoonline.screens.base.alert
import ru.iqsolution.tkoonline.screens.base.user.UserActivity
import ru.iqsolution.tkoonline.screens.common.map.MapRect
import ru.iqsolution.tkoonline.screens.common.wait.WaitDialog
import ru.iqsolution.tkoonline.screens.login.LoginActivity
import ru.iqsolution.tkoonline.screens.phones.PhonesActivity
import ru.iqsolution.tkoonline.screens.platform.PlatformActivity
import ru.iqsolution.tkoonline.telemetry.TelemetryService
import ru.iqsolution.tkoonline.workers.SendWorker
import timber.log.Timber
import java.time.ZoneOffset
import java.time.ZonedDateTime

class TasksActivity : UserActivity<TasksContract.Presenter>(), TasksContract.View {

    override val presenter: TasksPresenter by instance()

    private val gson: Gson by instance(arg = false)

    private val tasksAdapter: TasksAdapter by instance()

    private val waitDialog: WaitDialog by instance()

    private var alertDialog: AppAlertDialog? = null

    private val photoTypes = mutableListOf<PhotoType>()

    private val photoErrors = SimpleArrayMap<Int, String>()

    private val cleanChanges = SimpleArrayMap<Int, Int>()

    private var refreshTime: ZonedDateTime? = null

    private var locationCount = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks)
        platforms_map.apply {
            loadUrl(URL)
            setLocation(preferences.location)
        }
        platforms_refresh.setOnRefreshListener {
            presenter.loadRemoteData(true)
        }
        platforms_list.apply {
            addItemDecoration(DividerItemDecoration(applicationContext, LinearLayoutManager.VERTICAL).apply {
                ContextCompat.getDrawable(applicationContext, R.drawable.divider)?.let {
                    setDrawable(it)
                }
            })
            adapter = tasksAdapter
        }
        platforms_complete.setOnClickListener {
            onBackPressed()
        }
        iv_phone.setOnClickListener {
            startActivityNoop<PhonesActivity>()
        }
        if (preferences.allowCustomTasks) {
            /*with(platforms_photo) {
                isVisible = true
                setOnClickListener {
                    startActivityNoop<OutsideActivity>(
                        REQUEST_OUTSIDE,
                        EXTRA_PHOTO_TYPES to photoTypes
                    )
                }
            }*/
        }
        presenter.loadRemoteData(false)
        Timber.i("App version: ${BuildConfig.VERSION_CODE}")
    }

    override fun onStart() {
        super.onStart()
        setTouchable(true)
    }

    override fun onItemClick(position: Int, item: PlatformContainers) {
        setTouchable(false)
        startActivityNoop<PlatformActivity>(
            REQUEST_PLATFORM,
            EXTRA_PLATFORM to item,
            EXTRA_PHOTO_TYPES to photoTypes
        )
    }

    override fun onPhonesCount(size: Int) {
        iv_phone.isVisible = BuildConfig.DEBUG || size > 0
    }

    override fun onPhotoTypes(types: List<PhotoType>) {
        photoTypes.apply {
            clear()
            addAll(types)
        }
        photoErrors.clear()
        types.forEach {
            if (it.error == 1) {
                photoErrors.put(it.id, it.shortName)
            }
        }
    }

    override fun changeMapBounds(mapRect: MapRect) {
        platforms_map.setBounds(mapRect)
    }

    override fun onReceivedPlatforms(primary: List<PlatformContainers>, secondary: List<PlatformContainers>) {
        refreshTime = ZonedDateTime.now()
        cleanChanges.clear()
        tasksAdapter.apply {
            primaryItems.notifyItems(true, primary)
            items.notifyItems(false, secondary)
            notifyDataSetChanged()
        }
        presenter.loadPhotoCleanEvents()
        if (preferences.showRoute) {
            updateRoute()
        }
    }

    override fun onPhotoCleanEvents(photoEvents: List<PhotoEvent>, cleanEvents: List<CleanEvent>) {
        val location = preferences.location
        tasksAdapter.apply {
            primaryItems.apply {
                notifyItems(true, null, location, photoEvents, cleanEvents)
                if (location != null) {
                    sortBy { it.meters }
                }
            }
            items.apply {
                notifyItems(false, null, location, photoEvents, cleanEvents)
                sortByDescending { it.timestamp }
            }
            notifyDataSetChanged()
            // primary platforms will overlay secondary in such order
            platforms_map.setMarkers(gson.toJson(items), gson.toJson(primaryItems))
        }
        platforms_refresh.isRefreshing = false
    }

    override fun launchSendWork() {
        SendWorker.launch(applicationContext)
    }

    override fun highlightItem(kpId: Int) {
        tasksAdapter.apply {
            (primaryItems + items).forEachIndexed { index, item ->
                item.highlighted = if (item.kpId == kpId) {
                    platforms_list.layoutManager?.scrollToPosition(index)
                    true
                } else {
                    false
                }
            }
            notifyDataSetChanged()
        }
    }

    override fun onRoute(locationEvents: List<LocationEvent>) {
        platforms_map.setRoute(gson.toJson(locationEvents))
    }

    override fun onLocationState(state: LocationSettingsStates?) {
        super.onLocationState(state)
        onLocationAvailability(state?.isGpsUsable == true)
    }

    override fun onLocationAvailability(available: Boolean) {
        platforms_map.changeIcon(available)
    }

    override fun onLocationResult(location: SimpleLocation) {
        platforms_map.setLocation(location)
        locationCount++
        if (locationCount % 2 == 0L) {
            return
        }
        tasksAdapter.apply {
            primaryItems.apply {
                notifyItems(true, null, location)
                sortBy { it.meters }
            }
            items.notifyItems(false, null, location)
            notifyDataSetChanged()
        }
    }

    override fun cancelWork() {
        SendWorker.cancel(applicationContext)
        TelemetryService.start(applicationContext, EXTRA_TELEMETRY_TASK to true)
    }

    private fun logout(send: Boolean) {
        waitDialog.show()
        TelemetryService.start(applicationContext, EXTRA_TELEMETRY_TASK to false)
        presenter.logout(send, applicationContext)
    }

    override fun onLoggedOut(send: Boolean, success: Boolean) {
        waitDialog.dismiss()
        alertDialog = when {
            success -> {
                startActivityNoop<LoginActivity>()
                finish()
                return
            }
            send -> alert("Не все данные отправлены на сервер", "Ошибка отправки") {
                neutralPressed("Выйти") { _, _ ->
                    logout(false)
                }
                cancelButton()
                positiveButton("Повторить") { _, _ ->
                    logout(true)
                }
            }.display()
            else -> alert("Не удалось разлогиниться на сервере", "Ошибка выхода") {
                cancelButton()
                positiveButton("Повторить") { _, _ ->
                    logout(false)
                }
            }.display()
        }
    }

    override fun onUnhandledError(e: Throwable?) {
        if (e !is CancellationException) {
            platforms_refresh.isRefreshing = false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_PLATFORM -> {
                if (resultCode == RESULT_OK) {
                    data?.getIntExtra(EXTRA_PLATFORM, -1)?.let {
                        cleanChanges.put(it, data.getIntExtra(EXTRA_STATUS, -1))
                    }
                    presenter.loadPhotoCleanEvents()
                }
            }
            REQUEST_OUTSIDE -> {
                if (resultCode == RESULT_OK) {
                    launchSendWork()
                }
            }
        }
        if (preferences.showRoute) {
            updateRoute()
        }
    }

    override fun onBackPressed() {
        alertDialog = alert("Требуется отправка данных на сервер", "Выход") {
            neutralPressed("Выйти") { _, _ ->
                logout(false)
            }
            cancelButton()
            positiveButton("Отправить") { _, _ ->
                logout(true)
            }
        }.display()
    }

    override fun onDestroy() {
        alertDialog?.dismiss()
        waitDialog.dismiss()
        platforms_map.release()
        super.onDestroy()
    }

    /**
     * The order of notification (primary) -> (secondary) is important
     */
    private fun MutableList<PlatformContainers>.notifyItems(
        isPrimary: Boolean,
        platforms: List<PlatformContainers>? = null,
        location: SimpleLocation? = null,
        photoEvents: List<PhotoEvent>? = null,
        cleanEvents: List<CleanEvent>? = null
    ) {
        val zone = ZoneOffset.systemDefault()
        if (platforms != null) {
            clear()
            addAll(platforms)
        }
        val iterator = listIterator()
        for (item in iterator) {
            if (location != null) {
                item.setDistanceTo(location)
            }
            if (photoEvents != null) {
                for (event in photoEvents) {
                    if (item.kpId == event.kpId) {
                        if (!isPrimary) {
                            val eventTime = event.whenTime.withZoneSameInstant(zone)
                            val millis = eventTime.toInstant().toEpochMilli()
                            if (item.timestamp < millis) {
                                item.timestamp = millis
                            }
                        }
                        photoErrors.get(event.typeId)?.let {
                            item.putError(it, 0)
                        }
                    }
                }
            }
            if (cleanEvents != null) {
                for (event in cleanEvents) {
                    if (item.kpId == event.kpId) {
                        val eventTime = event.whenTime.withZoneSameInstant(zone)
                        if (!isPrimary) {
                            val millis = eventTime.toInstant().toEpochMilli()
                            if (item.timestamp < millis) {
                                item.timestamp = millis
                            }
                        }
                        if (refreshTime?.withZoneSameInstant(zone)?.isBefore(eventTime) == true) {
                            cleanChanges.get(item.kpId)?.let {
                                item.status = it // 31 or 10
                                if (isPrimary) {
                                    // the primary item should be in secondary items
                                    tasksAdapter.items.add(item)
                                    iterator.remove()
                                }
                            }
                        }
                        break
                    }
                }
            }
        }
    }

    companion object {

        private const val REQUEST_PLATFORM = 300

        private const val REQUEST_OUTSIDE = 310

        private const val URL = "file:///android_asset/platforms.html"
    }
}