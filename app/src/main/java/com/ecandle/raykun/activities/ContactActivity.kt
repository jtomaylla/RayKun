package com.ecandle.raykun.activities

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import com.ecandle.raykun.R
import com.ecandle.raykun.dialogs.DeleteContactDialog
import com.ecandle.raykun.extensions.dbHelper
import com.ecandle.raykun.helpers.CONTACT_ID
import com.ecandle.raykun.helpers.USER_ID
import com.ecandle.raykun.models.Contact
import com.simplemobiletools.commons.extensions.getDialogTheme
import com.simplemobiletools.commons.extensions.toast
import com.simplemobiletools.commons.extensions.updateTextColors
import com.simplemobiletools.commons.extensions.value
import kotlinx.android.synthetic.main.activity_contact.*

class ContactActivity : SimpleActivity() {
    private val LOG_TAG = ContactActivity::class.java.simpleName
    private var mDialogTheme = 0

    private var wasActivityInitialized = false
    private var wasIsPrimaryToggled = false
    lateinit var mContact: Contact



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_cross)
        supportActionBar?.title = resources.getString(R.string.contacts)
        val intent = intent ?: return
        mDialogTheme = getDialogTheme()

        // Load Contact Groups from Shared Preferences
        //loadContactGroups()

        val contactId = intent.getIntExtra(CONTACT_ID, 0)
        val mUserId = intent.getStringExtra(USER_ID)
        val contact = dbHelper.getContactWithId(contactId)
        if (contactId != 0 && contact == null) {
            finish()
            return
        }

        if (contact != null) {
            mContact = contact
            setupEditContact()
            //mGroupId = mContact.group_id.toInt()
        } else {
            mContact = Contact(0,mUserId,"","","","","","")

            setupNewContact()
        }

        updateFirstName()
        updateLastName()
        updateEmail()
        updatePhoneNumber()
        updateTitle()
        updateIsPrimary()
        updateUserId()
        setupContactIsPrimary()
        //contact_group_id.setOnClickListener { showGroupIdDialog() }
        
        updateTextColors(contact_scrollview)

        wasActivityInitialized = true
    }

    private fun setupContactIsPrimary() {
        //contact_is_primary_holder.beVisibleIf(wasIsPrimaryToggled || mContact.is_primary != "1")

        contact_is_primary_holder.setOnClickListener {
            contact_is_primary.toggle()
            if (contact_is_primary.isChecked){
                mContact.is_primary = "1"
            } else {
                mContact.is_primary = "0"
            }
        }
    }


    private fun setupEditContact() {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        supportActionBar?.title = resources.getString(R.string.edit_contact)
        contact_firstname.setText(mContact.firstname)
        contact_lastname.setText(mContact.lastname)
        contact_email.setText(mContact.email)
        contact_phonenumber.setText(mContact.phonenumber)
        contact_title.setText(mContact.title)
        //contact_is_primary.setText(mContact.is_primary)
        updateIsPrimary()
        contact_is_primary.isChecked = mContact.is_primary == "1"
        contact_user_id.text = mContact.userid
        //contact_group_id.setText(mContact.group_id)
    }

    private fun setupNewContact() {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        supportActionBar?.title = resources.getString(R.string.new_contact)
    }


    private fun updateFirstName() {
        contact_firstname.setText(mContact.firstname)
    }

    private fun updateLastName() {
        contact_lastname.setText(mContact.lastname)
    }

    private fun updateEmail() {
        contact_email.setText(mContact.email)
    }

    private fun updatePhoneNumber() {
        contact_phonenumber.setText(mContact.phonenumber)
    }

    private fun updateTitle() {
        contact_title.setText(mContact.title)
    }

    private fun updateIsPrimary() {
        contact_is_primary.isChecked = mContact.is_primary == "1"
    }

    private fun updateUserId() {
        contact_user_id.text = mContact.userid
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_task, menu)
        if (wasActivityInitialized) {
            menu.findItem(R.id.delete).isVisible = mContact.id != 0
            menu.findItem(R.id.exit).isVisible = true
        }
        return true
    }

    override fun onOptionsItemSelected(contact: MenuItem): Boolean {
        when (contact.itemId) {
            R.id.save -> saveContact()
            R.id.delete -> deleteContact()
            R.id.exit -> finish()
            else -> return super.onOptionsItemSelected(contact)
        }
        return true
    }

    private fun deleteContact() {
        DeleteContactDialog(this, arrayListOf(mContact.id)) {
            if (it) {
                dbHelper.deleteContacts(arrayOf(mContact.id.toString()))
            }
            finish()
        }
    }

    private fun saveContact() {
        val newId = mContact.id
        val newFirstname  = contact_firstname.value
        if (newFirstname.isEmpty()) {
            toast(R.string.title_empty)
            contact_firstname.requestFocus()
            return
        }
        val newLastname  = contact_lastname.value
        if (newLastname.isEmpty()) {
            toast(R.string.title_empty)
            contact_lastname.requestFocus()
            return
        }
        val newEmail = contact_email.value
        val newPhonenumber = contact_phonenumber.value
        val newTitle= contact_title.value
        var  newIsPrimary = ""
        if (contact_is_primary.isChecked) {
            newIsPrimary = "1"
        } else {
            newIsPrimary = "0"
        }
        val newUserId = contact_user_id.value

        mContact = Contact(id = newId,
                userid = newUserId,
                is_primary =  newIsPrimary,
                firstname = newFirstname,
                phonenumber = newPhonenumber,
                lastname = newLastname,
                email = newEmail ,
                title = newTitle
        )
        //mContact.id = 0
        Log.d(LOG_TAG,"mContact.contactid = $mContact.id")
        storeContact()
    }

    private fun storeContact() {
        if (mContact.id == 0) {
            // Add last Contact value ID plus one for new contact
            val contacts = dbHelper.fetchContacts()
            mContact.id = contacts[contacts.size-1].id + 1
            //
            dbHelper.insertContact(mContact)
            Log.d(LOG_TAG,"contact added")
            finish()
        } else {
            dbHelper.updateContact(mContact)
            Log.d(LOG_TAG,"contact updated")
           contactUpdated()
        }
    }

    private fun contactUpdated() {
        toast(R.string.contact_updated)
        finish()
    }


//    private fun showGroupIdDialog() {
//        showContactGroupIdDialog(mGroupId) {
//            setGroupId(it)
//        }
//    }
//
//    private fun setGroupId(groupid: Int) {
//        mGroupId = groupid
//        updateIsPrimary()
//    }
//
//    protected fun showContactGroupIdDialog(curGroupId: Int, callback: (minutes: Int) -> Unit) {
//        hideKeyboard()
//
//        val contacts = ArrayList<RadioContact>(groups.size + 1)
//
//        groups.mapIndexedTo(contacts, { index, value ->
//            RadioContact(index, getGroupIdText(value))
//            RadioContact(index, getGroupIdText(value), value)
//        })
//
//        var selectedIndex = 0
//
//        groups.forEachIndexed { index, value ->
//            if (value == curGroupId)
//                selectedIndex = index
//        }
//
//        RadioGroupDialog(this, contacts, selectedIndex) {
//            callback(it as Int)
//        }
//    }
//
//
//    fun loadContactGroups(){
//        val savedSettings = SavedSettings(applicationContext)
//
//        val jArray = JSONArray(savedSettings.getJsonInvoiceDataContact("contacts_groups"))
//        val contactData1 = ContactGroup(
//                0,
//                getString(R.string.empty_name)
//        )
//        data.add(contactData1)
//        groups.add(contactData1.id)
//        // Extract data from json and store into ArrayList as class objects
//        for (i in 0 until jArray.length()) {
//            val jsondata: JSONObject = jArray.getJSONObject(i)
//            val contactData = ContactGroup(
//                    jsondata.getInt("id"),
//                    jsondata.getString("name")
//            )
//            data.add(contactData)
//            groups.add(contactData.id)
//        }
//
//    }
//
//    fun getGroupIdText(groupid: Int) : String {
//        return data.get(groupid).name
//    }
//
//    private fun getGroupIdValue(groupid: Int): String {
//        return data.get(groupid).id.toString()
//    }
}
