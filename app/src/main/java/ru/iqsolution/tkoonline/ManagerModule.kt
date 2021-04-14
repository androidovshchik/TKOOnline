package ru.iqsolution.tkoonline

import org.kodein.di.*
import ru.iqsolution.tkoonline.screens.common.status.StatusListener
import ru.iqsolution.tkoonline.screens.common.status.StatusManager
import ru.iqsolution.tkoonline.telemetry.LocationManager
import ru.iqsolution.tkoonline.telemetry.TelemetryListener

val managerModule = DI.Module("manager") {

    bind<AdminManager>() with singleton {
        AdminManager(instance())
    }

    bind<StatusManager>() with contexted<StatusListener>().provider {
        StatusManager(instance(), context)
    }

    bind<LocationManager>() with contexted<TelemetryListener>().provider {
        LocationManager(instance(), context)
    }
}