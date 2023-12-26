package com.travel.airportzo.user.ui.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.TextView.BufferType
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.text.color
import androidx.fragment.app.activityViewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.travel.airportzo.user.R
import com.travel.airportzo.user.databinding.PackageServiceFragmentBinding
import com.travel.airportzo.user.model.PassengerStationData
import com.travel.airportzo.user.model.ServiceTicketData
import com.travel.airportzo.user.savedpreference.SavedSharedPreference
import com.travel.airportzo.user.ui.activity.MainActivity
import com.travel.airportzo.user.ui.adapter.CancelPolicyAdapter
import com.travel.airportzo.user.ui.adapter.PackageListAdapter
import com.travel.airportzo.user.ui.base.BaseFragment
import com.travel.airportzo.user.ui.fragments.ChooseServiceFragment.Companion.servicePosition
import com.travel.airportzo.user.ui.fragments.ChooseServiceFragment.Companion.ticketServiceListData
import com.travel.airportzo.user.utils.*
import com.travel.airportzo.user.utils.Utility.TAG
import com.travel.airportzo.user.viewmodel.MainViewModel
import com.travel.airportzo.user.webView.WebView
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class PackageServiceFragment : BaseFragment() {

    private val packageServiceFragment by lazy {
        PackageServiceFragmentBinding.inflate(
            layoutInflater
        )
    }

    private val mainViewModel: MainViewModel by activityViewModels()

    private var selectedPackageData: ArrayList<PassengerStationData.PassengerStationData> =
        arrayListOf()

    private val packageServiceData: ArrayList<ServiceTicketData.Service_array> by lazy { arrayListOf() }
    private val packageCancelData: ArrayList<ServiceTicketData.Cancellation_policy_detail> by lazy { arrayListOf() }
    private var adultCountTotal: Int = 0
    private var childCountTotal: Int = 0
    private var totalAdult: Int = 0
    private var totalChild: Int = 0


    private var countBadge: Int = 0
    var terms: String = ""
    private var policy: String = ""
    var cancel: String = ""
    private var gmt_view: String = ""
    private var packageName: String = ""
    private var packageToken: String = ""
    private var selectedCompanyToken: String = ""
    private var airportToken: String = ""
    var SERVICETIMING: Date? = null
    var passengerLastTime : String = ""
    var passengerLastDate : String = ""

    var DepatureDate : String =""


    companion object {
        val passengerStationData: ArrayList<PassengerStationData.PassengerStationData> by lazy { arrayListOf() }
        val selectedPassengerData: ArrayList<PassengerStationData> by lazy { arrayListOf() }
        val total: ArrayList<Int> by lazy { arrayListOf() }
        val adultTotalCount: ArrayList<Int> by lazy { arrayListOf() }
        val childTotalCount: ArrayList<Int> by lazy { arrayListOf() }
        val totalPackage: ArrayList<Int> by lazy { arrayListOf() }
        val totalCount: ArrayList<Int> = arrayListOf()

    }

    private var passengerStationDataObject: PassengerStationData.PassengerStationData? = null
    private var selectedServiceToken: String? = null

    private val packageListAdapter by lazy {
        PackageListAdapter(
            requireContext(),
            packageServiceData,
            ::choosePackageClick,
            ::removePackage,
        )
    }

    private val cancelPolicyAdapter by lazy { CancelPolicyAdapter(requireContext(), packageCancelData) }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        /** brand color */
        packageServiceFragment.addToCart.setBackgroundColor(Color.parseColor(activity?.let {
            SavedSharedPreference.getCustomColor(
                it
            ).brand_colour
        }))

        packageServiceFragment.nextStation.setBackgroundColor(Color.parseColor(activity?.let {
            SavedSharedPreference.getCustomColor(
                it
            ).brand_colour
        }))






//        passengerStationData.clear()
//        for(i in 0 until passengerStationData.size){
//            if ()
//            packageServiceFragment.Servicechildcountshow.text= passengerStationData[i].children_count.toString()
//
//        }

//        packageServiceFragment.aServiceterms.setOnDebounceListener {
//            startActivity(
//                Intent(requireContext(), WebView::class.java)
//                    .putExtra("URL", terms)
//            )
//        }
//
//
//        packageServiceFragment.aServicepolicy.setOnDebounceListener {
//            startActivity(
//                Intent(requireContext(), WebView::class.java)
//                    .putExtra("URL", policy)
//            )
//        }
//
//
//        packageServiceFragment.aServicecancel.setOnDebounceListener {
//            startActivity(
//                Intent(requireContext(), WebView::class.java)
//                    .putExtra("URL", cancel)
//            )
//        }


//        packageServiceFragment.aServiceterms.setOnDebounceListener {
//            findNavController().navigate(R.id.action_packageServiceFragment_to_termandconditionFragment, bundleOf("terms" to terms))
//        }
//
//        packageServiceFragment.aServicepolicy.setOnDebounceListener {
//            findNavController().navigate(R.id.action_packageServiceFragment_to_privacypolicyFragment,bundleOf("policy" to policy))
//        }
//
//        packageServiceFragment.aServicecancel.setOnDebounceListener {
//            findNavController().navigate(R.id.action_packageServiceFragment_to_cancelpolicyFragment,bundleOf("cancel" to cancel))
//        }

        packageServiceFragment.ServiceAdultCountShow.addTextChangedListener(textWatcherListener)
        packageServiceFragment.cServiceChildCountShow.addTextChangedListener(textWatcherListener)

        for (a in 0 until ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!!.size) {
            if (ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![a].service_type == "Bundle") {
                for (b in 0 until ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![a].service_group!!.size) {
                    for (c in 0 until ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![a].service_group!![b].service_array.size) {
                        if (ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![a].service_group!![b].service_array[c].service_token == Utility.bottom) {
                            packageServiceData.clear()
                            ChooseServiceFragment.servicePosition?.let {
                                ticketServiceListData[it].service_collection!![a].service_group!![b].service_array
                            }?.let {
                                packageServiceData.addAll(it)
                                selectedCompanyToken = it[0].sp_company_token
                            }
                            Log.d("packageServiceData",packageServiceData.toString())

                            terms =
                                ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![a].service_group!![b].terms_conditions
                            policy =
                                ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![a].service_group!![b].privacy_policy
                            cancel =
                                ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![a].service_group!![b].cancellation_policy
                            packageName =
                                ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![a].service_group!![b].sp_company_name
                            packageServiceFragment.cServicePackagesRecyclerview.adapter = packageListAdapter

                        }
                    }
                }
            } else {
                for (b in 0 until ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![a].service_group!!.size) {
                    for (c in 0 until ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![a].service_group!![b].service_array.size) {
                        if (ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![a].service_group!![b].service_array[c].service_token == Utility.bottom &&
                            ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![a].service_group!![b].service_array[c].business_names == Utility.business
                        ) {
                            packageServiceData.clear()
                            ChooseServiceFragment.servicePosition?.let {
                                ticketServiceListData[it].service_collection!![a].service_group!![b].service_array
                            }?.let {
                                packageServiceData.addAll(it)
                                selectedCompanyToken = it[0].sp_company_token
                            }
                            Log.d("packageServiceData",packageServiceData.toString())
                            terms =
                                ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![a].service_group!![b].terms_conditions
                            policy =
                                ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![a].service_group!![b].privacy_policy
                            cancel =
                                ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![a].service_group!![b].cancellation_policy
                            packageName =
                                ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![a].service_group!![b].sp_company_name
                            packageServiceFragment.cServicePackagesRecyclerview.adapter = packageListAdapter
                        }
                    }
                }
            }
        }



        for (i in 0 until ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!!.size) {
            for (j in 0 until ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![i].service_group!!.size) {
                for (k in 0 until ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![i].service_group!![j].cancellation_policy_detail.size) {
                    if (ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![i].service_group!![j].cancellation_policy_detail.isNotEmpty()) {
                        for (l in 0 until ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![i].service_group!![j].service_array.size) {
                            if (ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![i].service_group!![j].service_array[l].service_token == Utility.bottom &&
                                    ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![i].service_group!![j].service_array[l].business_names == Utility.business
                            ) {

                                ChooseServiceFragment.servicePosition?.let {
                                    ticketServiceListData[it].service_collection!![i].service_group!![j].cancellation_policy_detail[k]
                                }?.let { packageCancelData.add(it) }
                                packageServiceFragment.cancelRecycler.adapter = cancelPolicyAdapter
                            }
                            Log.d("packageServiceData", packageCancelData.toString())
                        }
                    }
                }
            }
        }



        for (i in 0 until ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!!.size) {
            for (j in 0 until ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![i].service_group!!.size) {
                for (k in 0 until ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![i].service_group!![j].cancellation_policy_detail.size) {
                    if (ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![i].service_group!![j].reschedule_policy.isNotEmpty()) {
                        for (l in 0 until ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![i].service_group!![j].service_array.size){
                            if (ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![i].service_group!![j].service_array[l].service_token == Utility.bottom &&
                                ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![i].service_group!![j].service_array[l].business_names == Utility.business
                            ) {
                                packageServiceFragment.rescheduleText.text = ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![i].service_group!![j].reschedule_policy
                            }
                        }
                    }
                }
            }
        }


        for (i in 0 until ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!!.size) {
            for (j in 0 until ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![i].service_group!!.size) {

                Log.d("packageDate",  ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![i].service_group!!.toString())

                for (k in 0 until ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![i].service_group!![j].service_array.size) {
                    packageToken =
                        ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![i].service_group!![j].service_array[k].service_token
//                    if (PackageServiceFragment.passengerStationData.any { it.service_token == packagetoken } && PackageServiceFragment.passengerStationData.any { it.sp_company_token == selectedCompanyToken })

                    if (passengerStationData.indexOfFirst {
                            it.service_token === packageToken && it.sp_company_token === selectedCompanyToken
                        } >= 0) {
                        val index = passengerStationData.indexOfFirst {
                            it.service_token === packageToken && it.sp_company_token === selectedCompanyToken
                        }

                        selectedPackageData = arrayListOf(passengerStationData[index])
                        packageServiceFragment.ServiceAdultCountShow.text =
                            selectedPackageData[0].adult_count.toString()
                        packageServiceFragment.cServiceChildCountShow.text =
                            selectedPackageData[0].children_count.toString()
                        packageServiceFragment.cServiceDatePickerEditText.text =
                            selectedPackageData[0].service_date
                        DepatureDate = selectedPackageData[0].service_date
                        packageServiceFragment.cServiceTimeEdittext.text =
                            selectedPackageData[0].service_time + " " + gmt_view

                        Log.d("selectedPackageDate", "1 : ${packageServiceFragment.cServiceTimeEdittext.text.toString()}")

                        break
                    }
                }
            }
        }

        gmt_view = "(${ticketServiceListData.get(ChooseServiceFragment.servicePosition!!).gmt_view})"
        Log.d("gmtView",gmt_view.toString() )

        customTextView(packageServiceFragment.termsTextView)



        packageServiceFragment.addToCart.setOnDebounceListener {

            val newDate = packageServiceFragment.cServiceDatePickerEditText.text.toString()
            ticketServiceListData[servicePosition!!].journey_date = newDate

            val currentStationArray = mainViewModel.myDataLiveData.value
            val updatedStationArray = currentStationArray?.copy(journey_date = newDate)

            if (updatedStationArray != null) {
                mainViewModel.updateMyData(updatedStationArray)
            }



            if (selectedPackageData.isEmpty()) {
                addToCart()
            }

            else if (SERVICETIMING!!.after(Utility.FLIGHTDATE)) {

                addToCart()

            } else if (SERVICETIMING!! == Utility.FLIGHTDATE) {
                addToCart()
            } else {
                alertToast(requireContext(), "Service timing must be after the Flight timing")
            }


        }




        packageServiceFragment.removeFromCart.setOnDebounceListener { removeFromCart() }
        packageServiceFragment.nextStation.setOnDebounceListener {
            nextStation()
            val intent = Intent("close-sheet")
            intent.putExtra("message", "close_sheet")
            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
        }

        packageServiceFragment.checkout.setOnDebounceListener {
            checkOut()
            val intent = Intent("close-sheet")
            intent.putExtra("message", "close_sheet")
            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
        }

        packageServiceFragment.cServiceAdultCountAdd.setOnClickListener {

            // add the number of adult to price of packageServiceData

            val quantity: Int
            val quantityValue = packageServiceFragment.ServiceAdultCountShow.text.toString()
            quantity = quantityValue.toInt() + 1

//            if (passengerStationData.isEmpty()) {
//                packageServiceFragment.ServiceAdultCountShow.text = quantity.toString()
//            } else {
//                val adultCount = passengerStationData[0].adult_count
//                if (quantity <= adultCount) {
//                    packageServiceFragment.ServiceAdultCountShow.text = quantity.toString()
//                } else {
//                    alertToast(
//                        context = requireContext(),
//                        message = "Adult count cannot be higher."
//                    )
//                }
//            }

            packageServiceFragment.ServiceAdultCountShow.text = quantity.toString()

            for (data in passengerStationData){
                data.adult_count = packageServiceFragment.ServiceAdultCountShow.text.toString().toInt()
            }
        }

        packageServiceFragment.cServiceAdultCountMinus.setOnClickListener {

            if (packageServiceFragment.ServiceAdultCountShow.text.toString() != "1"){
                val quantity: Int
                val quantityValue = packageServiceFragment.ServiceAdultCountShow.text.toString()
                if (quantityValue <= "1") {
                    packageServiceFragment.ServiceAdultCountShow.text = "0"
                } else {
                    quantity = quantityValue.toInt() - 1
                    packageServiceFragment.ServiceAdultCountShow.text = quantity.toString()
                }

                for (data in passengerStationData){
                    data.adult_count = packageServiceFragment.ServiceAdultCountShow.text.toString().toInt()
                }
            }
        }

        packageServiceFragment.cServiceChildCountMinus.setOnClickListener {

            val quantity: Int
            val quantityValue = packageServiceFragment.cServiceChildCountShow.text.toString()
            if (quantityValue <= "1") {
                packageServiceFragment.cServiceChildCountShow.text = "0"
            } else {
                quantity = quantityValue.toInt() - 1
                packageServiceFragment.cServiceChildCountShow.text = quantity.toString()
            }

            for (data in passengerStationData){
                data.children_count = packageServiceFragment.cServiceChildCountShow.text.toString().toInt()
            }

        }

        packageServiceFragment.cServiceChildCountAdd.setOnClickListener {
            val quantity: Int
            val quantityValue = packageServiceFragment.cServiceChildCountShow.text.toString()
            quantity = quantityValue.toInt() + 1

//            if (passengerStationData.isEmpty()) {
//                packageServiceFragment.cServiceChildCountShow.text = quantity.toString()
//            } else {
//                val childCount = passengerStationData[0].children_count
//                if (quantity <= childCount) {
//                    packageServiceFragment.cServiceChildCountShow.text = quantity.toString()
//                } else {
//                    alertToast(
//                        context = requireContext(),
//                        message = "Child count cannot be higher."
//                    )
//                }
//            }

            packageServiceFragment.cServiceChildCountShow.text = quantity.toString()

            for (data in passengerStationData){
                data.children_count = packageServiceFragment.cServiceChildCountShow.text.toString().toInt()
            }
        }


        val calendar: Calendar = Calendar.getInstance()
        val simpleDateFormat = SimpleDateFormat("dd MMM yyyy")

//        if (selectedPackageData.isEmpty()) {
//            val date = simpleDateFormat.format(calendar.time)
//            val hour = calendar.get(Calendar.HOUR_OF_DAY)
//            val minute = calendar.get(Calendar.MINUTE)
//            val amPm: String = if (hour >= 12) {
//                "PM"
//            } else {
//                "AM"
//            }
//
//            SERVICETIMING = Utility.FLIGHTDATE!!
////            packageServiceFragment.cServiceDatePickerEditText.text = date
//            packageServiceFragment.cServiceDatePickerEditText.text =
//                SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Utility.FLIGHTDATE!!)
//                    .toString()
//            packageServiceFragment.cServiceTimeEdittext.text =
//                SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Utility.FLIGHTDATE!!)
//                    .toString().uppercase() + " " + gmt_view
////            packageServiceFragment.cServiceTimeEdittext.text =
////                String.format(
////                    "%02d:%02d  ",
////                    hour,
////                    minute
////                )
//
//
//        } else {
//            val date =
//                simpleDateFormat.format(simpleDateFormat.parse(selectedPackageData[0].service_date)!!)
//
//            val serviceDate = selectedPackageData[0].service_date
//            val serviceTime = selectedPackageData[0].service_time
//
//            val dateString = serviceDate.split(" ")
//            val timeString = serviceTime.split(":")
//
//            SERVICETIMING = Calendar.getInstance().apply {
//                set(Calendar.YEAR, dateString[2].toInt())
//                set(Calendar.MONTH, getMonth(dateString[1]))
//                set(Calendar.DAY_OF_MONTH, dateString[0].toInt())
//                set(
//                    Calendar.MINUTE,
//                    timeString[1].replace("PM", "").replace("AM", "").trim().toInt()
//                )
//                set(Calendar.HOUR, timeString[0].toInt())
//            }.time
//
//            packageServiceFragment.cServiceDatePickerEditText.text = date
//        }


//        val current = LocalDateTime.now()
//        val formatter = DateTimeFormatter.ofPattern("HH:mm")
//        val formatted = current.format(formatter)
//        packageServiceFragment.cServicetimeedittext.text =
//            formatted +




        packageServiceFragment.cServiceDatePickerEditText.setOnDebounceListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                requireContext(), { _, year, monthOfYear, dayOfMonth ->
                    val dateType = "" + year + "-" + (monthOfYear + 1) + "-" + dayOfMonth
                    packageServiceFragment.cServiceDatePickerEditText.setOnDebounceListener {
                        val calendar = Calendar.getInstance()
                        val year = calendar.get(Calendar.YEAR)
                        val month = calendar.get(Calendar.MONTH)
                        val day = calendar.get(Calendar.DAY_OF_MONTH)

                        val datePickerDialog = DatePickerDialog(
                            requireContext(), { _, year, monthOfYear, dayOfMonth ->
                                val dateType = "" + year + "-" + (monthOfYear + 1) + "-" + dayOfMonth
                                val parser = SimpleDateFormat("yyyy-MM-dd")
                                val formatter = SimpleDateFormat("dd-MMM-yyyy")
                                val output = formatter.format(parser.parse(dateType))
                                packageServiceFragment.cServiceDatePickerEditText.text = output
                                passengerLastDate =  packageServiceFragment.cServiceDatePickerEditText.text.toString()

                                val selectedTime = Calendar.getInstance().apply {
                                    set(
                                        Calendar.HOUR_OF_DAY,
                                        SimpleDateFormat("hh", Locale.getDefault()).format(SERVICETIMING!!)
                                            .toInt()
                                    )
                                    set(
                                        Calendar.MINUTE,
                                        SimpleDateFormat("mm", Locale.getDefault()).format(SERVICETIMING!!)
                                            .toInt()
                                    )
                                    set(Calendar.YEAR, year)
                                    set(Calendar.MONTH, monthOfYear)
                                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                                }.time

                                SERVICETIMING = selectedTime
                                Log.d("gmtView", "dateSetOnclick: ${SERVICETIMING.toString()} ")

                            }, year, month, day
                        )
//                        Utility.departureDate = "23-Dec-2023"
//                        val dateFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.US)
//
//                        try {
//                            val departureDate = dateFormat.parse(Utility.departureDate)
//
//                            // Set the minimum date for the DatePickerDialog
//                            departureDate?.let {
//                                datePickerDialog.datePicker.minDate = it.time
//                            }
//                        } catch (e: ParseException) {
//                            e.printStackTrace()
//                        }
                        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
                        if (SERVICETIMING != null) {
                            val calendar = Calendar.getInstance()
                            calendar.time = SERVICETIMING!!

                            Log.d("DatePicker", "Setting SERVICETIMING date: ${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.DAY_OF_MONTH)}")

                            datePickerDialog.datePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
                        } else {
                            Log.e("DatePicker", "SERVICETIMING is null.")
                        }
//                        datePickerDialog.datePicker.minDate = SERVICETIMING!!.time
                        Log.d("gmtView", "dateOnclick: ${SERVICETIMING!!.time.toString()} ")
                        datePickerDialog.show()
                    }
                    val parser = SimpleDateFormat("yyyy-MM-dd")
                    val formatter = SimpleDateFormat("dd-MMM-yyyy")
                    val output = formatter.format(parser.parse(dateType))
                    packageServiceFragment.cServiceDatePickerEditText.text = output
                    passengerLastDate =  packageServiceFragment.cServiceDatePickerEditText.text.toString()

                    val selectedTime = Calendar.getInstance().apply {
                        set(
                            Calendar.HOUR_OF_DAY,
                            SimpleDateFormat("hh", Locale.getDefault()).format(SERVICETIMING!!)
                                .toInt()
                        )
                        set(
                            Calendar.MINUTE,
                            SimpleDateFormat("mm", Locale.getDefault()).format(SERVICETIMING!!)
                                .toInt()
                        )
                        set(Calendar.YEAR, year)
                        set(Calendar.MONTH, monthOfYear)
                        set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    }.time

                    SERVICETIMING = selectedTime
                    Log.d("gmtView", "dateSetOnclick: ${SERVICETIMING.toString()} ")

                }, year, month, day
            )
//            val dateFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.US)
//
//            try {
//                val departureDate = dateFormat.parse(Utility.departureDate)
//
//                // Set the minimum date for the DatePickerDialog
//                departureDate?.let {
//                    datePickerDialog.datePicker.minDate = it.time
//                }
//            } catch (e: ParseException) {
//                e.printStackTrace()
//            }
            datePickerDialog.datePicker.minDate = System.currentTimeMillis()
// Update the date picker with the SERVICETIMING date
            if (SERVICETIMING != null) {
                val calendar = Calendar.getInstance()
                calendar.time = SERVICETIMING!!

                Log.d("DatePicker", "Setting SERVICETIMING date: ${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.DAY_OF_MONTH)}")

                datePickerDialog.datePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
            } else {
                Log.e("DatePicker", "SERVICETIMING is null.")
            }
//            calendar.time = SERVICETIMING!!
//            datePickerDialog.datePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

//            datePickerDialog.datePicker.minDate = SERVICETIMING!!.time
            Log.d("gmtView", "dateOnclick: ${SERVICETIMING!!.time.toString()} ")
            datePickerDialog.show()
        }

        packageServiceFragment.cServiceTimeEdittext.setOnDebounceListener {
            val mTimePicker: TimePickerDialog
            val mCurrentTime = Calendar.getInstance()
            val hour = mCurrentTime.get(Calendar.HOUR_OF_DAY)
            val minute = mCurrentTime.get(Calendar.MINUTE)
            mTimePicker = TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    val amPm: String = if (hourOfDay >= 12) {
                        "PM"
                    } else {
                        "AM"
                    }

                    val selectedTime = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, hourOfDay)
                        set(Calendar.MINUTE, minute)
                        set(
                            Calendar.YEAR,
                            SimpleDateFormat("yyyy", Locale.getDefault()).format(SERVICETIMING!!)
                                .toInt()
                        )
                        set(
                            Calendar.MONTH,
                            getMonth(
                                SimpleDateFormat("MMM", Locale.getDefault()).format(SERVICETIMING!!)
                            )
                        )
                        set(
                            Calendar.DAY_OF_MONTH,
                            SimpleDateFormat("dd", Locale.getDefault()).format(SERVICETIMING!!)
                                .toInt()
                        )
                    }.time

                    packageServiceFragment.cServiceTimeEdittext.text =
                        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(selectedTime)
                            .toString().uppercase() + " " + gmt_view
                    passengerLastTime =  packageServiceFragment.cServiceTimeEdittext.text.toString()
                    Log.d("selectedPackageDate", "2 : ${passengerLastTime.toString()}")

                    SERVICETIMING = selectedTime
                    Log.d("gmtView", "timeSetOnclick: ${SERVICETIMING.toString()} ")

                }, hour, minute, false
            )
            mTimePicker.show()
        }

        for (data in packageServiceData) {
            if (data.isClicked) {
                selectedServiceToken = data.service_token

                val adultCount = packageServiceFragment.ServiceAdultCountShow.text.toString()
                val childCount = packageServiceFragment.cServiceChildCountShow.text.toString()
                val date = packageServiceFragment.cServiceDatePickerEditText.text.toString()
                Log.d("selectedServiceToken", date.toString())
                val time = packageServiceFragment.cServiceTimeEdittext.text.toString()
                    .replace(gmt_view, "")
                Log.d("selectedPackageDate", "3 : ${packageServiceFragment.cServiceTimeEdittext.text.toString()}")


                passengerStationDataObject = PassengerStationData.PassengerStationData(
                    service_token = data.service_token,
                    unique_business_token = data.unique_business_token,
                    adult_count = adultCount.toInt(),
                    children_count = childCount.toInt(),
                    service_date = date,
                    service_time = time,
                    sp_company_token = data.sp_company_token,
                    notes = ""
                )

                break

            }
        }

        if (passengerStationData.isNotEmpty()) {

            var isAvailableInCart = false

            for (data in selectedPackageData){
                if (selectedCompanyToken == data.sp_company_token){
                    packageServiceFragment.cServiceDatePickerEditText.text =
                        data.service_date
                    packageServiceFragment.cServiceTimeEdittext.text =
                        data.service_time + " " + gmt_view
                    Log.d("selectedPackageDate", "4 : ${packageServiceFragment.cServiceTimeEdittext.text.toString()}")
                }
            }


            packageToken
            selectedPassengerData

//            if (selectedPassengerData.isEmpty()){
//                isAvailableInCart = false
//            }else{



            for (data in packageServiceData) {
                if (data.isClicked) {
                    isAvailableInCart = data.isClicked
                    break
                }
            }

            if (isAvailableInCart) {


                packageServiceFragment.termsCheckbox.isChecked = true
                packageServiceFragment.addToCart.visibility = View.GONE
                packageServiceFragment.removeFromCart.visibility = View.VISIBLE
                /** brand color */
                packageServiceFragment.removeFromCart.setBackgroundColor(Color.parseColor(activity?.let {
                    SavedSharedPreference.getCustomColor(
                        it
                    ).brand_colour
                }))


                packageServiceFragment.checkout.visibility = View.VISIBLE
                packageServiceFragment.nextStation.visibility = View.VISIBLE

            }
//            val isCurrentCompanyTokenAvailable =
//                passengerStationData.any { data -> data.sp_company_token == selectedCompanyToken }
//            if (isCurrentCompanyTokenAvailable) {
//                packageServiceFragment.addToCart.visibility = View.GONE
//                packageServiceFragment.removeFromCart.visibility = View.VISIBLE
//                packageServiceFragment.checkout.visibility = View.VISIBLE
//                packageServiceFragment.nextStation.visibility = View.VISIBLE
//            }
        }

        if (packageServiceData.isEmpty()) {
            packageServiceFragment.bottomButtonLayout.visibility = View.GONE
        }

        val selectedTTR = getCurrentTTR(selectedCompanyToken)
        val currentServiceIndex =
            ticketServiceListData.indexOfFirst { it.ttr_token == selectedTTR }
        try {
            if (ticketServiceListData[currentServiceIndex + 1].service_collection!!.isEmpty()) {
                packageServiceFragment.nextStation.visibility = View.GONE
            } else {
                packageServiceFragment.nextStation.visibility = View.VISIBLE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (ChooseServiceFragment.servicePosition == ticketServiceListData.size - 1) {
            packageServiceFragment.nextStation.visibility = View.GONE
        }

        if (passengerStationData.isEmpty()){
            packageServiceFragment.ServiceAdultCountShow.text = "1"
            packageServiceFragment.cServiceChildCountShow.text = "0"
        }else{
            packageServiceFragment.ServiceAdultCountShow.text = passengerStationData[0].adult_count.toString()
            packageServiceFragment.cServiceChildCountShow.text = passengerStationData[0].children_count.toString()
        }

        if (selectedPassengerData.isNotEmpty()){

            val currentStationIndex = selectedPassengerData.indexOfFirst { it.ttr_token == ChooseServiceFragment.ttr_token }

            if (currentStationIndex == -1){
//            if (selectedPassengerData[selectedPassengerData.size - 1].stationData.isNotEmpty()){
                val mandatoryCalendar: Calendar = getCalenderData(
                    serviceDate = selectedPassengerData[selectedPassengerData.size - 1].stationData[0].service_date,
                    serviceTime = selectedPassengerData[selectedPassengerData.size - 1].stationData[0].service_time,
                    addDays = 0
                )
                packageServiceFragment.cServiceDatePickerEditText.text =
                    SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(mandatoryCalendar.time)
                        .toString()
                Log.d("selectedPassengerData",  packageServiceFragment.cServiceDatePickerEditText.text.toString())
                packageServiceFragment.cServiceTimeEdittext.text =
                    SimpleDateFormat("hh:mm a", Locale.getDefault()).format(mandatoryCalendar.time)
                        .toString().uppercase() + " " + gmt_view
                Log.d("selectedPackageDate", "5 : ${packageServiceFragment.cServiceTimeEdittext.text.toString()}")
//            }
            }else{

                val mandatoryCalendar: Calendar = getCalenderData(
                    serviceDate = selectedPassengerData[currentStationIndex].stationData[0].service_date,
                    serviceTime = selectedPassengerData[currentStationIndex].stationData[0].service_time,
                    addDays = 0
                )

                packageServiceFragment.cServiceDatePickerEditText.text =
                    SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(mandatoryCalendar.time)
                        .toString()
                packageServiceFragment.cServiceTimeEdittext.text =
                    SimpleDateFormat("hh:mm a", Locale.getDefault()).format(mandatoryCalendar.time)
                        .toString().uppercase() + " " + gmt_view
                Log.d("selectedPackageDate", "6 : ${packageServiceFragment.cServiceTimeEdittext.text.toString()}")
            }


        }

        packageCancelData.add(ServiceTicketData.Cancellation_policy_detail("0","0"))

        packageServiceFragment.cancelRecycler.adapter = cancelPolicyAdapter

        val serviceDate = requireContext().getGMTDate(gmt_view.replace("(","").replace(")",""))
        val serviceTime = requireContext().getGMTTime(gmt_view.replace("(","").replace(")",""))

        Log.d("serviceDate", requireContext().getGMTDate(gmt_view.replace("(","").replace(")","")).toString())

        val dateString = serviceDate.split("-")
        val timeString = serviceTime.split(":")

        SERVICETIMING = Calendar.getInstance().apply {
            set(Calendar.YEAR, dateString[2].toInt())
            set(Calendar.MONTH, getMonth(dateString[1]))
            set(Calendar.DAY_OF_MONTH, dateString[0].toInt()+1)
            set(
                Calendar.MINUTE,
                timeString[1].replace("PM", "").replace("AM", "").trim().toInt()
            )
            set(Calendar.HOUR, timeString[0].toInt())
        }.time

        if (selectedPackageData.isEmpty()){
            Utility.FLIGHTDATE = SERVICETIMING
        }
        /** date and time set in textview*/
        chooseServiceDate()


            for (data in packageServiceData) {
                data.service_date = packageServiceFragment.cServiceDatePickerEditText.text.toString()
                data.service_time = packageServiceFragment.cServiceTimeEdittext.text.toString()
            }






//        packageServiceFragment.cServiceDatePickerEditText.text = Utility.dateTime
        // packageServiceFragment.cServiceDatePickerEditText.text = requireContext().getGMTDate(gmt_view.replace("(","").replace(")",""))
        // packageServiceFragment.cServiceDatePickerEditText.text = Utility.departureDate
        //packageServiceFragment.cServiceTimeEdittext.text = "${requireContext().getGMTTime(gmt_view.replace("(","").replace(")",""))} $gmt_view"
        passengerLastTime = packageServiceFragment.cServiceTimeEdittext.text.toString()
        passengerLastDate = packageServiceFragment.cServiceDatePickerEditText.text.toString()
        Log.d("flightTime", "onCreate 1: ${passengerLastTime.toString()}")

    }
    private fun chooseServiceDate(){
        for (i in 0 until ticketServiceListData.size){
            packageServiceFragment.cServiceDatePickerEditText.text = ticketServiceListData[i].journey_date
            packageServiceFragment.cServiceTimeEdittext.text = "${requireContext().getGMTTime(gmt_view.replace("(","").replace(")",""))} $gmt_view"

        }
    }

    private val textWatcherListener = object : TextWatcher{
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            calculatePackagePrice()
        }

    }

    private fun calculatePackagePrice(){
        val adultCount = packageServiceFragment.ServiceAdultCountShow.text.toString().toInt()
        val childCount = packageServiceFragment.cServiceChildCountShow.text.toString().toInt()

        for (data in packageServiceData){
            val firstAdult = data.price_adult.toInt() * 1
            val extraAdult = data.additional_price_adult.toInt() * (adultCount - 1)

            var firstChild = 0
            var extraChild = 0
            if (childCount != 0){
                firstChild = data.price_children.toInt() * 1
                extraChild = data.additional_price_children.toInt() * (childCount - 1)
            }

            val totalAdultPrice = firstAdult + extraAdult
            val totalChildPrice = firstChild + extraChild
            data.totalAmount = totalAdultPrice + totalChildPrice
        }

        packageServiceFragment.cServicePackagesRecyclerview.adapter = packageListAdapter
    }

    private fun getCurrentTTR(selectedCompanyToken: String): String {
        var ttrToken = ""

        outer@ for (data in ticketServiceListData) {
            ttrToken = data.ttr_token
            for (collection in data.service_collection!!) {
                for (group in collection.service_group!!) {
                    if (selectedCompanyToken == group.sp_company_token) {
                        break@outer
                    }
                }
            }
        }

        return ttrToken
    }

    private fun getMonth(month: String): Int {
        return when (month) {
            "Jan" -> 0
            "Feb" -> 1
            "Mar" -> 2
            "Apr" -> 3
            "May" -> 4
            "Jun" -> 5
            "Jul" -> 6
            "Aug" -> 7
            "Sep" -> 8
            "Oct" -> 9
            "Nov" -> 10
            else -> 11
        }
    }

    private fun nextStation() {
        if (ChooseServiceFragment.servicePosition!! < ticketServiceListData.size - 1) {
            val intent = Intent("next-station")
            intent.putExtra("message", "next_station")
            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
        }
    }

    private fun showMessage(message: String) =
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

    private fun choosePackageClick(serviceArray: ServiceTicketData.Service_array) {

        val quantity: Int
        val quantityValue = countBadge
        quantity = quantityValue + 1
        countBadge = quantity

        totalPackage.add(countBadge)
        val countTotal = totalPackage.sum()

        totalCount.add(countTotal)
//        (activity as MainActivity).badge(totalCount)


        val adultCount = packageServiceFragment.ServiceAdultCountShow.text.toString()
        val childCount = packageServiceFragment.cServiceChildCountShow.text.toString()
        val date = packageServiceFragment.cServiceDatePickerEditText.text.toString()
        val time = packageServiceFragment.cServiceTimeEdittext.text.toString()
            .replace(gmt_view, "")





        val countAdult: Int = adultCount.toInt()
        val countChild: Int = childCount.toInt()
        val count: Double = serviceArray.price_adult.toDouble()
        val countA: Double = serviceArray.price_children.toDouble()

        adultCountTotal = (countAdult)
        childCountTotal = (countChild)

        totalAdult = ((count * countAdult).toInt())
        totalChild = ((countA * countChild).toInt())
        total.add((totalChild + totalAdult))
        Log.d(TAG, "total amount:$total ")

        adultTotalCount.add(adultCountTotal)
        childTotalCount.add(childCountTotal)




        if (selectedPackageData.isEmpty()) {
            selectedServiceToken = serviceArray.service_token
            passengerStationDataObject = PassengerStationData.PassengerStationData(
                service_token = serviceArray.service_token,
                unique_business_token = serviceArray.unique_business_token,
                adult_count = adultCount.toInt(),
                children_count = childCount.toInt(),
                service_date = date,
                service_time = time,
                sp_company_token = serviceArray.sp_company_token,
                notes = "",
            )

            Log.d("passsssss", "serviceArray:${packageServiceData}")

            passengerStationData.add(passengerStationDataObject!!)
            Log.d(TAG, "choosePackageClick:${passengerStationData}")

        }
        else {
            val countTotal = totalPackage.sum()
            totalCount.add(countTotal)
            selectedServiceToken = selectedPackageData[0].service_token
            val index = passengerStationData.indexOfFirst {
                it.service_token == selectedPackageData[0].service_token
            }
            passengerStationData[index].adult_count = adultCount.toInt()
            passengerStationData[index].children_count = childCount.toInt()
            passengerStationData[index].service_date = date
            passengerStationData[index].service_time = time
        }






    }

    @SuppressLint("SuspiciousIndentation")
    private fun addToCart() {
        if (Utility.tempDate == "" || Utility.tempTime == "") {
            for (data in packageServiceData) {
                val storedArrayCalendar: Calendar =
                    getCalenderData(data.service_date.toString(), data.service_time.toString(), 0)
//            val date =  packageServiceFragment.cServiceDatePickerEditText.text.toString()
//            val time = packageServiceFragment.cServiceTimeEdittext.text.toString()
//                .replace(gmt_view, "")
                val currentdate: Calendar = getCalenderData(
                    packageServiceFragment.cServiceDatePickerEditText.text.toString(),
                    packageServiceFragment.cServiceTimeEdittext.text.toString()
                        .replace(gmt_view, ""),
                    0
                )

                if (currentdate.timeInMillis - storedArrayCalendar.timeInMillis < 23 * 60 * 60 * 1000) {
                    Toast.makeText(
                        requireContext(),
                        "Service should be booked at least 24 hours in prior!",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                } else if (currentdate.get(Calendar.DAY_OF_YEAR) < storedArrayCalendar.get(Calendar.DAY_OF_YEAR) ||
                    currentdate.get(Calendar.YEAR) < storedArrayCalendar.get(Calendar.YEAR)
                ) {
                    Toast.makeText(
                        requireContext(),
                        "Service should be booked at least 24 hours in prior!",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }


            }
        }else{

                val storedArrayCalendar: Calendar =
                    getCalenderData(Utility.tempDate, Utility.tempTime.replace(gmt_view, ""),
                        0
                    )
                val currentdate: Calendar = getCalenderData(
                    packageServiceFragment.cServiceDatePickerEditText.text.toString(),
                    packageServiceFragment.cServiceTimeEdittext.text.toString()
                        .replace(gmt_view, ""),
                    0
                )

                if (currentdate.timeInMillis - storedArrayCalendar.timeInMillis < 1 * 60 * 60 * 1000) {
                    Toast.makeText(
                        requireContext(),
                        "Please check previous Flight Date & Time!",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                } else if (currentdate.get(Calendar.DAY_OF_YEAR) < storedArrayCalendar.get(Calendar.DAY_OF_YEAR) ||
                    currentdate.get(Calendar.YEAR) < storedArrayCalendar.get(Calendar.YEAR)
                ) {
                    Toast.makeText(
                        requireContext(),
                        "Please check previous Flight Date & Time!",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }

            Utility.tempDate = ""
            Utility.tempTime = ""

        }

        Utility.tempDate = packageServiceFragment.cServiceDatePickerEditText.text.toString()
        Utility.tempTime = packageServiceFragment.cServiceTimeEdittext.text.toString()

        if (packageServiceFragment.termsCheckbox.isChecked ) {
            if (passengerStationDataObject != null) {
                ChooseServiceFragment().callBackAdapter()
                for (data in passengerStationData) {
                    val serviceToken = data.service_token
                    for (serviceArray in packageServiceData) {
                            if (serviceToken == serviceArray.service_token) {
                                Log.d("termsCheckbox", "addToCart: 1")
                                data.service_date = packageServiceFragment.cServiceDatePickerEditText.text.toString()
                                data.service_time =
                                    packageServiceFragment.cServiceTimeEdittext.text.toString()
                                        .replace(gmt_view, "")
                            }
                        }
                }

                if (selectedPassengerData.isEmpty()) {
                    Log.d("termsCheckbox", "addToCart: 2")

                    val passengerArray: ArrayList<PassengerStationData.PassengerStationData> =
                        ArrayList()
                    passengerArray.addAll(passengerStationData)
                    Log.d("passengerArray", "passengerArray:${passengerArray} ")


                    val finalData = PassengerStationData(ttr_token = ChooseServiceFragment.ttr_token!!, stationData = passengerArray)

                    selectedPassengerData.add(finalData)
                } else {
                    val stationPackage = selectedPassengerData.indexOfFirst { it.ttr_token == ChooseServiceFragment.ttr_token }

                    if (stationPackage != -1){
                        Log.d("termsCheckbox", "addToCart: 3")

                        val filteringArray: ArrayList<PassengerStationData.PassengerStationData> =
                            ArrayList()

                        val completePackageArray: ArrayList<PassengerStationData.PassengerStationData> =
                            ArrayList()

                        for (data in selectedPassengerData) {
                            completePackageArray.addAll(data.stationData)
                        }

                        for (data in passengerStationData) {
                            if (completePackageArray.indexOfFirst { it.service_token == data.service_token } == -1) {
                                filteringArray.add(data)
                            }
                        }

                        val finalData = PassengerStationData(
                            ttr_token = ChooseServiceFragment.ttr_token!!,
                            stationData = filteringArray
                        )

                        for (data in selectedPassengerData){
                            if (data.ttr_token == ChooseServiceFragment.ttr_token!!){
                                data.stationData.addAll(filteringArray)
                            }
                        }


                    }else{
                        Log.d("termsCheckbox", "addToCart: 4")

                        val mandatoryCalendar: Calendar = getCalenderData(
                            serviceDate = selectedPassengerData[selectedPassengerData.size - 1].stationData[0].service_date,
                            serviceTime = selectedPassengerData[selectedPassengerData.size - 1].stationData[0].service_time,
                            addDays = 0
                        )
                        Log.d("passengerArray", "addToCart: ${ selectedPassengerData[selectedPassengerData.size - 1].stationData[0].service_date}")
                        Log.d("passengerArray", "addToCart: ${ selectedPassengerData[selectedPassengerData.size - 1].stationData[0].service_time}")


                        val date = packageServiceFragment.cServiceDatePickerEditText.text.toString()
                        val time = packageServiceFragment.cServiceTimeEdittext.text.toString()
                            .replace(gmt_view, "")

                        val currentPackageTime: Calendar = getCalenderData(
                            serviceDate = date,
                            serviceTime = time,
                            addDays = 0
                        )


                       /* if (currentPackageTime.time.before(mandatoryCalendar.time)) {
                            alertToast(requireContext(), "Please check previous flight date & time !")
                            return
                        }
                        else*/

                            if (requireActivity().formatDate("ddMMyyyyHHmms", currentPackageTime.time) ==
                                 requireActivity().formatDate("ddMMyyyyHHmms", mandatoryCalendar.time)) {

                                alertToast(requireContext(), "Please check previous flight date & time  !")
                                return
                        }






                        val filteringArray: ArrayList<PassengerStationData.PassengerStationData> =
                            ArrayList()

                        val completePackageArray: ArrayList<PassengerStationData.PassengerStationData> =
                            ArrayList()

                        for (data in selectedPassengerData) {
                            completePackageArray.addAll(data.stationData)
                        }

                        for (data in passengerStationData) {
                            if (completePackageArray.indexOfFirst { it.service_token == data.service_token } == -1) {
                                filteringArray.add(data)
                            }
                        }

                        val finalData = PassengerStationData(
                            ttr_token = ChooseServiceFragment.ttr_token!!,
                            stationData = filteringArray
                        )

                        selectedPassengerData.add(finalData)

                    }
                }

                packageServiceFragment.termsCheckbox.isChecked = true
                packageServiceFragment.addToCart.visibility = View.GONE
                packageServiceFragment.removeFromCart.visibility = View.VISIBLE
                /** brand color */
                packageServiceFragment.removeFromCart.setBackgroundColor(Color.parseColor(activity?.let {
                    SavedSharedPreference.getCustomColor(
                        it
                    ).brand_colour
                }))


                packageServiceFragment.checkout.visibility = View.VISIBLE

                val selectedTTR = getCurrentTTR(selectedCompanyToken)
                val currentServiceIndex =
                    ticketServiceListData.indexOfFirst { it.ttr_token == selectedTTR }

                try{
                    if (ticketServiceListData[currentServiceIndex + 1].service_collection!!.isEmpty()) {
                        packageServiceFragment.nextStation.visibility = View.GONE
                    } else {
                        packageServiceFragment.nextStation.visibility = View.VISIBLE
                    }
                }catch (e: Exception){e.printStackTrace()}

                if (ChooseServiceFragment.servicePosition == ticketServiceListData.size - 1) {
                    packageServiceFragment.nextStation.visibility = View.GONE
                }
                (activity as MainActivity).badge(totalCount)

                Toast.makeText(context, "Package Added to Cart", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Please add a package", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Please agree the terms", Toast.LENGTH_SHORT).show()
        }
    }


    private fun getCalenderData(serviceDate: String, serviceTime: String, addDays: Int): Calendar {

        Log.d("servDate", serviceDate.toString())

        val serviceDateSpit = serviceDate.split("-")
        Log.d("servDate1", serviceDateSpit.toString())
        if (serviceDateSpit.size < 3) {
            throw IllegalArgumentException("Invalid service date string: $serviceDate")
            return Calendar.getInstance()
        }

        val date = serviceDateSpit[0]
        Log.d("airDate", date.toString())
        val month = getMonth(serviceDateSpit[1])
        val year = Regex("\\d+").find(serviceDateSpit[2])?.value ?: "0"



        // Split the service time string into its components
        val serviceTimeSpit = serviceTime.split(" ")
        val am_pm = serviceTimeSpit[1]


        // Convert the service time string to 24-hour format
        val displayFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val parseFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val serviceTime24 = displayFormat.format(parseFormat.parse(serviceTime)!!)

        // Split the service time string into its components
        val timeSpit = serviceTime24.split(":")
        val hour = timeSpit[0].toInt()
        val minutes = timeSpit[1].toInt()

        // Create a new Calendar instance and set its date and time components
        val mandatoryCalender = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH,date.toIntOrNull() ?: 1)
            set(Calendar.MONTH, month)
            set(Calendar.YEAR, year.toIntOrNull() ?: 0)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minutes)
        }
        mandatoryCalender.add(Calendar.DATE, addDays)
        return mandatoryCalender
    }

    private fun checkOut() {
        val intent = Intent("open-cart")
        intent.putExtra("message", "open_cart")
        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
    }

    private fun removeFromCart() {

        if (passengerStationData.isNotEmpty()) {
            var tokenIndex: Int? = null


            val currentTTR = getCurrentTTR(selectedCompanyToken)

            tokenIndex = passengerStationData.indexOfFirst {
                it.service_token == selectedServiceToken
            }

            val companyIndex = selectedPassengerData.indexOfFirst { it.ttr_token == currentTTR }

            if (companyIndex != -1){
                selectedPassengerData.removeAt(companyIndex)
            }

            if (tokenIndex != null) {
                totalCount.removeLast()
                passengerStationData.removeAt(tokenIndex)
                (activity as MainActivity).badge(totalCount)
                Toast.makeText(context, "Package Removed From Cart", Toast.LENGTH_SHORT).show()
                packageServiceFragment.addToCart.visibility = View.VISIBLE
                packageServiceFragment.removeFromCart.visibility = View.GONE
                packageServiceFragment.checkout.visibility = View.GONE
                packageServiceFragment.nextStation.visibility = View.GONE
            }
        }
    }

    private fun removePackage(token: String, serviceArray: ServiceTicketData.Service_array) {
        if (totalCount.isNotEmpty()) {
            totalCount.removeLast()
        }
//        totalCount.removeLast()
        val count: Double = serviceArray.price_adult.toDouble()
        val countA: Double = serviceArray.price_children.toDouble()

        if (selectedPassengerData.isNotEmpty()){
            var isEmpty = false
            for (data in selectedPassengerData){
                if (data.stationData.isNotEmpty()){
                    val index = data.stationData.indexOfFirst { it.service_token == token }
                    if (index != -1){
                        data.stationData.removeAt(index)
                    }
                }
            }
            outer@ for (data in selectedPassengerData){
                if (data.stationData.isNotEmpty()){
                    isEmpty = false
                    break@outer
                }
            }

            val positionNoServices = selectedPassengerData.indexOfFirst { it.stationData.isEmpty() }
            if (positionNoServices != -1){
                selectedPassengerData.removeAt(positionNoServices)
            }

            if (isEmpty){
                selectedPassengerData.clear()
            }
        }

        (activity as MainActivity).badge(totalCount)

        for (i in 0 until passengerStationData.size) {
            if (passengerStationData[i].service_token == token) {
                val adultCount = passengerStationData[i].adult_count
                val childCount = passengerStationData[i].children_count
                val amountToMinus = (adultCount * count) + (childCount * countA)
                total[0] = (total[0] - amountToMinus).toInt()
                passengerStationData.removeAt(i)
                selectedPackageData.clear()
                break
            }
        }

        var isAnyClicked = false

        pack1@ for (data in packageServiceData) {
            if (data.isClicked) {
                isAnyClicked = true

                break@pack1
            }
        }

        if (!isAnyClicked) {
            packageServiceFragment.addToCart.visibility = View.VISIBLE
            packageServiceFragment.removeFromCart.visibility = View.GONE
            packageServiceFragment.checkout.visibility = View.GONE
            if (ChooseServiceFragment.servicePosition == ticketServiceListData.size - 1) {
                packageServiceFragment.nextStation.visibility = View.GONE
            } else {
                packageServiceFragment.nextStation.visibility = View.VISIBLE
            }
        }

        passengerStationDataObject = null

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        /** Brand Color*/
        val color = Color.parseColor(context?.let { SavedSharedPreference.getCustomColor(it).secondary_colour })
        packageServiceFragment.termsCheckbox.buttonTintList = ColorStateList.valueOf(color)




        return packageServiceFragment.root
    }


    @SuppressLint("SetTextI18n")
    private fun customTextView(textView: TextView) {

        val hyperColor = ContextCompat.getColor(requireContext(), R.color.hyperLink)
        val blackColor = ContextCompat.getColor(requireContext(), R.color.textcolor)

        val termsText = SpannableStringBuilder()

        termsText.color(blackColor) { append("I agree to ") }
        termsText.color(hyperColor) {
            append("Terms and Conditions ")
            val start = 11
            val end = 31
            setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    requireActivity().startActivity(
                        Intent(requireContext(), WebView::class.java)
                            .putExtra("URL", terms)
                            .putExtra("heading", "Terms and Conditions")
                    )
                }
            }, start, end, 0)
        }
        termsText.color(blackColor) { append(", ") }
        termsText.color(hyperColor) {
            append("Privacy Policy ")
            val start = 34
            val end = 48
            setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    requireActivity().startActivity(
                        Intent(requireContext(), WebView::class.java)
                            .putExtra("URL", policy)
                            .putExtra("heading", "Privacy Policy")
                    )
                }
            }, start, end, 0)
        }
        termsText.color(blackColor) { append("and ") }
        termsText.color(hyperColor) {
            append("Cancellation Policy ")
            val start = 53
            val end = 72
            setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    requireActivity().startActivity(
                        Intent(requireContext(), WebView::class.java)
                            .putExtra("URL", cancel)
                            .putExtra("heading", "Cancellation Policy")
                    )
                }

            }, start, end, 0)
        }
        termsText.color(blackColor) { append("of $packageName.") }

        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.setText(termsText, BufferType.SPANNABLE)
    }
}



