package ru.iqsolution.tkoonline.screens.routes

import android.content.Context
import com.google.gson.Gson
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import org.kodein.di.instance
import ru.iqsolution.tkoonline.extensions.authHeader
import ru.iqsolution.tkoonline.models.Schedule
import ru.iqsolution.tkoonline.patternDate
import ru.iqsolution.tkoonline.remote.Server
import ru.iqsolution.tkoonline.screens.base.user.UserPresenter
import timber.log.Timber
import java.net.UnknownHostException

class RoutesPresenter(context: Context) : UserPresenter<RoutesContract.View>(context),
    RoutesContract.Presenter {

    private val server: Server by instance()

    private val gson: Gson by instance(arg = false)

    override fun loadRoutes() {
        baseJob.cancelChildren()
        val car = preferences.carId
        val tokenId = preferences.tokenId
        val header = preferences.accessToken.authHeader!!
        val today = preferences.serverDay
        launch {
            val schedules = arrayOf(
                today,
                today.minusDays(1)
            ).map { day ->
                async {
                    val schedule = Schedule(day)
                    try {
                        val routes = server.getRoutes(header, day.format(patternDate)).data
                        routes.forEach {
                            it.tokenId = tokenId
                            it.day = day
                        }
                        schedule.routes.addAll(routes)
                        db.routeDao().safeInsert(routes)
                    } catch (e: UnknownHostException) {
                        val routes = db.routeDao().getByDay(car, day.format(patternDate))
                        schedule.routes.addAll(routes)
                    }
                    schedule
                }
            }.awaitAll()
            Timber.i(gson.toJson(schedules))
            reference.get()?.onRoutes(schedules)
        }
    }
}