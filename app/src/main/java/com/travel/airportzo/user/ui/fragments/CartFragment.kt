package com.travel.airportzo.user.ui.fragments


import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.travel.airportzo.user.R
import com.travel.airportzo.user.databinding.CartFragmentBinding
import com.travel.airportzo.user.savedpreference.SavedSharedPreference
import com.travel.airportzo.user.ui.activity.MainActivity
import com.travel.airportzo.user.ui.base.BaseFragment
import com.travel.airportzo.user.utils.setOnDebounceListener


class CartFragment : BaseFragment() {

    private var cartFragment: CartFragmentBinding? = null

    private val flightName: ArrayList<String> by lazy { arrayListOf() }

    private var places: String? = ""



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



        cartFragment = CartFragmentBinding.inflate(inflater, container, false)

        /** brand color*/
        cartFragment?.textView4?.setBackgroundColor(Color.parseColor(activity?.let {
            SavedSharedPreference.getCustomColor(
                it
            ).brand_colour
        }))
        cartFragment?.nameView?.setBackgroundColor(Color.parseColor(activity?.let {
            SavedSharedPreference.getCustomColor(
                it
            ).brand_colour
        }))




        return cartFragment?.root
    }


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (PackageServiceFragment.passengerStationData.isEmpty()) {
            cartFragment?.card?.visibility = View.GONE
            cartFragment?.cartText?.visibility = View.VISIBLE
        } else {
            flightName.clear()
            cartFragment?.card?.visibility = View.VISIBLE
            for (a in 0 until ChooseServiceFragment.ticketServiceListData.size) {
                flightName.add(ChooseServiceFragment.ticketServiceListData[a].airport_code)
            }
            cartFragment?.aAirportList?.text = ""
            for (i in flightName) {
                if (flightName.size == 1) {
                    places = ""
                    cartFragment?.aAirportList?.text = i

                } else if (flightName.size >= 2) {
                    places = cartFragment?.aAirportList?.text.toString()
                    if (places!!.isEmpty()) {
                        places += i
                    } else {
                        places = "$places - $i"
                    }
                    cartFragment?.aAirportList?.text = places
                }
            }

            cartFragment?.dDateAndTime?.text = PackageServiceFragment.passengerStationData[0].service_date
            cartFragment?.services?.text =
                PackageServiceFragment.totalCount.size.toString() + " " + resources.getString(R.string.Service)
            ChooseServiceFragment.ticketServiceListData
            cartFragment?.pPassengerList?.text = PackageServiceFragment.passengerStationData[0].adult_count.toString() + " " + resources.getString(R.string.adults)
            cartFragment?.childrenList?.text = PackageServiceFragment.passengerStationData[0].children_count.toString() + " " + resources.getString(R.string.child)
            cartFragment?.aAmount?.text = "${getString(R.string.indianRupee)} ${PackageServiceFragment.total.sum().toString()}"
        }

        cartFragment?.layout?.setOnDebounceListener {
            findNavController().navigate(R.id.action_cart_to_SummaryFragment)
        }

        calculateTotalAmount()

        for (data in PackageServiceFragment.passengerStationData){
            Log.d("date_validator", "date | time : ${data.service_date} | ${data.service_time}")
        }







    }



    private fun calculateTotalAmount(){
        val total: ArrayList<Int> by lazy { arrayListOf() }

        for (passengerData in PackageServiceFragment.passengerStationData){
            for (serviceListData in ChooseServiceFragment.ticketServiceListData){
                for (serviceCollectionData in serviceListData.service_collection!!){
                    for (serviceGroup in serviceCollectionData.service_group!!){
                        for (serviceArray in serviceGroup.service_array){
                            if (passengerData.service_token == serviceArray.service_token){
                                val adult = serviceArray.price_adult.toInt()
                                var additionalAdult = 0
                                if (passengerData.adult_count > 1){
                                    additionalAdult = serviceArray.additional_price_adult.toInt() * (passengerData.adult_count - 1)
                                }
                                var child = 0
                                var additionalChild = 0
                                if (passengerData.children_count == 1){
                                    child = serviceArray.price_children.toInt()
                                }
                                if (passengerData.children_count > 1){
                                    child = serviceArray.price_children.toInt()
                                    additionalChild = serviceArray.additional_price_children.toInt() * (passengerData.children_count - 1)
                                }
                                total.add(adult+additionalAdult+child+additionalChild)
                            }
                        }
                    }
                }
            }
        }
        PackageServiceFragment.total.clear()
        for (data in total){
            PackageServiceFragment.total.add(data)
        }
        cartFragment?.aAmount?.text = "${getString(R.string.indianRupee)} ${total.sum().toString()}"

    }





}