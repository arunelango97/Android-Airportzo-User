package com.travel.airportzo.user.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.travel.airportzo.user.ui.fragments.*

class AgentBookingAdapter(fm : FragmentManager) : FragmentPagerAdapter(fm) {
    companion object {
        const val NUM_ITEMS = 2
    }

    override fun getCount(): Int {
        return NUM_ITEMS
    }

    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> return AgentMySelfFragment()
            1 -> return AgentOtherFragment()
        }
        return AgentMySelfFragment()
    }
}