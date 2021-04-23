package ru.iqsolution.tkoonline.screens.phones

import ru.iqsolution.tkoonline.local.entities.Contact
import ru.iqsolution.tkoonline.screens.base.AdapterListener
import ru.iqsolution.tkoonline.screens.base.user.IUserPresenter
import ru.iqsolution.tkoonline.screens.base.user.IUserView

interface PhonesContract {

    interface Presenter : IUserPresenter<View> {

        fun loadContacts()
    }

    interface View : IUserView, AdapterListener<Contact> {

        fun onContacts(list: List<Contact>)
    }
}