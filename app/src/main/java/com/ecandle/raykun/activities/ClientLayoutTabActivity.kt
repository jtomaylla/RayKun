package com.ecandle.raykun.activities

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity
import com.ecandle.raykun.R
import com.ecandle.raykun.adapters.ClientTabPagerAdapter
import com.ecandle.raykun.extensions.dbHelper
import com.ecandle.raykun.helpers.ITEM_ID
import com.ecandle.raykun.models.Client
import kotlinx.android.synthetic.main.activity_client_layout_tab.*


class ClientLayoutTabActivity  : AppCompatActivity() {
    private val LOG_TAG = ClientLayoutTabActivity ::class.java.simpleName

    //lateinit var mClient: Client

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_layout_tab)
        setSupportActionBar(mytoolbar)
        supportActionBar?.title = resources.getString(R.string.clients)
        val clientId = intent.getIntExtra(ITEM_ID, 0)
        val mClient = dbHelper.getClientWithId(clientId)

        configureTabLayout(mClient!!)
    }

    private fun configureTabLayout(client: Client) {

        tab_layout.addTab(tab_layout.newTab().setText(getString(R.string.details)))
        tab_layout.addTab(tab_layout.newTab().setText(getString(R.string.invoice_info)))
        tab_layout.addTab(tab_layout.newTab().setText(getString(R.string.contacts)))
        tab_layout.addTab(tab_layout.newTab().setText(getString(R.string.team_sales)))

        val adapter = ClientTabPagerAdapter(supportFragmentManager,
                tab_layout.tabCount,client)

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