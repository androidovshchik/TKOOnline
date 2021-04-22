package ru.iqsolution.tkoonline.screens.call

import ru.iqsolution.tkoonline.local.entities.Contact
import ru.iqsolution.tkoonline.screens.base.IBasePresenter
import ru.iqsolution.tkoonline.screens.base.IBaseView

interface DialContract {

    interface Presenter : IBasePresenter<View> {

        fun readContact(phone: String?)

        fun observeCalls()
    }

    interface View : IBaseView {

        fun onContactInfo(contact: Contact?)

        fun onCallState(state: Int)

        fun finish()
    }
}