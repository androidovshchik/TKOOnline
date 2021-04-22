package ru.iqsolution.tkoonline.screens.call

import android.content.Context
import android.telecom.Call
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.iqsolution.tkoonline.screens.base.BasePresenter

class DialPresenter(context: Context) : BasePresenter<DialContract.View>(context),
    DialContract.Presenter {

    override fun readContact(phone: String?) {
        launch {
            val contact = withContext(Dispatchers.IO) {
                db.contactDao().getByPhone(phone)
            }
            reference.get()?.onContactInfo(contact)
        }
    }

    override fun observeCalls() {
        launch {
            CallService.state.collect {
                reference.get()?.onCallState(it)
                if (it == Call.STATE_DISCONNECTED) {
                    delay(1000)
                    reference.get()?.finish()
                }
            }
        }
    }
}