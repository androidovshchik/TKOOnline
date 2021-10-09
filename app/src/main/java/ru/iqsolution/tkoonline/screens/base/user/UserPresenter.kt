package ru.iqsolution.tkoonline.screens.base.user

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.iqsolution.tkoonline.patternDate
import ru.iqsolution.tkoonline.screens.base.BasePresenter

abstract class UserPresenter<V : IUserView>(context: Context) : BasePresenter<V>(context),
    IUserPresenter<V> {

    override fun calculateSend() {
        launch {
            val photoCount = db.photoEventDao().getSendCount()
            val locationCount = withContext(Dispatchers.IO) {
                db.locEventDao().getSendCount()
            }
            val allCount = photoCount + db.taskEventDao().getSendCount() + locationCount
            reference.get()?.onCloudUpdate(allCount, photoCount)
        }
    }

    override fun loadDebugRoute() {
        val carId = preferences.carId
        val day = preferences.serverDay
        launch {
            val events = db.locEventDao().getDayEvents(carId, day.format(patternDate)).map { it.location }
            reference.get()?.onDebugRoute(events)
        }
    }
}