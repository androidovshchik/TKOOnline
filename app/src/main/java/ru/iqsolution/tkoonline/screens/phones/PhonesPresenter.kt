package ru.iqsolution.tkoonline.screens.phones

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.iqsolution.tkoonline.screens.base.user.UserPresenter

class PhonesPresenter(context: Context) : UserPresenter<PhonesContract.View>(context),
    PhonesContract.Presenter {

    override fun loadContacts() {
        launch {
            val contacts = withContext(Dispatchers.IO) {
                db.contactDao().getAll()
            }
            reference.get()?.onContacts(contacts)
        }
    }
}