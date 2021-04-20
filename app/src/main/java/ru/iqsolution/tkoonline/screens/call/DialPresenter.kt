package ru.iqsolution.tkoonline.screens.call

import android.content.Context
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.iqsolution.tkoonline.screens.base.BasePresenter

class DialPresenter(context: Context) : BasePresenter<DialContract.View>(context),
    DialContract.Presenter {

    override fun observeCalls() {
        launch {
            CallService.state.collect {
                reference.get()?.onCallState(it)
            }
        }
    }
}