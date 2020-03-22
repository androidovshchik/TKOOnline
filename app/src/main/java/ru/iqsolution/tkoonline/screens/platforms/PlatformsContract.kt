package ru.iqsolution.tkoonline.screens.platforms

import android.content.Context
import androidx.lifecycle.Observer
import androidx.work.WorkInfo
import ru.iqsolution.tkoonline.local.entities.CleanEvent
import ru.iqsolution.tkoonline.local.entities.PhotoEvent
import ru.iqsolution.tkoonline.models.PhotoType
import ru.iqsolution.tkoonline.models.PlatformContainers
import ru.iqsolution.tkoonline.screens.base.AdapterListener
import ru.iqsolution.tkoonline.screens.base.IBasePresenter
import ru.iqsolution.tkoonline.screens.base.IBaseView
import ru.iqsolution.tkoonline.screens.common.map.MapRect
import ru.iqsolution.tkoonline.screens.common.wait.WaitListener

interface PlatformsContract {

    interface Presenter : IBasePresenter<View>, Observer<WorkInfo> {

        fun loadPlatformsTypes(refresh: Boolean)

        fun loadPhotoCleanEvents()

        fun logout(send: Boolean, context: Context)
    }

    interface View : IBaseView, WaitListener, AdapterListener<PlatformContainers> {

        fun onReceivedTypes(types: List<PhotoType>)

        fun changeMapBounds(mapRect: MapRect)

        fun onReceivedPlatforms(primary: List<PlatformContainers>, secondary: List<PlatformContainers>)

        fun onPhotoCleanEvents(photoEvents: List<PhotoEvent>, cleanEvents: List<CleanEvent>)

        fun launchSendWorker()

        fun highlightItem(kpId: Int)

        fun onLoggedOut(send: Boolean, success: Boolean)
    }
}