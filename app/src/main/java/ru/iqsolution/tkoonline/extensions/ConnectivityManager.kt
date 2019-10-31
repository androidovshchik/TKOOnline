@file:Suppress("unused", "DEPRECATION")

package ru.iqsolution.tkoonline.extensions

import android.net.ConnectivityManager

val ConnectivityManager.isConnected: Boolean
    get() = activeNetworkInfo?.isConnected == true