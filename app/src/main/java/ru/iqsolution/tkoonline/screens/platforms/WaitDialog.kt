package ru.iqsolution.tkoonline.screens.platforms

import android.app.Activity
import android.os.Bundle
import android.view.Window
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.screens.base.BaseDialog

class WaitDialog(activity: Activity) : BaseDialog(activity) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_wait)
    }
}