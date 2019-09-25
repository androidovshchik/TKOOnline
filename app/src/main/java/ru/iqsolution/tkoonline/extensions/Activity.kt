@file:Suppress("unused")

package ru.iqsolution.tkoonline.extensions

import android.app.Activity
import org.jetbrains.anko.intentFor

inline fun <reified T : Activity> Activity.startActivityNoop(
    requestCode: Int? = null,
    vararg params: Pair<String, Any?>
) {
    if (requestCode != null) {
        startActivityForResult(intentFor<T>(*params), requestCode)
    } else {
        startActivity(intentFor<T>(*params))
    }
    overridePendingTransition(0, 0)
}