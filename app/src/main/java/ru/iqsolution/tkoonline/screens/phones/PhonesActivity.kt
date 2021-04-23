package ru.iqsolution.tkoonline.screens.phones

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.telecom.Call
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import kotlinx.android.synthetic.main.activity_phones.*
import kotlinx.android.synthetic.main.include_toolbar.*
import org.kodein.di.instance
import ru.iqsolution.tkoonline.EXTRA_CONTACT
import ru.iqsolution.tkoonline.EXTRA_DIRECTION
import ru.iqsolution.tkoonline.R
import ru.iqsolution.tkoonline.local.entities.Contact
import ru.iqsolution.tkoonline.screens.base.user.UserActivity

class PhonesActivity : UserActivity<PhonesContract.Presenter>(), PhonesContract.View {

    override val presenter: PhonesPresenter by instance()

    private val contactsAdapter: ContactsAdapter by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phones)
        toolbar_back.setOnClickListener {
            onBackPressed()
        }
        toolbar_title.text = "К списку заданий"
        with(phones_list) {
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(applicationContext, VERTICAL).apply {
                ContextCompat.getDrawable(applicationContext, R.drawable.divider)?.let {
                    setDrawable(it)
                }
            })
            adapter = contactsAdapter
        }
        presenter.loadContacts()
    }

    override fun onContacts(list: List<Contact>) {
        contactsAdapter.items.clear()
        contactsAdapter.items.addAll(list)
        contactsAdapter.notifyDataSetChanged()
    }

    override fun onAdapterEvent(position: Int, item: Contact) {
        startActivity(Intent(Intent.ACTION_CALL).apply {
            data = Uri.parse("tel:${item.phone}")
            putExtra(EXTRA_CONTACT, item.name)
            putExtra(EXTRA_DIRECTION, Call.Details.DIRECTION_OUTGOING)
        })
    }
}