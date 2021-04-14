package ru.iqsolution.tkoonline.screens

import android.app.Activity
import org.kodein.di.*
import ru.iqsolution.tkoonline.screens.common.wait.WaitDialog
import ru.iqsolution.tkoonline.screens.platforms.PlatformsActivity
import ru.iqsolution.tkoonline.screens.platforms.PlatformsAdapter

val screenModule = DI.Module("screen") {

    bind<PlatformsAdapter>() with contexted<PlatformsActivity>().provider {
        PlatformsAdapter(instance()).apply {
            setListener(context)
        }
    }

    bind<WaitDialog>() with contexted<Activity>().provider {
        WaitDialog(context)
    }
}