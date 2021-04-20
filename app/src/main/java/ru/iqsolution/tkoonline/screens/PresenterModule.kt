package ru.iqsolution.tkoonline.screens

import org.kodein.di.*
import ru.iqsolution.tkoonline.screens.call.DialActivity
import ru.iqsolution.tkoonline.screens.call.DialPresenter
import ru.iqsolution.tkoonline.screens.camera.CameraActivity
import ru.iqsolution.tkoonline.screens.camera.CameraPresenter
import ru.iqsolution.tkoonline.screens.login.LoginActivity
import ru.iqsolution.tkoonline.screens.login.LoginPresenter
import ru.iqsolution.tkoonline.screens.outside.OutsideActivity
import ru.iqsolution.tkoonline.screens.outside.OutsidePresenter
import ru.iqsolution.tkoonline.screens.phones.PhonesActivity
import ru.iqsolution.tkoonline.screens.phones.PhonesPresenter
import ru.iqsolution.tkoonline.screens.photo.PhotoActivity
import ru.iqsolution.tkoonline.screens.photo.PhotoPresenter
import ru.iqsolution.tkoonline.screens.platform.PlatformActivity
import ru.iqsolution.tkoonline.screens.platform.PlatformPresenter
import ru.iqsolution.tkoonline.screens.platforms.PlatformsActivity
import ru.iqsolution.tkoonline.screens.platforms.PlatformsPresenter
import ru.iqsolution.tkoonline.screens.problem.ProblemActivity
import ru.iqsolution.tkoonline.screens.problem.ProblemPresenter

val presenterModule = DI.Module("presenter") {

    bind<LoginPresenter>() with contexted<LoginActivity>().provider {
        LoginPresenter(instance()).apply {
            attachView(context)
        }
    }

    bind<PhonesPresenter>() with contexted<PhonesActivity>().provider {
        PhonesPresenter(instance()).apply {
            attachView(context)
        }
    }

    bind<DialPresenter>() with contexted<DialActivity>().provider {
        DialPresenter(instance()).apply {
            attachView(context)
        }
    }

    bind<OutsidePresenter>() with contexted<OutsideActivity>().provider {
        OutsidePresenter(instance()).apply {
            attachView(context)
        }
    }

    bind<PhotoPresenter>() with contexted<PhotoActivity>().provider {
        PhotoPresenter(instance()).apply {
            attachView(context)
        }
    }

    bind<CameraPresenter>() with contexted<CameraActivity>().provider {
        CameraPresenter(instance()).apply {
            attachView(context)
        }
    }

    bind<PlatformPresenter>() with contexted<PlatformActivity>().provider {
        PlatformPresenter(instance()).apply {
            attachView(context)
        }
    }

    bind<PlatformsPresenter>() with contexted<PlatformsActivity>().provider {
        PlatformsPresenter(instance()).apply {
            attachView(context)
        }
    }

    bind<ProblemPresenter>() with contexted<ProblemActivity>().provider {
        ProblemPresenter(instance()).apply {
            attachView(context)
        }
    }
}