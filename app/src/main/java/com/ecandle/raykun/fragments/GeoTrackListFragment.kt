package com.ecandle.raykun.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.SearchView
import android.view.*
import com.ecandle.raykun.R
import com.ecandle.raykun.activities.GeoTrackMapsActivity
import com.ecandle.raykun.activities.RoutingMapsActivity
import com.ecandle.raykun.activities.SimpleActivity
import com.ecandle.raykun.adapters.GeoTrackListAdapter
import com.ecandle.raykun.extensions.config
import com.ecandle.raykun.extensions.dbHelper
import com.ecandle.raykun.helpers.*
import com.ecandle.raykun.interfaces.RoutingClientsListener
import com.ecandle.raykun.models.Client
import com.ecandle.raykun.models.ListItem
import com.simplemobiletools.commons.extensions.beGoneIf
import com.simplemobiletools.commons.extensions.beVisibleIf
import kotlinx.android.synthetic.main.fragment_client_list.view.*


class GeoTrackListFragment : Fragment(), RoutingClientsListener, SearchView.OnQueryTextListener {

    private var mClients: List<Client> = ArrayList()
    lateinit var mView: View
    private var mGeoTrackListAdapter: GeoTrackListAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_client_list, container, false)
        val placeholderText = String.format(getString(R.string.two_string_placeholder), "${getString(R.string.no_upcoming_clients)}\n", getString(R.string.add_some_clients))
        mView.client_empty_list_placeholder.text = placeholderText
        return mView
    }

    override fun onResume() {
        super.onResume()
        checkClients()
    }

    fun checkClients() {
        context!!.dbHelper.getClients() {
            receivedClients(it)
        }
    }

    private fun receivedClients(clients: List<Client>) {
        if (context == null || activity == null)
            return

        mClients = clients
        val listClients = ArrayList<ListItem>(mClients.size)
        val replaceDescription = context!!.config.replaceDescription
        val sorted = mClients.sortedWith(compareBy({ it.userid}, { it.company }, { it.contact_name }, { if (replaceDescription) it.company else it.company }))
        val sublist = sorted.subList(0, Math.min(sorted.size, 100))
        var prevCode = ""
        sublist.forEach {
            listClients.add(Client(it.userid ,
                    it.company,
                    it.vat,
                    it.phonenumber ,
                    it.country,
                    it.city,
                    it.zip,
                    it.state ,
                    it.address,
                    it.website,
                    it.datecreated,
                    it.active,
                    it.leadid,
                    it.billing_street,
                    it.billing_city,
                    it.billing_state,
                    it.billing_zip,
                    it.billing_country,
                    it.shipping_street,
                    it.shipping_city,
                    it.shipping_state,
                    it.shipping_zip,
                    it.shipping_country,
                    it.longitude ,
                    it.latitude,
                    it.default_language,
                    it.default_currency,
                    it.show_primary_contact,
                    it.addedfrom,
                    it.contact_name,
                    it.contact_email,
                    it.country_name,
                    it.billing_country_name,
                    it.shipping_country_name
            ))
        }

        var allowLongClick = false
        if (arguments!!.getString("map_mode") == "2"){
            allowLongClick = true
        }

        mGeoTrackListAdapter = GeoTrackListAdapter(activity as SimpleActivity, listClients, allowLongClick, this, mView.client_list) {
            if (it is Client) {
                editClient(it)
            }
        }

        activity?.runOnUiThread {
            mView.client_list.apply {
                this@apply.adapter = mGeoTrackListAdapter
            }
            checkPlaceholderVisibility()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_client_list_fragment, menu)

        val item = menu.findItem(R.id.action_search)
        val searchView = MenuItemCompat.getActionView(item) as SearchView
        searchView.setOnQueryTextListener(this)

        item.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {

            override fun onMenuItemActionExpand(client: MenuItem): Boolean {

                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                // Do something when collapsed
                mGeoTrackListAdapter!!.setSearchResult(mClients)
                return true // Return true to collapse action view

            }
        })
    }

    private fun checkPlaceholderVisibility() {
        mView.client_empty_list_placeholder.beVisibleIf(mClients.isEmpty())
        mView.client_list.beGoneIf(mClients.isEmpty())
        if (activity != null)
            mView.client_empty_list_placeholder.setTextColor(activity!!.config.textColor)
    }

    private fun editClient(client: Client) {
        Intent(context, GeoTrackMapsActivity::class.java).apply {
            putExtra(ITEM_ID, client.userid)
            putExtra(CLIENT_LATITUDE, client.latitude)
            putExtra(CLIENT_LONGITUDE, client.longitude)
            putExtra(CLIENT_COMPANY, client.company)
            putExtra(CLIENT_ADDRESS, client.address)
            startActivity(this)
        }
    }

//    override fun deleteClients(ids: ArrayList<Int>) {
//        val clientIDs = Array(ids.size, { i -> (ids[i].toString()) })
//        context!!.dbHelper.deleteClients(clientIDs)
//    }


    override  fun routingClients(ids: ArrayList<Int>) {
        val clientIDs = Array(ids.size, { i -> (ids[i].toString()) })
        //var selectedClients = context!!.dbHelper.getClientsWithIds(clientIDs)
        Intent(context, RoutingMapsActivity::class.java).apply {
            putExtra(SELECTED_CLIENTS, clientIDs)
            startActivity(this)
        }
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        val filteredClientList = filter(mClients, newText)
        if (newText.isEmpty()) {
            checkClients()
        }else{
            receivedClients(filteredClientList)
        }
        return true
    }

    private fun filter(clients: List<Client>, query: String): List<Client> {
        var query = query
        query = query.toLowerCase()
        val filteredClientList = ArrayList<Client>()
        for (client in clients) {
            val text = client.company.toLowerCase()
            if (text.contains(query)) {
                filteredClientList.add(client)
            }
        }
        return filteredClientList
    }

    companion object {

        fun newInstance(mapMode: String): GeoTrackListFragment {
            val arguments = Bundle()

            arguments.putString("map_mode", mapMode)
            val fragment = GeoTrackListFragment()
            fragment.arguments = arguments
            return fragment
        }
    }
}
