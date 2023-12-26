package com.travel.airportzo.user.ui.bottomsheet

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayout
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.travel.airportzo.user.R
import com.travel.airportzo.user.model.PassengerStationData
import com.travel.airportzo.user.savedpreference.SavedSharedPreference
import com.travel.airportzo.user.ui.adapter.BottomFragmentAdapter
import com.travel.airportzo.user.ui.fragments.ChooseServiceFragment
import com.travel.airportzo.user.ui.fragments.ChooseServiceFragment.Companion.data
import com.travel.airportzo.user.ui.fragments.PackageServiceFragment
import com.travel.airportzo.user.utils.Utility
import com.travel.airportzo.user.utils.setOnDebounceListener


class BottomSheet : BottomSheetDialogFragment() {

    private lateinit var bottomSheet: ViewGroup
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>
    private lateinit var viewPager: ViewPager

    companion object {
        lateinit var array: JsonArray

    }

    override fun onStart() {
        super.onStart()

        bottomSheet =
            dialog!!.findViewById(com.google.android.material.R.id.design_bottom_sheet) as ViewGroup
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.peekHeight = BottomSheetBehavior.PEEK_HEIGHT_AUTO
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(view: View, i: Int) {
                if (BottomSheetBehavior.STATE_HIDDEN == i) {
                    dismiss()
                }
            }

            override fun onSlide(view: View, v: Float) {}
        })
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val myView: View = inflater.inflate(R.layout.bottomsheet_tablayout, container, false)
        val tabLayout = myView.findViewById<TabLayout>(R.id.myTabLayout)
        val back = myView.findViewById<ImageView>(R.id.cService)
        val image = myView.findViewById<ImageView>(R.id.img2)
        val name = myView.findViewById<TextView>(R.id.cServiceName)
        val airportName = myView.findViewById<TextView>(R.id.cServiceAirportName)
        val terminal = myView.findViewById<TextView>(R.id.cTerminal)

        for (a in 0 until ChooseServiceFragment.ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!!.size) {
            for (b in 0 until ChooseServiceFragment.ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![a].service_group!!.size) {
                for (c in 0 until ChooseServiceFragment.ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![a].service_group!![b].service_array.size){
                    if (ChooseServiceFragment.ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![a].service_group!![b].service_array[c].service_token == Utility.bottom) {
                        airportName.text =
                            ChooseServiceFragment.ticketServiceListData[ChooseServiceFragment.servicePosition!!].airport_code + " " + "Airport"
                        terminal.text = "-" + " " +
                                ChooseServiceFragment.ticketServiceListData[ChooseServiceFragment.servicePosition!!].terminal_name
                        name.text =
                            ChooseServiceFragment.ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![a].service_group!![b].sp_company_name
                        Glide.with(this)
                            .load(ChooseServiceFragment.ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![a].service_group!![b].sp_company_logo)
                            .into(image)
                    }
                }
            }
        }


        back.setOnDebounceListener {
            dismiss()

            createJson()
//            if (PackageServiceFragment.passengerStationData.isNotEmpty() && ChooseServiceFragment.ttr_token!!.isNotEmpty()) {
//                ChooseServiceFragment.data.clear()
//                ChooseServiceFragment.data.add(
//                    PassengerStationData(
//                        ChooseServiceFragment.ttr_token!!,
//                        PackageServiceFragment.passengerStationData
//                    )
//                )
//
//                val finalInput = JsonObject()
//                val stationarray = JsonArray()
//
//                for (a in 0 until ChooseServiceFragment.data.size) {
//                    val array = JsonArray()
//                    val obj = JsonObject()
//
//                    for (i in 0 until ChooseServiceFragment.data[a].stationData.size) {
//                        array.add(JsonObject().apply {
//                            addProperty(
//                                "service_token",
//                                ChooseServiceFragment.data[a].stationData[i].service_token
//                            )
//                            addProperty(
//                                "adult_count",
//                                ChooseServiceFragment.data[a].stationData[i].adult_count
//                            )
//                            addProperty(
//                                "children_count",
//                                ChooseServiceFragment.data[a].stationData[i].children_count
//                            )
//                            addProperty(
//                                "service_date",
//                                ChooseServiceFragment.data[a].stationData[i].service_date
//                            )
//                            addProperty(
//                                "service_time",
//                                ChooseServiceFragment.data[a].stationData[i].service_time
//                            )
//                            addProperty("notes", "")
//                        })
//                    }
//
//                    obj.addProperty(
//                        "ttr_token",
//                        ChooseServiceFragment.ticketServiceListData[ChooseServiceFragment.servicePosition!!].ttr_token
//                    )
//                    obj.add("service_array", array)
//                    stationarray.apply {
//                        add(obj)
//                    }
//
//                    Utility.stationarray.apply {
//                        add(obj)
//                    }
//                }
//
//                finalInput.addProperty(
//                    "user_token",
//                    SavedSharedPreference.getUserData(requireContext()).token
//                )
//                finalInput.add("station_array", stationarray)
//                Utility.finalInput = finalInput
//            }
        }

        viewPager = myView.findViewById(R.id.myViewPager)
        tabLayout.addTab(tabLayout.newTab().setText("Services"))
        tabLayout.addTab(tabLayout.newTab().setText("About"))
        tabLayout.addTab(tabLayout.newTab().setText("Reviews"))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        val adapter = BottomFragmentAdapter(childFragmentManager)
        viewPager.adapter = adapter
        ViewCompat.setNestedScrollingEnabled(viewPager, true)
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(dismissReceiver, IntentFilter("close-sheet"));
        return myView
    }



    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(dismissReceiver);
    }

    private val dismissReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            // Get extra data included in the Intent
            val message = intent.getStringExtra("message")
            if (message == "close_sheet"){
                dismissSheet()
            }
        }
    }

    private fun dismissSheet(){
        dismiss()
        createJson()
//        if (PackageServiceFragment.passengerStationData.isNotEmpty() && ChooseServiceFragment.ttr_token!!.isNotEmpty()) {
//            ChooseServiceFragment.data.clear()
//            ChooseServiceFragment.data.add(
//                PassengerStationData(
//                    ChooseServiceFragment.ttr_token!!,
//                    PackageServiceFragment.passengerStationData
//                )
//            )
//
//            val finalInput = JsonObject()
//            val stationarray = JsonArray()
//            Utility.stationarray = JsonArray()
//
//            for (data in PackageServiceFragment.selectedPassengerData){
//
//                val serviceArray = JsonArray()
//                val obj = JsonObject()
//
//                for (serviceData in data.stationData){
//                    serviceArray.add(JsonObject().apply {
//                        addProperty("service_token", serviceData.service_token)
//                        addProperty("adult_count", serviceData.adult_count)
//                        addProperty("children_count", serviceData.children_count)
//                        addProperty("service_date", serviceData.service_date)
//                        addProperty("service_time", serviceData.service_time)
//                        addProperty("notes", serviceData.notes)
//                    })
//                }
//                obj.add("service_array", serviceArray)
//                obj.addProperty("ttr_token", data.ttr_token)
//
//                stationarray.apply { add(obj) }
//                Utility.stationarray.apply { add(obj) }
//            }
//
//            finalInput.addProperty("user_token", SavedSharedPreference.getUserData(requireContext()).token)
//            finalInput.add("station_array", stationarray)
//            Utility.finalInput = finalInput
//
//
////            for (a in 0 until ChooseServiceFragment.data.size) {
////                val array = JsonArray()
////                val obj = JsonObject()
////
////                for (i in 0 until ChooseServiceFragment.data[a].stationData.size) {
////                    array.add(JsonObject().apply {
////                        addProperty(
////                            "service_token",
////                            ChooseServiceFragment.data[a].stationData[i].service_token
////                        )
////                        addProperty(
////                            "adult_count",
////                            ChooseServiceFragment.data[a].stationData[i].adult_count
////                        )
////                        addProperty(
////                            "children_count",
////                            ChooseServiceFragment.data[a].stationData[i].children_count
////                        )
////                        addProperty(
////                            "service_date",
////                            ChooseServiceFragment.data[a].stationData[i].service_date
////                        )
////                        addProperty(
////                            "service_time",
////                            ChooseServiceFragment.data[a].stationData[i].service_time
////                        )
////                        addProperty("notes", "")
////                    })
////                }
////                obj.addProperty(
////                    "ttr_token",
////                    ChooseServiceFragment.data[a].ttr_token
////                )
////                obj.add("service_array", array)
////                stationarray.apply {
////                    add(obj)
////                }
////
////                Utility.stationarray.apply {
////                    add(obj)
////                }
////            }
////
////            finalInput.addProperty(
////                "user_token",
////                SavedSharedPreference.getUserData(requireContext()).token
////            )
////            finalInput.add("station_array", stationarray)
////            Utility.finalInput = finalInput
//        }
    }


    private fun createJson(){
        if (PackageServiceFragment.passengerStationData.isNotEmpty() && ChooseServiceFragment.ttr_token!!.isNotEmpty()) {
            ChooseServiceFragment.data.clear()
            ChooseServiceFragment.data.add(
                PassengerStationData(
                    ChooseServiceFragment.ttr_token!!,
                    PackageServiceFragment.passengerStationData
                )
            )

            val finalInput = JsonObject()
            val stationarray = JsonArray()
            Utility.stationarray = JsonArray()

            for (data in PackageServiceFragment.selectedPassengerData){

                val serviceArray = JsonArray()
                val obj = JsonObject()

                for (serviceData in data.stationData){
                    serviceArray.add(JsonObject().apply {
                        addProperty("service_token", serviceData.service_token)
                        addProperty("adult_count", serviceData.adult_count)
                        addProperty("children_count", serviceData.children_count)
                        addProperty("service_date", serviceData.service_date)
                        addProperty("service_time", serviceData.service_time)
                        addProperty("notes", serviceData.notes)
                    })
                }
                obj.add("service_array", serviceArray)
                obj.addProperty("ttr_token", data.ttr_token)

                stationarray.apply { add(obj) }
                Utility.stationarray.apply { add(obj) }
            }
            Log.d("stationArray",Utility.stationarray.toString())

            finalInput.addProperty("user_token", SavedSharedPreference.getUserData(requireContext()).token)
            finalInput.add("station_array", stationarray)
            Utility.finalInput = finalInput

            Log.d("server_call", "createJson: ${Utility.finalInput}")
        }
    }



}