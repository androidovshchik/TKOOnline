package ru.iqsolution.tkoonline.screens.container

import android.app.Application
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.data.Containers
import ru.iqsolution.tkoonline.data.models.Container
import ru.iqsolution.tkoonline.screens.BasePresenter

class ContainerPresenter(application: Application) : BasePresenter<ContainerContract.View>(application),
    ContainerContract.Presenter {

    val containers: Containers by instance()

    override fun getContainer(id: Int): Container? {
        return containers.getItem(id)
    }
}