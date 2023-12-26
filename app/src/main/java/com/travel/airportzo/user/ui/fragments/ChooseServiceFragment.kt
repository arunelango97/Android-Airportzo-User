package com.travel.airportzo.user.ui.fragments

import android.annotation.SuppressLint
import android.content.*
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.bold
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.Navigation
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.travel.airportzo.user.R
import com.travel.airportzo.user.databinding.ChooseServiceFragmentBinding
import com.travel.airportzo.user.model.*
import com.travel.airportzo.user.network.ApiResult
import com.travel.airportzo.user.network.NoInternetActivity
import com.travel.airportzo.user.savedpreference.SavedSharedPreference
import com.travel.airportzo.user.utils.setOnDebounceListener
import com.travel.airportzo.user.ui.adapter.ChooseTicketAdapter
import com.travel.airportzo.user.ui.adapter.MainRecyclerviewAdapter
import com.travel.airportzo.user.ui.adapter.PackageAdapter
import com.travel.airportzo.user.ui.base.BaseFragment
import com.travel.airportzo.user.ui.bottomsheet.BottomSheet
import com.travel.airportzo.user.utils.Utility
import com.travel.airportzo.user.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


class ChooseServiceFragment : BaseFragment() {

    private var chooseFragment : ChooseServiceFragmentBinding? = null

    private val servicePackageData: ArrayList<ServiceTicketData.Service_collection> by lazy { arrayListOf() }
    private val ticketData: ArrayList<ServiceTicketData.Station_array> by lazy { arrayListOf() }
    private val packageData: ArrayList<ServiceTicketData.Service_group> by lazy { arrayListOf() }

    private var selectedItemPosition: Int = 0
    private var chooseTicketViewHolder: ChooseTicketAdapter.MyViewHolder? = null
    private val mainViewModel: MainViewModel by activityViewModels()





/** shared Pref*/
  /*  private val sharedPreferences: SharedPreferences by lazy {
        activity?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)!!
    }

    private val listener = SharedPreferences.OnSharedPreferenceChangeListener{ sharedPreferences,key ->
        Log.d("shareValue", key.toString())
        if (key == "key"){
            selectedItemPosition?.let {
                ticketData[it].journey_date = key
                chooseFragment.chooseServiceTicketList.adapter?.notifyItemRangeChanged(0,ticketData.size)
            }
        }

    }*/


    companion object {
        var ticketServiceListData = ArrayList<ServiceTicketData.Station_array>()
        var servicePosition: Int? = 0
        var ttr_token: String? = ""
        val data = arrayListOf<PassengerStationData>()

    }

    private val nextStationReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            // Get extra data included in the Intent
            val message = intent.getStringExtra("message")
            if (message == "next_station"){
                selectedAirport()
            }
        }
    }


    /* *//** broadcast receiver for date update*//*
    private val dateUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == BROADCAST_ACTION_UPDATE_DATE) {
                val newDate = intent.getStringExtra("date")
                selectedItemPosition?.let { position ->
                    if (newDate != null) {
                        ticketData[position].journey_date = newDate
                        Log.d("TAGY1", ticketData[position].journey_date)
                        chooseFragment.chooseServiceTicketList.adapter?.notifyItemChanged(position, data)
                    }

                }
            }
        }
    }*/



    override fun onDestroy() {
        /** Shared Pref*/
       /* sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)*/
        super.onDestroy()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(nextStationReceiver);

    /*    *//** broadcast receiver for date update*//*
        requireActivity().unregisterReceiver(dateUpdateReceiver)*/

    }

    private fun bottom() {
        val bottomSheet = BottomSheet()
        bottomSheet.isCancelable = false
        activity?.let { it1 -> bottomSheet.show(it1.supportFragmentManager, "TAG1") }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val flightData: ArrayList<SearchServiceData> =
            arguments?.getParcelableArrayList("directFlight")!!

        val directBoolean: Boolean = arguments?.getBoolean("directBoolean")!!

        val serviceToken: String = arguments?.getString("serviceToken")!!




        val jsonObject = JsonObject().apply {
            add("journey_array", Gson().toJsonTree(flightData))
            addProperty("has_specific_service", directBoolean)
            addProperty("service_token", serviceToken)
        }

        if (isNetworkConnected(requireContext())) {
            Log.d("server_call", "searchList: $jsonObject")
            lifecycleScope.launch {
            viewModel?.searchList(jsonObject = jsonObject)?.observe(requireActivity(), serviceCall)
            }
        } else {
            startActivity(Intent(requireContext(), NoInternetActivity::class.java))
        }

        chooseFragment?.header?.setOnDebounceListener {
            backPressed()
        }

        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(nextStationReceiver, IntentFilter("next-station"));

     /*   *//** broadcast receiver for date update*//*
        val filter = IntentFilter(BROADCAST_ACTION_UPDATE_DATE)
        requireActivity().registerReceiver(dateUpdateReceiver, filter)*/

/** shared Pref*/
//      sharedPreferences.registerOnSharedPreferenceChangeListener(listener)




    }


        @SuppressLint("SetTextI18n")
    private val serviceCall = Observer<ApiResult<ServiceTicketData>> { it ->
        when (it) {
            is ApiResult.Loading -> {
                loader.onChanged(it.loading)
            }
            is ApiResult.Success -> {
                ticketData.clear()
                ticketServiceListData.clear()
                ticketData.addAll(it.data.station_array)

                ticketServiceListData.addAll(it.data.station_array)

                val serviceSpannableString = SpannableStringBuilder()
                    .append("Services available on ")
                    .bold { append(ticketData[0].journey_date) }
                    .append(" (${ticketData[0].gmt_view}) at ")

                val serviceArrivalString = SpannableStringBuilder()
                    .bold { append("${ticketData[0].airport_code} (${ticketData[0].city_name})") }
                    .append("\n${ticketData[0].airport_name}-${ticketData[0].terminal_name}")

                chooseFragment?.serviceText?.text = serviceSpannableString
                chooseFragment?.chooseServiceArrivalName?.text = serviceArrivalString

//                chooseFragment.serviceText.text = "Services available on ${ticketData[0].journey_date} (${ticketData[0].gmt_view}) at "
//                chooseFragment.chooseServiceArrivalName.text = "${ticketData[0].airport_code} (${ticketData[0].city_name}) \n${ticketData[0].airport_name}-${ticketData[0].terminal_name}"


//                chooseFragment.chooseServiceArrivalName.text =
//                    ticketData[0].airport_code + " " + resources.getString(R.string.airport)
//                chooseFragment.chooseServiceTerminalName.text = ticketData[0].terminal_name
                if (ticketData[0].service_collection?.isNotEmpty()!!) {
                    packageData.clear()
                    servicePackageData.clear()
                }
                for (i in 0 until ticketData[0].service_collection!!.size) {
                    if (ticketData[0].service_collection!![i].service_type == "Bundle") {
                        packageData.addAll(ticketData[0].service_collection!![i].service_group!!)
                        chooseFragment?.packageList?.adapter = context?.let { it1 ->
                            PackageAdapter(it1, packageData, ::click)
                        }
                        Log.d("packagedata",packageData.toString())

                    } else if (ticketData[0].service_collection!![i].service_type == "Individual") {

                        servicePackageData.addAll(ticketData[0].service_collection!!)
                        chooseFragment?.meetList?.adapter = context?.let {
                            MainRecyclerviewAdapter(it, servicePackageData, ::clicked)
                        }
                    }
                }

              /*   Utility.dataList.addAll(ticketData)*/

                chooseFragment?.chooseServiceTicketList?.adapter = ChooseTicketAdapter(ticketData) { holder, position ->
                        chooseTicketViewHolder = holder
                        clickTicket(holder, position)
                    }
                chooseFragment?.location?.text =
                    ticketData.size.toString() + " " + resources.getString(R.string.locations)
            }

            is ApiResult.Error -> {
                MaterialAlertDialogBuilder(requireContext())
                    .setCancelable(false)
                    .setTitle("Alert")
                    .setMessage(it.message)
                    .setPositiveButton("Ok", DialogInterface.OnClickListener { dialog, which ->
                        backPressed()
                        dialog.dismiss()
                    })
                    .show()
            }
        }
    }

    private fun selectedAirport(){
        selectedItemPosition = selectedItemPosition!! + 1
        clickTicket(chooseTicketViewHolder!!, selectedItemPosition!! + 1)
        chooseFragment?.chooseServiceTicketList?.adapter?.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun clickTicket(holder: ChooseTicketAdapter.MyViewHolder, position: Int) {

        holder.itemView.setOnDebounceListener {
            selectedItemPosition = position
            Utility.datePosition = position
            Log.d("TAGY3",selectedItemPosition.toString() )

            chooseFragment?.chooseServiceTicketList?.adapter?.notifyDataSetChanged()
        }
        if (selectedItemPosition == position) {
            holder.chooseTicketAdapterBinding.sServiceView.visibility = View.VISIBLE
            holder.chooseTicketAdapterBinding.cServiceImage.visibility = View.VISIBLE
            holder.chooseTicketAdapterBinding.ticketView.backgroundColor = Color.WHITE
        } else {
            holder.chooseTicketAdapterBinding.sServiceView.visibility = View.GONE
            holder.chooseTicketAdapterBinding.cServiceImage.visibility = View.GONE
            holder.chooseTicketAdapterBinding.ticketView.backgroundColor =
                Color.parseColor("#9AE6EB")
        }

        if (position % 2 == 1) { // Check if position is odd
            // Rotate the image by 180 degrees
            holder.chooseTicketAdapterBinding.cServiceImage.rotation = 180f
        } else {
            // Reset rotation if the position is even
            holder.chooseTicketAdapterBinding.cServiceImage.rotation = 0f
        }
        chooseFragment?.next?.setOnDebounceListener {
            chooseFragment?.chooseServiceTicketList?.adapter?.notifyDataSetChanged()
            val quantity: Int
            val quantityValue = selectedItemPosition
            quantity = quantityValue.toInt() + 1
            selectedItemPosition = quantity
            chooseFragment?.chooseServiceTicketList?.layoutManager?.scrollToPosition(
                selectedItemPosition - 1
            )
        }
        if (selectedItemPosition == position) {
            packageData.clear()
            servicePackageData.clear()
            servicePosition = position
            ttr_token = ticketData[position].ttr_token

            Log.d("ttr_token_detect", "position: $position")
            Log.d("ttr_token_detect", "ttr_token: $ttr_token")

            val serviceSpannableString = SpannableStringBuilder()
                .append("Services available on ")
                .bold { append(ticketData[position].journey_date) }
                .append(" (${ticketData[position].gmt_view}) at ")
            Log.d("positionDate", "clickTicket: ${ticketData[position].journey_date.toString()} ")

            val serviceArrivalString = SpannableStringBuilder()
                .bold { append("${ticketData[position].airport_code} (${ticketData[position].city_name})") }
                .append("\n${ticketData[position].airport_name}-${ticketData[position].terminal_name}")

            chooseFragment?.serviceText?.text = serviceSpannableString
            chooseFragment?.chooseServiceArrivalName?.text = serviceArrivalString
//            chooseFragment.chooseServiceArrivalName.text = ticketData[position].airport_code + " " + resources.getString(R.string.airport)
//            chooseFragment.chooseServiceTerminalName.text = ticketData[position].terminal_name


            if (ticketData[position].service_collection!!.isNotEmpty()) {
                packageData.clear()
                for (i in 0 until ticketData[position].service_collection!!.size) {
                    if (ticketData[position].service_collection!![i].service_group!!.isNotEmpty()) {
                        if (ticketData[position].service_collection!![i].service_type == "Bundle") {
                            packageData.addAll(ticketData[position].service_collection!![i].service_group!!)
                            chooseFragment?.packageList?.adapter =
                                context?.let { it1 -> PackageAdapter(it1, packageData, ::click) }
                            chooseFragment?.meetList?.adapter = context?.let {
                                MainRecyclerviewAdapter(
                                    it, servicePackageData, ::clicked
                                )
                            }

                        } else if (ticketData[position].service_collection!![i].service_type == "Individual") {
                            chooseFragment?.packageList?.adapter =
                                context?.let { it1 -> PackageAdapter(it1, packageData, ::click) }
                            servicePackageData.clear()
                            servicePackageData.addAll(ticketData[position].service_collection!!)
                            chooseFragment?.meetList?.adapter = context?.let {
                                MainRecyclerviewAdapter(it, servicePackageData, ::clicked)
                            }
                        }
                    }
                }
                if (packageData.isEmpty()) {
                    chooseFragment?.chooseServiceBackgroundGift?.visibility = View.GONE
                } else {
                    chooseFragment?.chooseServiceBackgroundGift?.visibility = View.VISIBLE
                }
            } else {
                chooseFragment?.chooseServiceBackgroundGift?.visibility = View.GONE
            }
        } else {
            chooseFragment?.packageList?.adapter?.notifyDataSetChanged()
            chooseFragment?.meetList?.adapter?.notifyDataSetChanged()
        }

        val indexOfTTR = ticketData.indexOfFirst { it.ttr_token == ttr_token }

        if (ticketData.size - 1 == indexOfTTR){
            chooseFragment?.next?.visibility = View.GONE
        }else{
            chooseFragment?.next?.visibility = View.VISIBLE
        }
    }


    private fun clicked(serviceGroup: ServiceTicketData.Service_group) {
        Utility.bottom = ""
        Utility.business.clear()
        bottom()
//        Utility.bottom = serviceGroup.sp_company_token
        Log.d("sp_company_token", "clicked:${serviceGroup.sp_company_token.toString()} ")
        for (i in 0 until serviceGroup.business_names.size) {
            Utility.business.add(serviceGroup.business_names[i])
        }

        for (i in 0 until serviceGroup.service_array.size) {
            Utility.bottom = serviceGroup.service_array[i].service_token
            Log.d("manoj",Utility.bottom.toString())
            break
        }
    }

    private fun click(serviceGroup: ServiceTicketData.Service_group) {
        Utility.bottom = ""
        Utility.business.clear()
        bottom()
//        Utility.bottom = serviceGroup.sp_company_token

        for (i in 0 until serviceGroup.business_names.size) {
            Utility.business.add(serviceGroup.business_names[i])
        }

        for (i in 0 until serviceGroup.service_array.size) {
                Utility.bottom = serviceGroup.service_array[i].service_token
                Log.d("manoj",Utility.bottom.toString())
            break
        }



    }


    private fun backPressed() {
        Navigation.findNavController(requireView()).popBackStack()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment




        chooseFragment = ChooseServiceFragmentBinding.inflate(inflater, container, false)
        return chooseFragment?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /** brand Color */
        val colorValue = activity?.let { SavedSharedPreference.getCustomColor(it).brand_colour }
        val colorStateList = ColorStateList.valueOf(Color.parseColor(colorValue))
        chooseFragment?.next?.backgroundTintList = colorStateList


        /** view model*/
        mainViewModel.myDataLiveData.observe(viewLifecycleOwner, Observer { newDate ->

            ticketData[selectedItemPosition].journey_date = newDate.journey_date
            ticketServiceListData[servicePosition!!].journey_date = newDate.journey_date
//            chooseFragment?.chooseServiceTicketList?.adapter?.notifyDataSetChanged()
            Log.d("viewmodelJouneyDate", "journey_date updated to ${newDate.journey_date}")


        })


        chooseFragment?.header?.setOnClickListener{
            backPressed()
        }




    }
    fun callBackAdapter(){
        Log.d("callBackAdapter", "callBackAdapter")
        chooseFragment?.chooseServiceTicketList?.adapter = ChooseTicketAdapter(ticketData) { holder, position ->
            chooseTicketViewHolder = holder
            clickTicket(
                holder, position
            )
        }

    }




    /*  fun updateData(myData: String) {
          servicePosition?.let { position ->
              ticketServiceListData[position].journey_date = myData
              chooseFragment?.chooseServiceTicketList?.adapter?.notifyDataSetChanged()

          }
      }*/





}




