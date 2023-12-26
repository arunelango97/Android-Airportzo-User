package com.travel.airportzo.user.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import com.google.android.material.tabs.TabLayout
import com.travel.airportzo.user.databinding.AgentBookingFragmentBinding
import com.travel.airportzo.user.ui.adapter.AgentBookingAdapter
import com.travel.airportzo.user.ui.base.BaseFragment


/**
 * Agent booking fragment
 *
 * @constructor Create empty Agent booking fragment
 */
class AgentBookingFragment : BaseFragment() {

    private val agentBooking by lazy { AgentBookingFragmentBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        agentBooking.apply {
            bookingTab.addTab(agentBooking.bookingTab.newTab().setText("For Myself"))
            bookingTab.addTab(agentBooking.bookingTab.newTab().setText("For Others"))
            bookingTab.tabGravity = TabLayout.GRAVITY_FILL
            val adapter = AgentBookingAdapter(childFragmentManager)
            myViewPager.adapter = adapter
            ViewCompat.setNestedScrollingEnabled(myViewPager, true)
            myViewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(bookingTab))
            bookingTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    myViewPager.currentItem = tab.position
                }
                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return agentBooking.root
    }

}