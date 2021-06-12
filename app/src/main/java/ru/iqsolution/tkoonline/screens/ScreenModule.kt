package ru.iqsolution.tkoonline.screens

import android.app.Activity
import org.kodein.di.*
import ru.iqsolution.tkoonline.screens.common.wait.WaitDialog
import ru.iqsolution.tkoonline.screens.phones.ContactsAdapter
import ru.iqsolution.tkoonline.screens.phones.PhonesActivity
import ru.iqsolution.tkoonline.screens.platforms.PlatformsActivity
import ru.iqsolution.tkoonline.screens.platforms.PlatformsAdapter

val screenModule = DI.Module("screen") {

    bind<PlatformsAdapter>() with contexted<PlatformsActivity>().provider {
        PlatformsAdapter(instance()).apply {
            setListener(context)
        }
    }

    bind<ContactsAdapter>() with contexted<PhonesActivity>().provider {
        ContactsAdapter().apply {
            setListener(context)
        }
    }

    bind<WaitDialog>() with contexted<Activity>().provider {
        WaitDialog(context)
    }
}