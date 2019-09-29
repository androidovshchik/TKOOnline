package ru.iqsolution.tkoonline.screens.platform

import android.app.Application
import com.google.gson.Gson
import org.kodein.di.generic.instance
import ru.iqsolution.tkoonline.models.Platform
import ru.iqsolution.tkoonline.screens.base.BasePresenter

class PlatformPresenter(application: Application) : BasePresenter<PlatformContract.View>(application),
    PlatformContract.Presenter {

    val gson: Gson by instance()

    override fun parsePlatform(json: String): Platform {
        return gson.fromJson(json, Platform::class.java)
    }
}