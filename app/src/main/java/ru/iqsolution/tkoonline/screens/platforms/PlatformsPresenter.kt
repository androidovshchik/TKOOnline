package ru.iqsolution.tkoonline.screens.platforms

import android.app.Application
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.PlatformStatus
import ru.iqsolution.tkoonline.local.entities.PlatformContainersPhotoClean
import ru.iqsolution.tkoonline.screens.base.BasePresenter

class PlatformsPresenter(application: Application) : BasePresenter<PlatformsContract.View>(application),
    PlatformsContract.Presenter {

    val gson: Gson by instance()

    override val isAllowedPhotoKp
        get() = preferences.allowPhotoRefKp

    override fun loadPlatforms() {
        launch {
            var minLat = Double.MAX_VALUE
            var maxLat = Double.MIN_VALUE
            var minLon = Double.MAX_VALUE
            var maxLon = Double.MIN_VALUE
            val primaryList = arrayListOf<PlatformContainersPhotoClean>()
            val secondaryList = arrayListOf<PlatformContainersPhotoClean>()
            withContext(Dispatchers.IO) {
                val allList = appDb.platformDao().getPlatformsByToken(preferences.accessToken.toString())
                allList.forEach {
                    it.platform.apply {
                        if (latitude < minLat) {
                            minLat = latitude
                        } else if (latitude > maxLat) {
                            maxLat = latitude
                        }
                        if (longitude < minLon) {
                            minLon = longitude
                        } else if (longitude > maxLon) {
                            maxLon = longitude
                        }
                        when (status) {
                            PlatformStatus.PENDING, PlatformStatus.NOT_VISITED -> primaryList.add(it)
                            else -> secondaryList.add(it)
                        }
                    }
                }
                val primaryJson = gson.toJson(primaryList)
                val primaryJson = gson.toJson(primaryList)
                viewRef.get()?.onPlatformLists(
                    primaryList, secondaryList, if (allList.isNotEmpty()) Point(
                        (maxLat + minLat) / 2,
                        (maxLon + minLon) / 2
                    ) else null
                )
            }
            if (primaryList.isNotEmpty() && secondaryList.isNotEmpty()) {
                viewRef.get()?.changeMapPosition((maxLat + minLat) / 2, (maxLon + minLon) / 2)
            }
        }
    }
}