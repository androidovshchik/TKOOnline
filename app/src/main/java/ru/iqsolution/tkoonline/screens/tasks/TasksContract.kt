package ru.iqsolution.tkoonline.screens.tasks

import android.content.Context
import androidx.lifecycle.Observer
import androidx.work.WorkInfo
import ru.iqsolution.tkoonline.local.entities.CleanEvent
import ru.iqsolution.tkoonline.local.entities.PhotoEvent
import ru.iqsolution.tkoonline.local.entities.PhotoType
import ru.iqsolution.tkoonline.models.PlatformContainers
import ru.iqsolution.tkoonline.screens.base.AdapterListener
import ru.iqsolution.tkoonline.screens.base.user.IUserPresenter
import ru.iqsolution.tkoonline.screens.base.user.IUserView
import ru.iqsolution.tkoonline.screens.common.map.MapRect
import ru.iqsolution.tkoonline.screens.common.wait.WaitListener

interface TasksContract {

    interface Presenter : IUserPresenter<View>, Observer<WorkInfo> {

        fun loadRemoteData(refresh: Boolean)

        fun loadPhotoCleanEvents()

        fun logout(send: Boolean, context: Context)
    }

    interface View : IUserView, WaitListener, AdapterListener<PlatformContainers> {

        fun onPhonesCount(size: Int)

        fun onPhotoTypes(types: List<PhotoType>)

        fun changeMapBounds(mapRect: MapRect)

        fun onReceivedPlatforms(primary: List<PlatformContainers>, secondary: List<PlatformContainers>)

        fun onPhotoCleanEvents(photoEvents: List<PhotoEvent>, cleanEvents: List<CleanEvent>)

        fun launchSendWork()

        fun highlightItem(kpId: Int)

        fun onLoggedOut(send: Boolean, success: Boolean)
    }
}