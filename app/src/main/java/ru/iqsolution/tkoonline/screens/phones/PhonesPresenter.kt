package ru.iqsolution.tkoonline.screens.phones

import android.content.Context
import kotlinx.coroutines.launch
import ru.iqsolution.tkoonline.screens.base.user.UserPresenter

class PhonesPresenter(context: Context) : UserPresenter<PhonesContract.View>(context),
    PhonesContract.Presenter {

    override fun loadContacts() {
        val carId = preferences.carId
        launch {
            val contacts = db.contactDao().getAll(carId)
            reference.get()?.onContacts(contacts)
        }
    }
}