package com.ecandle.raykun.fragments


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.SearchView
import android.view.*
import com.ecandle.raykun.R
import com.ecandle.raykun.activities.ContactActivity
import com.ecandle.raykun.activities.SimpleActivity
import com.ecandle.raykun.adapters.ContactListAdapter
import com.ecandle.raykun.extensions.config
import com.ecandle.raykun.extensions.dbHelper
import com.ecandle.raykun.helpers.CONTACT_ID
import com.ecandle.raykun.interfaces.DeleteContactsListener
import com.ecandle.raykun.models.Contact
import com.ecandle.raykun.models.ListItem
import com.simplemobiletools.commons.extensions.beGoneIf
import com.simplemobiletools.commons.extensions.beVisibleIf
import kotlinx.android.synthetic.main.fragment_tab_contact_list.view.*

/**
 * A simple [Fragment] subclass.
 */
class ContactListFragment : Fragment() , DeleteContactsListener, SearchView.OnQueryTextListener {

    private val LOG_TAG = ContactListFragment::class.java.simpleName
    private var mContacts: List<Contact> = ArrayList()
    lateinit var mView: View
    private var mContactListAdapter: ContactListAdapter? = null
    lateinit var mContact: Contact
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_tab_contact_list, container, false)
        val placeholderText = String.format(getString(R.string.two_string_placeholder), "${getString(R.string.no_upcoming_contacts)}\n", getString(R.string.add_some_contacts))
        mView.contact_empty_list_placeholder.text = placeholderText
        return mView
    }

    override fun onResume() {
        super.onResume()
//        var connectionDetector = ConnectionDetector(context!!.applicationContext)
//        if (connectionDetector!!.isConnectingToInternet) {
//            loadContacts()
//        }else{
//            context!!.toast(getString(R.string.no_internet_connection), Toast.LENGTH_LONG)
//        }
        checkContacts()
    }

    fun checkContacts() {
        context!!.dbHelper.getContacts() {
            receivedContacts(it)
        }
    }

    private fun receivedContacts(items: List<Contact>) {
        if (context == null || activity == null)
            return

        mContacts = items
        val listContacts = ArrayList<ListItem>(mContacts.size)
        val replaceDescription = context!!.config.replaceDescription
        val sorted = mContacts.sortedWith(compareBy({ it.id}, { it.firstname }, { it.lastname }, { if (replaceDescription) it.lastname else it.lastname }))
        val sublist = sorted.subList(0, Math.min(sorted.size, 100))
        sublist.forEach {
            listContacts.add(Contact(it.id,it.userid , it.is_primary,it.firstname, it.lastname,it.email, it.phonenumber, it.title))
        }

        //mContactListAdapter = ContactListAdapter(context!!, listContacts)


        mContactListAdapter = ContactListAdapter(activity as SimpleActivity, listContacts, true, this, mView.contact_list) {
            if (it is Contact) {
                editItem(it)
            }
        }
        activity?.runOnUiThread {
            mView.contact_list.apply {
                this@apply.adapter = mContactListAdapter
            }
            checkPlaceholderVisibility()
        }
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_contact_list_fragment, menu)

        val item = menu.findItem(R.id.action_search)
        val searchView = MenuItemCompat.getActionView(item) as SearchView
        searchView.setOnQueryTextListener(this)

        item.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {

            override fun onMenuItemActionExpand(item: MenuItem): Boolean {

                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                // Do something when collapsed
                mContactListAdapter!!.setSearchResult(mContacts)
                return true // Return true to collapse action view

            }
        })
    }

    private fun checkPlaceholderVisibility() {
        mView.contact_empty_list_placeholder.beVisibleIf(mContacts.isEmpty())
        mView.contact_list.beGoneIf(mContacts.isEmpty())
        if (activity != null)
            mView.contact_empty_list_placeholder.setTextColor(activity!!.config.textColor)
    }

    private fun editItem(contact: Contact) {
        Intent(context, ContactActivity::class.java).apply {
            putExtra(CONTACT_ID, contact.id)
            startActivity(this)
        }
    }

    override fun deleteContacts(ids: java.util.ArrayList<Int>) {
        val itemIDs = Array(ids.size, { i -> (ids[i].toString()) })
        context!!.dbHelper.deleteContacts(itemIDs)
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        val filteredContactList = filter(mContacts, newText)
        //mProductListAdapter!!.setSearchResult(filteredModelList)
        if (newText.isEmpty()) {
            checkContacts()
        }else{
            receivedContacts(filteredContactList)
        }
        return true
    }

    private fun filter(contacts: List<Contact>, query: String): List<Contact> {
        var query = query
        query = query.toLowerCase()
        val filteredContactList = java.util.ArrayList<Contact>()
        for (contact in contacts) {
            val text = contact.lastname.toLowerCase()
            if (text.contains(query)) {
                filteredContactList.add(contact)
            }
        }
        return filteredContactList
    }

}