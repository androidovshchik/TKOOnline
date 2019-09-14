package ru.iqsolution.tkoonline.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.view.MenuItem
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein

@SuppressLint("Registered")
open class BaseActivity : Activity(), KodeinAware, IBaseView {

    override val kodein by kodein()

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}