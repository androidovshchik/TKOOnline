package ru.iqsolution.tkoonline

import com.elvishew.xlog.XLog
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber

class LogInterceptor : HttpLoggingInterceptor.Logger {

    override fun log(message: String) {
        Timber.tag("APP${BuildConfig.VERSION_CODE}").d(message)
    }
}

class LogTree(enableLogs: Boolean) : Timber.DebugTree() {

    init {
        saveToFile = BuildConfig.PROD || enableLogs
    }

    override fun createStackElementTag(element: StackTraceElement): String {
        return "${super.createStackElementTag(element)}:${element.methodName}:${element.lineNumber}"
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        super.log(priority, tag, message, t)
        if (saveToFile) {
            XLog.log(priority, "$tag: $message", t)
        }
    }

    companion object {

        @Volatile
        var saveToFile = false
    }
}