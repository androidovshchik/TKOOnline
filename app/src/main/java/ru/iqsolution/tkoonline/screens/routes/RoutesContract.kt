package ru.iqsolution.tkoonline.screens.routes

import ru.iqsolution.tkoonline.models.Schedule
import ru.iqsolution.tkoonline.screens.base.user.IUserPresenter
import ru.iqsolution.tkoonline.screens.base.user.IUserView

interface RoutesContract {

    interface Presenter : IUserPresenter<View> {

        fun loadRoutes()
    }

    interface View : IUserView, SchedulesAdapter.Listener {

        fun onRoutes(schedules: List<Schedule>)
    }
}