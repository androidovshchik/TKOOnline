package ru.iqsolution.tkoonline.screens.platform

import android.app.Activity
import android.os.Bundle
import android.view.Window
import kotlinx.android.synthetic.main.dialog_confirm.*
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.screens.base.BaseDialog

class ConfirmDialog(activity: Activity) : BaseDialog(activity) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_confirm)
        dialog_exit.setOnClickListener {
            makeCallback<ConfirmListener> {
                closeDetails(false)
            }
        }
        dialog_continue.setOnClickListener {
            dismiss()
        }
    }
}