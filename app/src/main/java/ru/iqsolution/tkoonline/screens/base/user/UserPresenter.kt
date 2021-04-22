package ru.iqsolution.tkoonline.screens.base.user

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.iqsolution.tkoonline.local.entities.LocationEvent
import ru.iqsolution.tkoonline.screens.base.BasePresenter

abstract class UserPresenter<V : IUserView>(context: Context) : BasePresenter<V>(context),
    IUserPresenter<V> {

    override fun calculateSend() {
        launch {
            var photoCount: Int
            val allCount = withContext(Dispatchers.IO) {
                photoCount = db.photoDao().getSendCount()
                photoCount + db.cleanDao().getSendCount() + db.locationDao().getSendCount()
            }
            reference.get()?.onCloudUpdate(allCount, photoCount)
        }
    }

    override fun loadRoute() {
        val day = preferences.serverDay
        val carId = preferences.carId
        launch {
            val locationEvents = mutableListOf<LocationEvent>()
            withContext(Dispatchers.IO) {
                locationEvents.addAll(
                    db.locationDao().getDayCarEvents(day, carId).map { it.location })
            }
            reference.get()?.onRoute(locationEvents)
        }
    }
}