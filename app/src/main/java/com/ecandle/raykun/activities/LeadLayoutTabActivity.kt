package com.ecandle.raykun.activities

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity
import com.ecandle.raykun.R
import com.ecandle.raykun.adapters.LeadTabPagerAdapter
import com.ecandle.raykun.extensions.dbHelper
import com.ecandle.raykun.helpers.LEAD_ID
import com.ecandle.raykun.models.Lead
import kotlinx.android.synthetic.main.activity_lead_layout_tab.*


class LeadLayoutTabActivity : AppCompatActivity() {
    private val LOG_TAG = LeadLayoutTabActivity::class.java.simpleName

    //lateinit var mLead: Lead

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lead_layout_tab)
        setSupportActionBar(mytoolbar)
        supportActionBar?.title = resources.getString(R.string.leads)
        val leadId = intent.getIntExtra(LEAD_ID, 0)
        val mLead = dbHelper.getLeadWithId(leadId)

        configureTabLayout(mLead!!)
    }

    private fun configureTabLayout(lead: Lead) {

        tab_layout.addTab(tab_layout.newTab().setText(getString(R.string.contact_info)))
        tab_layout.addTab(tab_layout.newTab().setText(getString(R.string.general_info)))

        val adapter = LeadTabPagerAdapter(supportFragmentManager,
                tab_layout.tabCount,lead)

        pager.adapter = adapter

        pager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tab_layout))

        tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
//                val actionBar = actionBar
//                actionBar!!.hide()
                pager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }

        })
    }


}