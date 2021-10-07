package ru.iqsolution.tkoonline.screens.routes

import android.content.Context
import com.google.gson.Gson
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import org.kodein.di.instance
import ru.iqsolution.tkoonline.extensions.authHeader
import ru.iqsolution.tkoonline.extensions.fromJson
import ru.iqsolution.tkoonline.local.entities.Route
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
        val header = preferences.token.authHeader.orEmpty()
        val today = preferences.serverDay
        launch {
            val items = mutableListOf<Any>()
            arrayOf(
                today,
                today.minusDays(1)
            ).forEach { day ->
                val routes = mutableListOf<Route>()
                try {
                    val response = server.getRoutes(header, day.format(patternDate))
                    routes.addAll(response.data.onEach {
                        it.tokenId = tokenId
                        it.day = day
                    })
                    routes.addAll(gson.fromJson<List<Route>>("""
                            [
        {
            "route_number": "12312.fsd",
            "fio": "test",
            "count": 2,
            "wait_count": 1
        }
    ]
                    """.trimIndent()).onEach {
                        it.tokenId = tokenId
                        it.day = day
                    })
                    db.routeDao().safeInsert(routes)
                } catch (e: UnknownHostException) {
                    routes.addAll(db.routeDao().getByDay(car, day.format(patternDate)))
                }
                items.add(day)
                items.addAll(routes)
            }
            Timber.i(gson.toJson(items))
            reference.get()?.onRoutes(items)
        }
    }
}