package com.travel.airportzo.user.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.travel.airportzo.user.ui.fragments.*

class BottomFragmentAdapter(fm : FragmentManager) : FragmentPagerAdapter(fm) {

    companion object{
        const val NUM_ITEMS = 3
    }

    override fun getItem(position: Int): Fragment {
        when(position){
            0 -> return PackageServiceFragment()
            1 -> return PackageAboutFragment()
            2 -> return PackageReviewFragment()
        }
        return PackageServiceFragment()
    }

    override fun getCount(): Int {
        return  NUM_ITEMS
    }
}