@file:Suppress("unused")

package ru.iqsolution.tkoonline.extensions

import android.app.Activity
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult

inline fun <reified T : Activity> Activity.startActivitySimply(
    requestCode: Int? = null,
    vararg params: Pair<String, Any?>
) {
    if (requestCode != null) {
        startActivityForResult<T>(requestCode, *params)
    } else {
        startActivity<T>(*params)
    }
    overridePendingTransition(0, 0)
}