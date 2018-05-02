package com.ecandle.raykun.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.ecandle.raykun.fragments.TabContactInfoFragment
import com.ecandle.raykun.fragments.TabGeneralInfoFragment
import com.ecandle.raykun.models.Lead

class LeadTabPagerAdapter(fm: FragmentManager, private var tabCount: Int, var lead: Lead) :
        FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? {

        when (position) {
            0 -> return TabContactInfoFragment.newInstance(lead)
            1 -> return TabGeneralInfoFragment.newInstance(lead)
            else -> return null
        }
    }

    override fun getCount(): Int {
        return tabCount
    }


}


