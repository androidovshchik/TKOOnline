package ru.iqsolution.tkoonline.screens.routes

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_routes.*
import org.kodein.di.instance
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.local.entities.Route
import ru.iqsolution.tkoonline.screens.base.user.UserActivity

class RoutesActivity : UserActivity<RoutesContract.Presenter>(), RoutesContract.View {

    override val presenter: RoutesPresenter by instance()

    private val adapter: SchedulesAdapter by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_routes)
        routes_list.adapter = adapter
        presenter.loadRoutes()
    }

    override fun onRoutes(items: List<Any>) {
        adapter.items.clear()
        adapter.items.addAll(items)
        adapter.notifyDataSetChanged()
    }

    override fun onItemClick(position: Int, item: Any) {
    }

    override fun onRouteClick(position: Int, item: Route) {

    }
}