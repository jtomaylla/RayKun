package com.ecandle.raykun.adapters

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.util.SparseArray
import com.ecandle.raykun.fragments.DayFragment
import com.ecandle.raykun.helpers.DAY_CODE
import com.ecandle.raykun.interfaces.NavigationListener

class MyDayPagerAdapter(fm: FragmentManager, private val mCodes: List<String>, private val mListener: NavigationListener) :
        FragmentStatePagerAdapter(fm) {
    private val mFragments = SparseArray<DayFragment>()

    override fun getCount() = mCodes.size

    override fun getItem(position: Int): Fragment {
        val bundle = Bundle()
        val code = mCodes[position]
        bundle.putString(DAY_CODE, code)

        val fragment = DayFragment()
        fragment.arguments = bundle
        fragment.mListener = mListener

        mFragments.put(position, fragment)
        return fragment
    }

    fun checkDayEvents(pos: Int) {
        for (i in -1..1) {
            mFragments[pos + i]?.checkEvents()
        }
    }

    fun destroyMultiselector(pos: Int) {
        for (i in -1..1) {
            mFragments[pos + i]?.getDayEventsAdapter()?.finishActMode()
        }
    }
}
