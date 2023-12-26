package com.travel.airportzo.user.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.travel.airportzo.user.R
import com.travel.airportzo.user.databinding.NotificationFragmentBinding
import com.travel.airportzo.user.model.NotificationData
import com.travel.airportzo.user.savedpreference.SavedSharedPreference
import com.travel.airportzo.user.ui.adapter.NotificationAdapter
import com.travel.airportzo.user.ui.base.BaseFragment
import java.util.ArrayList

class NotificationFragment : BaseFragment(){

    private val notificationFragment by lazy { NotificationFragmentBinding.inflate(layoutInflater) }

    private val notificationData: ArrayList<NotificationData> by lazy { arrayListOf() }
  private val notificationAdapter by lazy { context?.let { NotificationAdapter(it,notificationData) }}


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        notificationData()
        notificationFragment.notificationList.adapter = notificationAdapter



    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return notificationFragment.root


    }

    private fun notificationData() {
       notificationData.add(NotificationData(bannerimage = "https://dinlsoa01c2rk.cloudfront.net/support_video/1647837276838.png", notificationdes = "Contact your vendor now!", notificationtitle = "Now you can communicate with your vendor\n" +
               "for the multi journey MAA-DXB-FRA", date = "2 days"))
    }

}