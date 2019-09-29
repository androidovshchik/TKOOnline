package ru.iqsolution.tkoonline.screens.platform

import ru.iqsolution.tkoonline.models.Platform

interface PlatformContract {

    interface Presenter {

        fun parsePlatform(json: String): Platform
    }

    interface View
}