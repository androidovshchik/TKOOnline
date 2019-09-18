package ru.iqsolution.tkoonline.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.MenuItem
import io.github.inflationx.viewpump.ViewPumpContextWrapper

@SuppressLint("Registered")
open class BaseActivity<T : BasePresenter<*>> : Activity(), IBaseView {

    protected lateinit var presenter: T

    private var waitDialog: WaitDialog? = null

    override fun attachBaseContext(context: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(context))
    }

    override fun showLoading() {
        if (waitDialog == null) {
            waitDialog = WaitDialog(this)
        }
        waitDialog?.let {
            if (!it.isShowing) {
                it.show()
            }
        }
    }

    override fun hideLoading() {
        waitDialog?.hide()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        waitDialog?.dismiss()
        presenter.detachView()
        super.onDestroy()
    }
}