package ru.iqsolution.tkoonline.screens.common.wait

import android.app.Activity
import android.os.Bundle
import kotlinx.android.synthetic.main.dialog_wait.*
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.screens.base.AppDialog

class WaitDialog(activity: Activity) : AppDialog(activity) {

    init {
        setCancelable(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_wait)
        dialog_cancel.setOnClickListener {
            activityCallback<WaitListener> {
                cancelWork()
            }
            dismiss()
        }
    }
}