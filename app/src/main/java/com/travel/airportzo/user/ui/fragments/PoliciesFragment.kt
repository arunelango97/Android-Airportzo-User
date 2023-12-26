package com.travel.airportzo.user.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.travel.airportzo.user.R
import com.travel.airportzo.user.databinding.PoliciesFragmentBinding
import com.travel.airportzo.user.savedpreference.SavedSharedPreference
import com.travel.airportzo.user.utils.setOnDebounceListener
import com.travel.airportzo.user.ui.adapter.PolicyFragmentAdapter
import com.travel.airportzo.user.ui.base.BaseFragment


class PoliciesFragment : BaseFragment() {

private val policiesFragment by lazy { PoliciesFragmentBinding.inflate(layoutInflater) }
    private val tabList = arrayOf("Terms and condition","Privacy policy","Cancellation policy")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        /** brand color */

        policiesFragment.rootLayout.setBackgroundColor(Color.parseColor(activity?.let {
            SavedSharedPreference.getCustomColor(
                it
            ).brand_colour
        }))


        policiesFragment.personalBack.setOnDebounceListener {
//            findNavController().navigate(R.id.action_cartPoliciesFragment_to_navigation_checkout)
            findNavController().popBackStack()
        }

        val adapter = PolicyFragmentAdapter(childFragmentManager, lifecycle)
        policiesFragment.myViewPager.adapter = adapter
        TabLayoutMediator(policiesFragment.myTabLayout, policiesFragment.myViewPager) { tab, position ->
            tab.text = tabList[position]
        }.attach()

/*        policiesFragment.apply {
            myTabLayout.addTab(policiesFragment.myTabLayout.newTab().setText("Terms and condition"))
            myTabLayout.addTab(policiesFragment.myTabLayout.newTab().setText("Privacy policy"))
            myTabLayout.addTab(policiesFragment.myTabLayout.newTab().setText("Cancellation policy"))
            myTabLayout.tabGravity = TabLayout.GRAVITY_FILL
            val adapter = PolicyFragmentAdapter(childFragmentManager)
            myViewPager.adapter = adapter
            ViewCompat.setNestedScrollingEnabled(myViewPager, true)
            myViewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(myTabLayout))
            myTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    myViewPager.currentItem = tab.position
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })

        }*/


    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): ConstraintLayout {

        return policiesFragment.root


    }



}