package com.ecandle.raykun.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.ecandle.raykun.fragments.TabClientDetailsFragment
import com.ecandle.raykun.fragments.TabContactListFragment
import com.ecandle.raykun.fragments.TabInvoiceInfoFragment
import com.ecandle.raykun.fragments.TabTeamSalesFragment
import com.ecandle.raykun.models.Client

class ClientTabPagerAdapter(fm: FragmentManager, private var tabCount: Int,var client: Client) :
        FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? {

        when (position) {
            0 -> return TabClientDetailsFragment.newInstance(client)
            1 -> return TabInvoiceInfoFragment.newInstance(client)
            2 -> return TabContactListFragment.newInstance(client)
            3 -> return TabTeamSalesFragment.newInstance(client)
            else -> return null
        }
    }

    override fun getCount(): Int {
        return tabCount
    }


}


