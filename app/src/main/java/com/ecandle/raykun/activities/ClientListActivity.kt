package com.ecandle.raykun.activities
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.ecandle.raykun.R
import com.ecandle.raykun.extensions.dbHelper
import com.ecandle.raykun.extensions.launchNewClientIntent
import com.ecandle.raykun.fragments.ClientListFragment
import com.ecandle.raykun.helpers.ConnectionDetector
import com.ecandle.raykun.helpers.USER_ID
import com.ecandle.raykun.models.Client
import com.ecandle.raykun.tasks.loadClientDataTask
import com.simplemobiletools.commons.extensions.beVisible
import com.simplemobiletools.commons.extensions.toast
import kotlinx.android.synthetic.main.activity_client_list.*

class ClientListActivity : SimpleActivity() {
    private val LOG_TAG = ClientListActivity::class.java.simpleName
    private var mUserId: String? = null
    lateinit var mClient: Client

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_list)

        var connectionDetector = ConnectionDetector(this)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_cross)

        supportActionBar?.title = resources.getString(R.string.clients)

        client_fab.setOnClickListener { launchNewClientIntent() }

        mUserId = intent.getStringExtra(USER_ID)
        val intent = intent ?: return

        if (connectionDetector!!.isConnectingToInternet) {
            loadClients()
        }else{
            toast(getString(R.string.no_internet_connection), Toast.LENGTH_LONG)
        }

        fillClientsList()

    }

    private fun fillClientsList() {
        client_list_holder.beVisible()
        supportFragmentManager.beginTransaction().replace(R.id.client_list_holder, ClientListFragment(), "").commit()
    }

    fun loadClients(){

        if (dbHelper.isTableExists(dbHelper.CLIENTS_TABLE_NAME)) {
            dbHelper.initClientsTable()
        }else{
            dbHelper.createClientsTable()
        }

        val url="http://ecandlemobile.com/RayKun/webservice/index.php/admin/clients/showAllClients"
        val loadClientData = loadClientDataTask(this)

        val clientsData =  loadClientData.execute(url).get()

        Log.d("loadClientDataClient",clientsData.toString())

        for (client in clientsData){
            saveClient(client)
        }
    }

    private fun saveClient(client: Client) {
        val userid= client.userid
        val company = client.company
        val vat = client.vat
        val phonenumber = client.phonenumber
        val country = client.country
        val city = client.city
        val zip= client.zip
        val state = client.state
        val address = client.address
        val website = client.website
        val datecreated= client.datecreated
        val active = client.active
        val leadid= client.leadid
        val billing_street = client.billing_street
        val billing_city = client.billing_city
        val billing_state = client.billing_state
        val billing_zip = client.billing_zip
        val billing_country = client.billing_country
        val shipping_street = client.shipping_street
        val shipping_city = client.shipping_city
        val shipping_state = client.shipping_state
        val shipping_zip = client.shipping_zip
        val shipping_country = client.shipping_country
        val longitude = client.longitude
        val latitude = client.latitude
        val default_language = client.default_language
        val default_currency = client.default_currency
        val show_primary_contact = client.show_primary_contact
        val addedfrom = client.addedfrom
        val contact_name = client.contact_name
        val contact_email = client.contact_email
        val country_name = client.country_name
        val billing_country_name = client.billing_country_name
        val shipping_country_name = client.shipping_country_name
        mClient = Client(userid ,
                company,
                vat,
                phonenumber ,
                country,
                city,
                zip,
                state ,
                address,
                website,
                datecreated,
                active,
                leadid,
                billing_street,
                billing_city,
                billing_state,
                billing_zip,
                billing_country,
                shipping_street,
                shipping_city,
                shipping_state,
                shipping_zip,
                shipping_country,
                longitude ,
                latitude,
                default_language,
                default_currency,
                show_primary_contact,
                addedfrom,
                contact_name,
                contact_email,
                country_name,
                billing_country_name,
                shipping_country_name
                )
        storeClient()
    }

    private fun storeClient() {
            dbHelper.insertClient(mClient)
                Log.d(LOG_TAG,"client added")
                //finish()
    }



//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.menu_client_list, menu)
//        menu.findClient(R.id.exit).isVisible = true
//        return true
//    }
//
//    override fun onOptionsClientSelected(client: MenuClient): Boolean {
//        when (client.clientId) {
//            R.id.exit -> finish()
//            else -> return super.onOptionsClientSelected(client)
//        }
//        return true
//    }
}
