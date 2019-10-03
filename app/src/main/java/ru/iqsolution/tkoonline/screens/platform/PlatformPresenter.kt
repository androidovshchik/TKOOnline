package ru.iqsolution.tkoonline.screens.platform

import android.app.Application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.local.Database
import ru.iqsolution.tkoonline.models.PlatformContainers
import ru.iqsolution.tkoonline.screens.base.BasePresenter

class PlatformPresenter(application: Application) : BasePresenter<PlatformContract.View>(application),
    PlatformContract.Presenter {

    val db: Database by instance()

    override fun loadPhotoCleanEvents(platform: PlatformContainers) {
        launch {
            withContext(Dispatchers.IO) {
                db.photoDao().getDayKpIdEvents(preferences.serverDay).forEach {
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
                db.cleanDao().getDayKpIdEvent(preferences.serverDay).forEach {
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
}