package ru.iqsolution.tkoonline.screens.base

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.nfc.NfcAdapter
import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import android.view.WindowManager
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import org.jetbrains.anko.activityManager
import org.jetbrains.anko.toast
import org.kodein.di.DI
import org.kodein.di.android.closestDI
import org.kodein.di.instance
import ru.iqsolution.tkoonline.BuildConfig
import ru.iqsolution.tkoonline.extensions.pendingFor
import ru.iqsolution.tkoonline.extensions.startActivityNoop
import ru.iqsolution.tkoonline.local.Preferences
import ru.iqsolution.tkoonline.screens.LockActivity
import ru.iqsolution.tkoonline.screens.presenterModule
import ru.iqsolution.tkoonline.screens.screenModule

@Suppress("MemberVisibilityCanBePrivate")
abstract class BaseActivity<P : IBasePresenter<*>> : Activity(), IBaseView {

    private val parentDI by closestDI()

    override val di by DI.lazy {

        extend(parentDI)

        import(presenterModule)

        import(screenModule)
    }

    protected abstract val presenter: P

    protected var nfcAdapter: NfcAdapter? = null

    protected val preferences: Preferences by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override val isTouchable: Boolean
        get() = window.attributes.flags and WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE == 0

    override fun setTouchable(enable: Boolean) {
        if (enable) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }

    override fun onResume() {
        super.onResume()
        nfcAdapter?.enableForegroundDispatch(this, pendingFor(javaClass), null, null)
    }

    @Suppress("ConstantConditionIf")
    override fun showError(e: Throwable?) {
        showError(
            if (BuildConfig.PROD) {
                e?.localizedMessage ?: e.toString()
            } else {
                e.toString()
            }
        )
    }

    override fun showError(message: CharSequence?) {
        if (message != null) {
            toast(message)
        }
    }

    override fun attachBaseContext(context: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(context))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onKeyLongPress(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (activityManager.lockTaskModeState != ActivityManager.LOCK_TASK_MODE_NONE) {
                startActivityNoop<LockActivity>()
            }
            return true
        }
        return super.onKeyLongPress(keyCode, event)
    }

    override fun onPause() {
        nfcAdapter?.disableForegroundDispatch(this)
        super.onPause()
        overridePendingTransition(0, 0)
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }
}