package com.ecandle.raykun.activities
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.ecandle.raykun.R
import com.ecandle.raykun.extensions.dbHelper
import com.ecandle.raykun.fragments.ContactListFragment
import com.ecandle.raykun.helpers.ConnectionDetector
import com.ecandle.raykun.helpers.USER_ID
import com.ecandle.raykun.models.Contact
import com.ecandle.raykun.models.Item
import com.ecandle.raykun.tasks.loadContactDataTask
import com.simplemobiletools.commons.extensions.beVisible
import kotlinx.android.synthetic.main.activity_contact_list.*

class ContactListActivity : SimpleActivity() {
    private val LOG_TAG = ContactListActivity::class.java.simpleName
    private var mUserId: String? = null
    lateinit var mItem: Item
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_list)

        var connectionDetector = ConnectionDetector(this)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_cross)

        supportActionBar?.title = resources.getString(R.string.contacts)

        contact_fab.setOnClickListener { launchNewContactIntent() }

        mUserId = intent.getStringExtra(USER_ID)
        val intent = intent ?: return

        if (connectionDetector!!.isConnectingToInternet) {
            loadContacts()
        }

        fillContactsList()

    }

    fun launchNewContactIntent() {
        Intent(applicationContext, ContactActivity::class.java).apply {
            putExtra(USER_ID, mUserId)
            startActivity(this)
        }
    }

    private fun fillContactsList() {
        contact_list_holder.beVisible()
        supportFragmentManager.beginTransaction().replace(R.id.contact_list_holder, ContactListFragment(), "").commit()
    }

    fun loadContacts(){
        if (dbHelper.isTableExists(dbHelper.CONTACTS_TABLE_NAME)) {
            dbHelper.initContactsTable()
        }else{
            dbHelper.createContactsTable()
        }
        //http://ecandle.local/RayKun/webservice/index.php/admin/clients/showAllContacts
        val url="http://ecandlemobile.com/RayKun/webservice/index.php/admin/clients/showAllContacts"
        val loadContactData = loadContactDataTask(this)

        val contactsData =  loadContactData.execute(url).get()

        Log.d("loadContactData",contactsData.toString())

        for (contact in contactsData){
            saveContact(contact)
        }
    }
    private fun saveContact(contact: Contact) {

        dbHelper.insertContact(contact)
        Log.d(LOG_TAG,"contact added")

    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.menu_product_list, menu)
//        menu.findItem(R.id.exit).isVisible = true
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.exit -> finish()
//            else -> return super.onOptionsItemSelected(item)
//        }
//        return true
//    }
}
