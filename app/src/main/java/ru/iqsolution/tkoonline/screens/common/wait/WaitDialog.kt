package ru.iqsolution.tkoonline.screens.common.wait

import android.app.Activity
import android.os.Bundle
import kotlinx.android.synthetic.main.dialog_wait.*
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.screens.base.BaseDialog

class WaitDialog(activity: Activity) : BaseDialog(activity) {

    init {
        setCancelable(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_wait)
        dialog_cancel.setOnClickListener {
            dismiss()
            makeCallback<WaitListener> {
                cancelWork()
            }
        }
    }
}