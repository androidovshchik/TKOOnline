package ru.iqsolution.tkoonline.screens

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.Window
import ru.iqsolution.tkoonline.R

class WaitDialog(activity: Activity) : Dialog(activity) {

    init {
        setCancelable(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_wait)
    }
}