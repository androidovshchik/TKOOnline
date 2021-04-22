package ru.iqsolution.tkoonline.screens.phones

import ru.iqsolution.tkoonline.local.entities.Contact
import ru.iqsolution.tkoonline.screens.base.AdapterListener
import ru.iqsolution.tkoonline.screens.base.IBasePresenter
import ru.iqsolution.tkoonline.screens.base.IBaseView

interface PhonesContract {

    interface Presenter : IBasePresenter<View> {

        fun loadContacts()
    }

    interface View : IBaseView, AdapterListener<Contact> {

        fun onContacts(list: List<Contact>)
    }
}