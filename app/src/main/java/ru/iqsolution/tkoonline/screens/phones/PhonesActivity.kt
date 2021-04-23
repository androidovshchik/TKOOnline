package ru.iqsolution.tkoonline.screens.phones

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import kotlinx.android.synthetic.main.activity_phones.*
import kotlinx.android.synthetic.main.include_toolbar.*
import org.kodein.di.instance
import ru.iqsolution.tkoonline.BuildConfig
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
        if (BuildConfig.DEBUG) {
            contactsAdapter.items.add(Contact().apply {
                id = Long.MAX_VALUE
                name = "Баланс"
                phone = "*100#"
            })
            contactsAdapter.items.add(Contact().apply {
                id = Long.MAX_VALUE - 1
                name = "Автоинформирование"
                phone = "+78002509890"
            })
        }
        contactsAdapter.items.addAll(list)
        contactsAdapter.notifyDataSetChanged()
    }

    override fun onAdapterEvent(position: Int, item: Contact) {
        startActivity(Intent(Intent.ACTION_CALL).apply {
            data = Uri.parse("tel:${item.phone}")
        })
    }
}