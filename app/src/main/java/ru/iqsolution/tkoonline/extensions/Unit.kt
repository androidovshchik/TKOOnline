@file:Suppress("unused")

package ru.iqsolution.tkoonline.extensions

import android.os.Build

fun isNougat() = Build.VERSION.SDK_INT == Build.VERSION_CODES.N

fun isNougatMR1() = Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1

fun isOreo() = Build.VERSION.SDK_INT == Build.VERSION_CODES.O

fun isOreoMR1() = Build.VERSION.SDK_INT == Build.VERSION_CODES.O_MR1

fun isPie() = Build.VERSION.SDK_INT == Build.VERSION_CODES.P

fun isNougatPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N

fun isNougatMR1Plus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1

fun isOreoPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

fun isOreoMR1Plus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1

fun isPiePlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P