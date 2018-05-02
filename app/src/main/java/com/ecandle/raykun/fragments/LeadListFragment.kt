package com.ecandle.raykun.fragments


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.SearchView
import android.view.*
import com.ecandle.raykun.R
import com.ecandle.raykun.activities.LeadLayoutTabActivity
import com.ecandle.raykun.activities.SimpleActivity
import com.ecandle.raykun.adapters.LeadListAdapter
import com.ecandle.raykun.extensions.config
import com.ecandle.raykun.extensions.dbHelper
import com.ecandle.raykun.helpers.LEAD_ID
import com.ecandle.raykun.interfaces.DeleteLeadsListener
import com.ecandle.raykun.models.Lead
import com.ecandle.raykun.models.ListItem
import com.simplemobiletools.commons.extensions.beGoneIf
import com.simplemobiletools.commons.extensions.beVisibleIf
import kotlinx.android.synthetic.main.fragment_lead_list.view.*

/**
 * A simple [Fragment] subclass.
 */
class LeadListFragment : Fragment() , DeleteLeadsListener, SearchView.OnQueryTextListener {

    private val LOG_TAG = LeadListFragment::class.java.simpleName
    private var mLeads: List<Lead> = ArrayList()
    lateinit var mView: View
    private var mLeadListAdapter: LeadListAdapter? = null
    lateinit var mLead: Lead
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_lead_list, container, false)
        val placeholderText = String.format(getString(R.string.two_string_placeholder), "${getString(R.string.no_upcoming_records)}\n", getString(R.string.add_some_records))
        mView.lead_empty_list_placeholder.text = placeholderText
        return mView
    }

    override fun onResume() {
        super.onResume()
//        var connectionDetector = ConnectionDetector(context!!.applicationContext)
//        if (connectionDetector!!.isConnectingToInternet) {
//            loadLeads()
//        }else{
//            context!!.toast(getString(R.string.no_internet_connection), Toast.LENGTH_LONG)
//        }
        checkLeads()
    }

    fun checkLeads() {
        context!!.dbHelper.getLeads() {
            receivedLeads(it)
        }
    }

    private fun receivedLeads(items: List<Lead>) {
        if (context == null || activity == null)
            return

        mLeads = items
        val listLeads = ArrayList<ListItem>(mLeads.size)
        val replaceDescription = context!!.config.replaceDescription
        val sorted = mLeads.sortedWith(compareBy({ it.id}, { it.name }, { it.company }, { if (replaceDescription) it.name else it.name }))
        val sublist = sorted.subList(0, Math.min(sorted.size, 100))
        sublist.forEach {
            listLeads.add(Lead(it.id,
                    it.name,
                    it.title,
                    it.company,
                    it.description,
                    it.country,
                    it.city,
                    it.zip,
                    it.state,
                    it.address,
                    it.assigned,
                    it.dateadded,
                    it.status,
                    it.source,
                    it.lastcontact,
                    it.email,
                    it.website,
                    it.phonenumber,
                    it.status_name,
                    it.source_name,
                    it.is_public))
        }

        mLeadListAdapter = LeadListAdapter(activity as SimpleActivity, listLeads, true, this, mView.lead_list) {
            if (it is Lead) {
                editItem(it)
            }
        }
        activity?.runOnUiThread {
            mView.lead_list.apply {
                this@apply.adapter = mLeadListAdapter
            }
            checkPlaceholderVisibility()
        }
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_lead_list_fragment, menu)

        val item = menu.findItem(R.id.action_search)
        val searchView = MenuItemCompat.getActionView(item) as SearchView
        searchView.setOnQueryTextListener(this)

        item.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {

            override fun onMenuItemActionExpand(item: MenuItem): Boolean {

                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                // Do something when collapsed
                mLeadListAdapter!!.setSearchResult(mLeads)
                return true // Return true to collapse action view

            }
        })
    }

    private fun checkPlaceholderVisibility() {
        mView.lead_empty_list_placeholder.beVisibleIf(mLeads.isEmpty())
        mView.lead_list.beGoneIf(mLeads.isEmpty())
        if (activity != null)
            mView.lead_empty_list_placeholder.setTextColor(activity!!.config.textColor)
    }

    private fun editItem(lead: Lead) {
        Intent(context, LeadLayoutTabActivity::class.java).apply {
            putExtra(LEAD_ID, lead.id)
            startActivity(this)
        }
    }

    override fun deleteLeads(ids: java.util.ArrayList<Int>) {
        val itemIDs = Array(ids.size, { i -> (ids[i].toString()) })
        context!!.dbHelper.deleteLeads(itemIDs)
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        val filteredLeadList = filter(mLeads, newText)
        //mProductListAdapter!!.setSearchResult(filteredModelList)
        if (newText.isEmpty()) {
            checkLeads()
        }else{
            receivedLeads(filteredLeadList)
        }
        return true
    }

    private fun filter(leads: List<Lead>, query: String): List<Lead> {
        var query = query
        query = query.toLowerCase()
        val filteredLeadList = java.util.ArrayList<Lead>()
        for (lead in leads) {
            val text = lead.name.toLowerCase()
            if (text.contains(query)) {
                filteredLeadList.add(lead)
            }
        }
        return filteredLeadList
    }

}