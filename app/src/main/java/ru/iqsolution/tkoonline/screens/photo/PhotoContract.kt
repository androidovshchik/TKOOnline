package ru.iqsolution.tkoonline.screens.photo

import ru.iqsolution.tkoonline.local.FileManager
import ru.iqsolution.tkoonline.models.PlatformContainers
import ru.iqsolution.tkoonline.screens.base.IBaseView

interface PhotoContract {

    interface Presenter {

        fun platformFromJson(json: String?): PlatformContainers?

        fun moveFile(fileManager: FileManager)
    }

    interface View : IBaseView
}