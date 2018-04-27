package com.ecandle.raykun.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ecandle.raykun.R
import com.ecandle.raykun.adapters.ContactListAdapter
import com.ecandle.raykun.extensions.config
import com.ecandle.raykun.extensions.dbHelper
import com.ecandle.raykun.helpers.ConnectionDetector
import com.ecandle.raykun.models.Client
import com.ecandle.raykun.models.Contact
import com.ecandle.raykun.tasks.loadContactDataTask
import com.simplemobiletools.commons.extensions.beGoneIf
import com.simplemobiletools.commons.extensions.beVisibleIf
import com.simplemobiletools.commons.extensions.toast
import kotlinx.android.synthetic.main.fragment_tab_contact_list.view.*

/**
 * A simple [Fragment] subclass.
 */
class TabContactListFragment : Fragment()  {

    private val LOG_TAG = TabContactListFragment ::class.java.simpleName
    private var mContacts: List<Contact> = ArrayList()
    lateinit var mView: View
    private var mContactListAdapter: ContactListAdapter? = null
    lateinit var mContact: Contact
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //setHasOptionsMenu(false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_tab_contact_list, container, false)
        val placeholderText = String.format(getString(R.string.two_string_placeholder), "${getString(R.string.no_upcoming_contacts)}\n", getString(R.string.add_some_contacts))
        mView.contact_empty_list_placeholder.text = placeholderText
        return mView
    }

    override fun onResume() {
        super.onResume()
        var connectionDetector = ConnectionDetector(context!!.applicationContext)
        if (connectionDetector!!.isConnectingToInternet) {
            loadContacts()
        }else{
            context!!.toast(getString(R.string.no_internet_connection), Toast.LENGTH_LONG)
        }
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
        val listContacts = ArrayList<Contact>(mContacts.size)
        val replaceDescription = context!!.config.replaceDescription
        val sorted = mContacts.sortedWith(compareBy({ it.id}, { it.firstname }, { it.lastname }, { if (replaceDescription) it.lastname else it.lastname }))
        val sublist = sorted.subList(0, Math.min(sorted.size, 100))
        sublist.forEach {
            listContacts.add(Contact(it.id,it.userid , it.is_primary,it.firstname, it.lastname,it.email, it.phonenumber, it.title))
        }

        mContactListAdapter = ContactListAdapter(context!!, listContacts)

        activity?.runOnUiThread {
            mView.contact_list.apply {
                this@apply.adapter = mContactListAdapter
            }
            checkPlaceholderVisibility()
        }
    }

    private fun checkPlaceholderVisibility() {
        mView.contact_empty_list_placeholder.beVisibleIf(mContacts.isEmpty())
        mView.contact_list.beGoneIf(mContacts.isEmpty())
        if (activity != null)
            mView.contact_empty_list_placeholder.setTextColor(activity!!.config.textColor)
    }

    fun loadContacts(){
        val mUserId =arguments!!.getString("userid")

        if (context!!.dbHelper.isTableExists(context!!.dbHelper.CONTACTS_TABLE_NAME)) {
            context!!.dbHelper.initContactsTable()
            //context!!.dbHelper.dropTable(context!!.dbHelper.CONTACTS_TABLE_NAME)
        }else{
            context!!.dbHelper.createContactsTable()
        }
        //context!!.dbHelper.dropTable(context!!.dbHelper.CONTACTS_TABLE_NAME)
        //context!!.dbHelper.createContactsTable()
        val url="http://ecandlemobile.com/RayKun/webservice/index.php/admin/clients/showAllContactsClient?id="+mUserId
        val loadContactData = loadContactDataTask(context!!.applicationContext)

        val contactsData =  loadContactData.execute(url).get()

        Log.d("loadContactData",contactsData.toString())

        for (contact in contactsData){
            saveContact(contact)
        }
    }
    private fun saveContact(contact: Contact) {

        context!!.dbHelper.insertContact(contact)
        Log.d(LOG_TAG,"contact added")

    }

    companion object {

        fun newInstance(client: Client): TabContactListFragment {
            val arguments = Bundle()

            arguments.putString("userid", client.userid.toString())

            val fragment = TabContactListFragment()
            fragment.arguments = arguments
            return fragment
        }
    }

}