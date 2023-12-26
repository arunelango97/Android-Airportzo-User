package com.travel.airportzo.user.ui.fragments


import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.provider.Settings.Global.DEVICE_NAME
import android.telephony.TelephonyManager
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.os.bundleOf
import androidx.core.text.bold
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.mukesh.OtpView
import com.travel.airportzo.user.R
import com.travel.airportzo.user.databinding.HomeFragmentBinding
import com.travel.airportzo.user.model.*
import com.travel.airportzo.user.network.ApiResult
import com.travel.airportzo.user.network.NoInternetActivity
import com.travel.airportzo.user.savedpreference.SavedSharedPreference
import com.travel.airportzo.user.ui.activity.MainActivity
import com.travel.airportzo.user.ui.adapter.*
import com.travel.airportzo.user.ui.base.BaseFragment
import com.travel.airportzo.user.utils.Utility
import com.travel.airportzo.user.utils.getCurrentDate
import com.travel.airportzo.user.utils.setOnDebounceListener
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class HomeFragment : BaseFragment() {

    private val homeFragment by lazy { HomeFragmentBinding.inflate(layoutInflater) }

    private val fromData: ArrayList<TerminalData.Common> by lazy { arrayListOf() }
    private val serviceData: ArrayList<ReadServiceJson.DataItem> by lazy { arrayListOf() }
    private val availableServiceData: ArrayList<ReadServiceJson.AvailServiceItem> by lazy { arrayListOf() }
    private val partnersData: ArrayList<ReadServiceJson.OurPartnersItem> by lazy { arrayListOf() }
    private val colorData: ArrayList<ColorData> by lazy { arrayListOf() }
    private val terminalList: ArrayList<String> by lazy { arrayListOf() }
    private val directFlights: ArrayList<SearchServiceData> by lazy { arrayListOf() }
    private val transitsOneFlight: ArrayList<SearchServiceData> by lazy { arrayListOf() }



//    private val offerImageData: ArrayList<HomeServiceData.Image> by lazy { arrayListOf() }
//    private val topServiceImageData: ArrayList<HomeServiceData.TopService> by lazy { arrayListOf() }
//    private val offerAdapter by lazy { context?.let { HomeOfferAdapter(it,offerImageData) } }
//    private val homeTopServiceAdapter by lazy { context?.let { HomeTopServiceAdapter(it,topServiceImageData) } }
    private val serviceAdapter by lazy {
        context?.let {
            ServiceAdapter(
                it,
                serviceData,
                colorData,
                ::onClicked
            )
        }
    }
    private val bookTicketData: ArrayList<ReadServiceJson.BookingDataItem> by lazy { arrayListOf() }

    private val upcomingBookingAdapter by lazy { UpcomingBookingAdapter(bookTicketData, ::openBookingDetails) }

    private val partnerAdapter by lazy { OurPartnersAdapter(dataList = partnersData) }
    private val availabilityAdapter by lazy {
        AvailabilityAdapter(
            dataList = availableServiceData,
            onclick = ::availabilityClicked
        )
    }

    private var fromToken: String = ""
    private var toToken: String = ""

    private var fromOneToken: String = ""
    private var toOneToken: String = ""

    private var fromTwoToken: String = ""
    private var toTwoToken: String = ""

    private var fromThreeToken: String = ""
    private var toThreeToken: String = ""

    private var directBoolean: Boolean = false
    private var serviceToken: String = ""

    private var mobileNumber: String? = ""
    private var countryCode: String? = ""
    private var otp: String? = ""

    private var radioSelected: String? = ""

    private lateinit var countDownTimer: CountDownTimer

    companion object {
        val transitsDirectFlight: ArrayList<SearchServiceData> by lazy { arrayListOf() }
        var deviceToken =""
        var deviceName =""
        var deviceID =""
    }

    private fun onClicked(token: String, color: Int) {
        findNavController().navigate(
            R.id.action_home_to_ServiceFragment,
            bundleOf("token" to token, "color" to color)
        )
    }


    private val transit by lazy {
        context?.let {
            BottomSheetDialog(it, R.style.MyBottomSheetDialogTheme).apply {
                setContentView(layoutInflater.inflate(R.layout.bottomsheet_transit, null))
                /** brand color  */
                val transitSearchBtn = findViewById<MaterialButton>(R.id.aSearchServiceButton)
                val addTransitTextView = findViewById<TextView>(R.id.aAddTransits)
                val colorString = activity?.let { SavedSharedPreference.getCustomColor(it).brand_colour }
                val brandColor = Color.parseColor(colorString)
                transitSearchBtn?.setBackgroundColor(brandColor)
                val textColor = Color.parseColor(SavedSharedPreference.getCustomColor(it).secondary_colour)
                addTransitTextView?.setTextColor(textColor)


                setCancelable(false)
                setOnShowListener {
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        homeFragment.viewAll.setTextColor(Color.parseColor(activity?.let {
            SavedSharedPreference.getCustomColor(
                it
            ).brand_colour
        }))



        return homeFragment.root

    }


    @SuppressLint("CutPasteId", "SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        FirebaseMessaging.getInstance().token.addOnSuccessListener { result ->
            if (result != null) {
                deviceToken = result
                Log.d("FirebaseDeviceToken", result)
            }
        }

         deviceName = Settings.Global.getString(context!!.contentResolver, "device_name")
         deviceID =Settings.Secure.getString(context!!.contentResolver, Settings.Secure.ANDROID_ID)

        (activity as MainActivity).agentCall()

        homeProfile()


        Log.d("TAG", "onViewCreated: $deviceName")
        Log.d("TAG", "onViewCreated: $deviceID")

        userDynamicColor()

        homeFragment.lookServiceButton.setOnDebounceListener {
            homeFragment.homeScroll.fullScroll(View.FOCUS_UP)
        }


        homeFragment.viewAll.setOnDebounceListener {
            val intent = Intent("open-cart")
            intent.putExtra("message", "gotobooking")
            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
        }

//        homeFragment.offers.adapter=offerAdapter
//        homeFragment.topServices.adapter=homeTopServiceAdapter

        homeFragment.radioGate.setOnCheckedChangeListener { _, checkedId ->
            val id = homeFragment.direct.id
            radioSelected = when (checkedId) {

                id -> {
                    directFlights.clear()
                    homeFragment.direct.setTextColor(context?.let {
                        ContextCompat.getColorStateList(
                            it, R.color.textcolor
                        )
                    })
                    homeFragment.transits.setTextColor(context?.let {
                        ContextCompat.getColorStateList(
                            it, R.color.grey
                        )
                    })
                    getString(R.string.direct)
                }
                else -> {
                    directFlights.clear()
                    transit?.show()

                    transit?.findViewById<TextView>(R.id.aJourney1DepartureOnEdit)?.text = requireContext().getCurrentDate()
                    transit?.findViewById<TextView>(R.id.aJourney2DepartureOnEdit)?.text = requireContext().getCurrentDate()
                    transit?.findViewById<TextView>(R.id.aJourney3DepartureOnEdit)?.text = requireContext().getCurrentDate()

                    homeFragment.direct.setTextColor(context?.let {
                        ContextCompat.getColorStateList(
                            it, R.color.grey
                        )
                    })
                    homeFragment.transits.setTextColor(context?.let {
                        ContextCompat.getColorStateList(
                            it, R.color.textcolor
                        )
                    })
                    getString(R.string.transits)
                }
            }
        }


        //Radio Button

        transit?.findViewById<ImageView>(R.id.close)?.setOnDebounceListener {
            transit?.dismiss()
            homeFragment.direct.isChecked = true
            homeFragment.transits.isChecked = false
        }

        //Direct Flight

        homeFragment.departEdit.setOnDebounceListener {
            val year = Calendar.YEAR
            val month = Calendar.MONTH
            val day = Calendar.DAY_OF_MONTH + 1

            val datePickerDialog =
                DatePickerDialog(requireContext(), { _, year, monthOfYear, dayOfMonth ->
                    val dateType = "" + year + "-" + (monthOfYear + 1) + "-" + dayOfMonth
                    val parser = SimpleDateFormat("yyyy-MM-dd")
                    val formatter = SimpleDateFormat("dd-MMM-yyyy")
                    val output = parser.parse(dateType)?.let { it1 -> formatter.format(it1) }
                    homeFragment.departEdit.text = output
                    Utility.departureDate = homeFragment.departEdit.text.toString()
                    Log.d("departureDate", "departEdit:${Utility.departureDate.toString()}")

                    Utility.FLIGHTDATE = Calendar.getInstance().apply {
                        set(Calendar.YEAR, year)
                        set(Calendar.MONTH, monthOfYear)
                        set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    }.time
                }, year, month, day)
            datePickerDialog.datePicker.minDate = System.currentTimeMillis() + 24 * 60 * 60 * 1000
            datePickerDialog.show()
        }


        homeFragment.apply {
            bookService.setOnDebounceListener {
                Utility.tempDate = ""
                Utility.tempTime = ""
                Utility.stationarray = JsonArray()
                PackageServiceFragment.totalCount.clear()
                PackageServiceFragment.adultTotalCount.clear()
                PackageServiceFragment.childTotalCount.clear()
                PackageServiceFragment.total.clear()
                PackageServiceFragment.selectedPassengerData.clear()
                directFlights.clear()
                ChooseServiceFragment.data.clear()
                PackageServiceFragment.passengerStationData.clear()
                PackageServiceFragment.total.clear()

                (activity as MainActivity).badge(PackageServiceFragment.totalCount)

                val transitsOutOneFrom = homeFragment.fromEdit.text.toString()
                val transitsOutTo = homeFragment.toEdit.text.toString()
                Utility.departureDate = homeFragment.departEdit.text.toString()
                Log.d("departureDate", "departureDate: ${Utility.departureDate.toString()} ")
                val departDate = homeFragment.departEdit.text.toString()
                val departFlight = homeFragment.departFlightEdit.text.toString()

                if (transitsOutOneFrom == transitsOutTo){
                    errorsAlert("Alert!","Departure and arrival cannot be with same airport and terminal")
                    return@setOnDebounceListener
                }

                if (transitsOutOneFrom.isEmpty()) {
                    alertToast(requireContext(), "Please enter Departure")
                } else if (transitsOutTo.isEmpty()) {
                    alertToast(requireContext(), "Please enter Arrival")
                } else if (departDate.isEmpty()) {
                    alertToast(requireContext(), "Please enter Departure Date")
                } else if (departFlight.isEmpty()) {
                    alertToast(requireContext(), "Please enter Departure Flight number")
                } else {
                    for (i in fromData) {
                        if (i.terminal_string == transitsOutOneFrom) {
                            fromToken = i.ttr_token
                        }
                    }
                    for (i in fromData) {
                        if (i.terminal_string == transitsOutTo) {
                            toToken = i.ttr_token
                        }
                    }
                    directFlights.clear()
                    directFlights.add(SearchServiceData(fromToken,toToken,departDate,departFlight))


                    directBoolean = false
                    serviceToken = ""
                    transitsDirectFlight.clear()
                    transitsDirectFlight.addAll(directFlights)
                    findNavController().navigate(
                        R.id.action_home_to_ChooseServiceFragment,
                        bundleOf(
                            "directFlight" to directFlights,
                            "directBoolean" to directBoolean,
                            "serviceToken" to serviceToken
                        )
                    )
                }
            }
        }


        //transits Flight


        val transitsOneFrom = transit?.findViewById<AutoCompleteTextView>(R.id.aJourney1FromEdit)
        val transitsOneTo = transit?.findViewById<AutoCompleteTextView>(R.id.aJourney1toEdit)
        val transitsTwoFrom = transit?.findViewById<TextView>(R.id.aJourney2FromEdit)
        val transitsTwoTo = transit?.findViewById<AutoCompleteTextView>(R.id.aJourney2toEdit)
        val transitsThreeFrom = transit?.findViewById<TextView>(R.id.aJourney3FromEdit)
        val transitsThreeTo = transit?.findViewById<AutoCompleteTextView>(R.id.aJourney3toEdit)

        val transitsOneData = transit?.findViewById<TextView>(R.id.aJourney1DepartureOnEdit)
        val transitsOneNo = transit?.findViewById<TextView>(R.id.aJourney1DepartureFlightEdit)
        val transitsTwoData = transit?.findViewById<TextView>(R.id.aJourney2DepartureOnEdit)
        val transitsTwoNo = transit?.findViewById<TextView>(R.id.aJourney2DepartureFlightEdit)
        val transitsThreeData = transit?.findViewById<TextView>(R.id.aJourney3DepartureOnEdit)
        val transitsThreeNo = transit?.findViewById<TextView>(R.id.aJourney3DepartureFlightEdit)

        val spinnerAdapter: ArrayAdapter<String> = object : ArrayAdapter<String>(
            requireContext(),
            R.layout.spinner_item,
            android.R.id.text1,
            terminalList
        ) {
            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val v = super.getDropDownView(position, convertView, parent)
                v.post {
                    (v.findViewById<View>(android.R.id.text1) as TextView).isSingleLine =
                        false
                }
                return v
            }
        }

//        //From lists
//        val fromAdapter: ArrayAdapter<String> = context?.let {
//            ArrayAdapter<String>(
//                it,
//                R.layout.spinner_text_terminal,
//                terminalList
//            )
//        }!!

        homeFragment.fromEdit.threshold = 2
        transitsOneFrom?.threshold = 2
        transitsOneFrom?.setAdapter(spinnerAdapter)
        homeFragment.fromEdit.setAdapter(spinnerAdapter)

        //to lists
//        val toAdapter: ArrayAdapter<String> = context?.let {
//            ArrayAdapter<String>(
//                it,
//                android.R.layout.simple_spinner_dropdown_item,
//                terminalList
//            )
//        }!!

        homeFragment.toEdit.threshold = 2
        transitsOneTo?.threshold = 2
        transitsOneTo?.setAdapter(spinnerAdapter)
        transitsOneTo?.doAfterTextChanged {
            val transitsOutOneTo = transitsOneTo.text.toString()
            transitsTwoFrom?.text = transitsOutOneTo
        }

        transitsTwoTo?.threshold = 2
        transitsTwoTo?.setAdapter(spinnerAdapter)
        transitsTwoTo?.doAfterTextChanged {
            val transitsOutTwoTo = transitsTwoTo.text.toString()
            transitsThreeFrom?.text = transitsOutTwoTo
        }

        transitsThreeTo?.threshold = 2
        transitsThreeTo?.setAdapter(spinnerAdapter)

        homeFragment.toEdit.setAdapter(spinnerAdapter)

        val calendar: Calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1)
        val simpleDateFormat = SimpleDateFormat("dd-MMM-yyyy")
        val date = simpleDateFormat.format(calendar.time)
        homeFragment.departEdit.text = date
        Log.d("homeFragment", "Departure Date:$date")
        Utility.FLIGHTDATE = calendar.time
        Log.d("FLIGHTDATE", calendar.time.toString())

        transitsOneData?.setOnDebounceListener {
            val year = Calendar.YEAR
            val month = Calendar.MONTH
            val day = Calendar.DAY_OF_MONTH

            val datePickerDialog = DatePickerDialog(
                requireContext(), { _, year, monthOfYear, dayOfMonth ->
                    val dateType = "" + year + "-" + (monthOfYear + 1) + "-" + dayOfMonth
                    val parser = SimpleDateFormat("yyyy-MM-dd")
                    val formatter = SimpleDateFormat("dd-MMM-yyyy")
                    val output = parser.parse(dateType)?.let { it1 -> formatter.format(it1) }
                    transitsOneData.text = output
                }, year, month, day
            )
            datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            datePickerDialog.show()
        }

        transitsTwoData?.setOnDebounceListener {
            val year = Calendar.YEAR
            val month = Calendar.MONTH
            val day = Calendar.DAY_OF_MONTH

            val datePickerDialog = DatePickerDialog(
                requireContext(), { _, year, monthOfYear, dayOfMonth ->
                    val dateType = "" + year + "-" + (monthOfYear + 1) + "-" + dayOfMonth
                    val parser = SimpleDateFormat("yyyy-MM-dd")
                    val formatter = SimpleDateFormat("dd-MMM-yyyy")
                    val output = parser.parse(dateType)?.let { it1 -> formatter.format(it1) }
                    transitsTwoData.text = output
                }, year, month, day
            )
            datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            datePickerDialog.show()
        }

        transitsThreeData?.setOnDebounceListener {
            val year = Calendar.YEAR
            val month = Calendar.MONTH
            val day = Calendar.DAY_OF_MONTH

            val datePickerDialog = DatePickerDialog(
                requireContext(), { _, year, monthOfYear, dayOfMonth ->
                    val dateType = "" + year + "-" + (monthOfYear + 1) + "-" + dayOfMonth
                    val parser = SimpleDateFormat("yyyy-MM-dd")
                    val formatter = SimpleDateFormat("dd-MMM-yyyy")
                    val output = parser.parse(dateType)?.let { it1 -> formatter.format(it1) }
                    transitsThreeData.text = output
                }, year, month, day
            )
            datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            datePickerDialog.show()
        }


        //transits Visible And Gone

        transit?.findViewById<TextView>(R.id.aAddTransits)?.setOnDebounceListener {
            if (transit?.findViewById<ConstraintLayout>(R.id.aLayoutJourney3)?.isVisible == true) {
                transit?.findViewById<ConstraintLayout>(R.id.aLayoutJourney4)?.visibility =
                    View.VISIBLE
                transit?.findViewById<View>(R.id.aJourney3ImageLine)?.visibility = View.VISIBLE
                transit?.findViewById<TextView>(R.id.aRemoveTransits)?.visibility = View.VISIBLE
                transit?.findViewById<View>(R.id.aJourney3ImageLine)?.visibility = View.VISIBLE
                transit?.findViewById<View>(R.id.aJourney2View3)?.visibility = View.VISIBLE
                transit?.findViewById<TextView>(R.id.aAddTransits)?.visibility = View.GONE
            } else {
                transit?.findViewById<ConstraintLayout>(R.id.aLayoutJourney3)?.visibility =
                    View.VISIBLE
                transit?.findViewById<View>(R.id.aJourney2ImageLine)?.visibility = View.VISIBLE
                transit?.findViewById<View>(R.id.aJourney3ImageLine)?.visibility = View.GONE
                transit?.findViewById<TextView>(R.id.aRemoveTransits)?.visibility = View.VISIBLE
                transit?.findViewById<View>(R.id.aJourney2View3)?.visibility = View.GONE
                //four
                transit?.findViewById<TextView>(R.id.aAddTransits)?.visibility = View.GONE
            }
        }

        transit?.findViewById<TextView>(R.id.aRemoveTransits)?.setOnDebounceListener {
            if (transit?.findViewById<ConstraintLayout>(R.id.aLayoutJourney4)?.isVisible == true) {
                transit?.findViewById<ConstraintLayout>(R.id.aLayoutJourney4)?.visibility =
                    View.GONE
                transit?.findViewById<View>(R.id.aJourney3ImageLine)?.visibility = View.GONE
                transit?.findViewById<TextView>(R.id.aRemoveTransits)?.visibility = View.VISIBLE
                transit?.findViewById<TextView>(R.id.aAddTransits)?.visibility = View.VISIBLE
            } else {
                transit?.findViewById<ConstraintLayout>(R.id.aLayoutJourney3)?.visibility =
                    View.GONE
                transit?.findViewById<View>(R.id.aJourney3ImageLine)?.visibility = View.GONE
                transit?.findViewById<TextView>(R.id.aRemoveTransits)?.visibility = View.GONE
                transit?.findViewById<TextView>(R.id.aAddTransits)?.visibility = View.VISIBLE
                transit?.findViewById<View>(R.id.aJourney2ImageLine)?.visibility = View.GONE
            }
        }

        transit?.findViewById<Button>(R.id.aSearchServiceButton)?.setOnDebounceListener {
            Utility.tempDate = ""
            Utility.tempTime = ""

            transitsOneFlight.clear()
            directFlights.clear()
            PackageServiceFragment.totalCount.clear()
            Utility.stationarray = JsonArray()
            PackageServiceFragment.adultTotalCount.clear()
            PackageServiceFragment.childTotalCount.clear()
            PackageServiceFragment.total.clear()
            directFlights.clear()
            ChooseServiceFragment.data.clear()
            PackageServiceFragment.passengerStationData.clear()
            PackageServiceFragment.total.clear()

            val oneFrom = transitsOneFrom?.text.toString()
            val oneTo = transitsOneTo?.text.toString()
            val oneData = transitsOneData?.text.toString()
            val oneNo = transitsOneNo?.text.toString()

            val twoFrom = transitsTwoFrom?.text.toString()
            val twoTo = transitsTwoTo?.text.toString()
            val twoData = transitsTwoData?.text.toString()
            val twoNo = transitsTwoNo?.text.toString()

            val threeTo = transitsThreeTo?.text.toString()
            val threeData = transitsThreeData?.text.toString()
            val threeNo = transitsThreeNo?.text.toString()

            val layoutThree = transit?.findViewById<ConstraintLayout>(R.id.aLayoutJourney3)

            if (oneFrom == oneTo){
                errorsAlert("Alert!","Departure and arrival cannot be with same airport and terminal")
                return@setOnDebounceListener
            }

            if (!TextUtils.isEmpty(twoFrom) && !TextUtils.isEmpty(twoTo)){
                if (twoFrom == twoTo){
                    errorsAlert("Alert!","Departure and arrival cannot be with same airport and terminal")
                    return@setOnDebounceListener
                }
            }


            if (oneFrom.isEmpty()) {
                alertToast(requireContext(), "Please enter all details")
            } else if (oneTo.isEmpty() || twoFrom.isEmpty()) {
                alertToast(requireContext(), "Please enter all details")
            } else if (oneData.isEmpty()) {
                alertToast(requireContext(), "Please enter all details")
            } else if (oneNo.isEmpty()) {
                alertToast(requireContext(), "Please enter all details")
//            } else if(twoFrom.isEmpty()) {
//                alertToast(requireContext(), "Please enter Departure")
            } else if (twoTo.isEmpty()) {
                alertToast(requireContext(), "Please enter all details")
            } else if (twoData.isEmpty()) {
                alertToast(requireContext(), "Please enter all details")
            } else if (twoNo.isEmpty()) {
                alertToast(requireContext(), "Please enter all details")
//            }else if (threeTo.isEmpty()) {
//                alertToast(requireContext(), "Please enter Arrival")
            } else {
                for (i in fromData) {
                    if (i.terminal_string == oneFrom) {
                        fromOneToken = i.ttr_token
                    }
                }

                for (i in fromData) {
                    if (i.terminal_string == oneTo) {
                        toOneToken = i.ttr_token
                        fromTwoToken = i.ttr_token
                    }
                }

                for (i in fromData) {
                    if (i.terminal_string == twoTo) {
                        toTwoToken = i.ttr_token
                        fromThreeToken = i.ttr_token
                    }
                }

                for (i in fromData) {
                    if (i.terminal_string == threeTo) {
                        toThreeToken = i.ttr_token
                    }
                }

                if (layoutThree?.isVisible == true) {
                    directFlights.clear()

                    directFlights.add(SearchServiceData(fromOneToken, toOneToken, oneData, oneNo))
                    directFlights.add(SearchServiceData(fromTwoToken, toTwoToken, twoData, twoNo))
                    directFlights.add(
                        SearchServiceData(
                            fromThreeToken,
                            toThreeToken,
                            threeData,
                            threeNo
                        )
                    )



                    transitsDirectFlight.clear()
                    transitsDirectFlight.addAll(directFlights)

                } else {
                    directFlights.clear()
                    directFlights.add(SearchServiceData(fromOneToken, toOneToken, oneData, oneNo))
                    directFlights.add(SearchServiceData(fromTwoToken, toTwoToken, twoData, twoNo))

                    transitsDirectFlight.clear()
                    transitsDirectFlight.addAll(directFlights)
                }

                directBoolean = false
                serviceToken = ""

                val jsonObject = JsonObject().apply {
                    add("journey_array", Gson().toJsonTree(directFlights))
                }

                println(jsonObject)
                findNavController().navigate(
                    R.id.action_home_to_ChooseServiceFragment,
                    bundleOf(
                        "directFlight" to directFlights,
                        "directBoolean" to directBoolean,
                        "serviceToken" to serviceToken
                    )
                )
                transit?.dismiss()
            }
        }


        colorData.clear()
        colorData.add(
            ColorData(
                color = Color.parseColor("#D9C2EF"),
                colorbtn = Color.parseColor("#9872BD")
            )
        )
        colorData.add(
            ColorData(
                color = Color.parseColor("#C2D4EF"),
                colorbtn = Color.parseColor("#7994C4")
            )
        )
        colorData.add(
            ColorData(
                color = Color.parseColor("#E3EFC2"),
                colorbtn = Color.parseColor("#AFC573")
            )
        )
        colorData.add(
            ColorData(
                color = Color.parseColor("#C2EFE3"),
                colorbtn = Color.parseColor("#71AF9E")
            )
        )
        colorData.add(
            ColorData(
                color = Color.parseColor("#EFC2DB"),
                colorbtn = Color.parseColor("#B46691")
            )
        )
        colorData.add(
            ColorData(
                color = Color.parseColor("#E5D5C0"),
                colorbtn = Color.parseColor("#B28145")
            )
        )
        colorData.add(
            ColorData(
                color = Color.parseColor("#EDCA9A"),
                colorbtn = Color.parseColor("#AB9475")
            )
        )

        colorData.add(
            ColorData(
                color = Color.parseColor("#D9C2EF"),
                colorbtn = Color.parseColor("#9872BD")
            )
        )
        colorData.add(
            ColorData(
                color = Color.parseColor("#C2D4EF"),
                colorbtn = Color.parseColor("#7994C4")
            )
        )
        colorData.add(
            ColorData(
                color = Color.parseColor("#E3EFC2"),
                colorbtn = Color.parseColor("#AFC573")
            )
        )
        colorData.add(
            ColorData(
                color = Color.parseColor("#C2EFE3"),
                colorbtn = Color.parseColor("#71AF9E")
            )
        )
        colorData.add(
            ColorData(
                color = Color.parseColor("#EFC2DB"),
                colorbtn = Color.parseColor("#B46691")
            )
        )
        colorData.add(
            ColorData(
                color = Color.parseColor("#E5D5C0"),
                colorbtn = Color.parseColor("#B28145")
            )
        )
        colorData.add(
            ColorData(
                color = Color.parseColor("#EDCA9A"),
                colorbtn = Color.parseColor("#AB9475")
            )
        )

        colorData.add(
            ColorData(
                color = Color.parseColor("#D9C2EF"),
                colorbtn = Color.parseColor("#9872BD")
            )
        )
        colorData.add(
            ColorData(
                color = Color.parseColor("#C2D4EF"),
                colorbtn = Color.parseColor("#7994C4")
            )
        )
        colorData.add(
            ColorData(
                color = Color.parseColor("#E3EFC2"),
                colorbtn = Color.parseColor("#AFC573")
            )
        )
        colorData.add(
            ColorData(
                color = Color.parseColor("#C2EFE3"),
                colorbtn = Color.parseColor("#71AF9E")
            )
        )
        colorData.add(
            ColorData(
                color = Color.parseColor("#EFC2DB"),
                colorbtn = Color.parseColor("#B46691")
            )
        )
        colorData.add(
            ColorData(
                color = Color.parseColor("#E5D5C0"),
                colorbtn = Color.parseColor("#B28145")
            )
        )
        colorData.add(
            ColorData(
                color = Color.parseColor("#EDCA9A"),
                colorbtn = Color.parseColor("#AB9475")
            )
        )


        homeFragment.profile.setOnDebounceListener {
            val token = SavedSharedPreference.getUserData(requireContext()).token
            if (token!!.isEmpty()) {
                loginView?.show()
            } else {
                findNavController().navigate(R.id.action_home_to_ProfileFragment)
            }
        }


        loginView?.findViewById<MaterialButton>(R.id.SendOtp)?.setOnDebounceListener {
            mobileNumber = loginView?.findViewById<EditText>(R.id.mobileEdit)?.text.toString()
            countryCode =
                loginView?.findViewById<com.hbb20.CountryCodePicker>(R.id.code)?.selectedCountryCode.toString()
            if (mobileNumber!!.isEmpty()) {
                context?.let { it1 -> alertToast(it1, "please enter 10 digit Mobile number") }
            } else {
                logIn()
                loginView!!.dismiss()
            }
        }


        otpView?.findViewById<AppCompatTextView>(R.id.editNumber)?.setOnDebounceListener {
            otpView?.findViewById<OtpView>(R.id.otpView)?.text?.clear()
            otpView?.dismiss()
            countDownTimer.cancel()
            loginView?.show()
        }


        otpView?.findViewById<MaterialButton>(R.id.verifyOtp)?.setOnDebounceListener {
            otp = otpView?.findViewById<OtpView>(R.id.otpView)?.text.toString()
            when {
                otpView?.findViewById<OtpView>(R.id.otpView)?.text.toString().isBlank() -> {
                    context?.let { it1 -> alertToast(it1, "Enter OTP") }
                }
                otpView?.findViewById<OtpView>(R.id.otpView)?.text.toString().length <= 5 -> {
                    context?.let { it1 -> alertToast(it1, "Enter 6 digit OTP") }
                }
                else -> {
                    mobileNumber?.let { it1 -> submitOTP(it1, otp!!, countryCode!!) }
                }
            }
        }



        otpView?.findViewById<TextView>(R.id.resend)?.setOnDebounceListener {
            if (otpView?.findViewById<TextView>(R.id.resend)!!.text.toString() == resources.getString(
                    R.string.didnot_receive
                )
            ) {
                otpView?.findViewById<OtpView>(R.id.otpView)?.requestFocus()
                otpView?.findViewById<TextView>(R.id.resendCode)?.visibility = View.GONE
                logIn()
            }
        }
    }


    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
        viewModel?.terminalList()?.observe(requireActivity(), terminalData)
        }
        val jsonObject = JsonObject().apply {
            addProperty("user_token", SavedSharedPreference.getUserData(requireContext()).token)
        }
        lifecycleScope.launch {
        viewModel?.services(jsonObject)?.observe(requireActivity(), services)
        }
    }

    private fun submitOTP(mobileNumber: String, otp: String, countryCode: String) {
        val jsonObject = JsonObject().apply {
            addProperty("login_device", "Android")
            addProperty("country_code", countryCode)
            addProperty("mobile_number", mobileNumber)
            addProperty("otp", otp)
            addProperty("device_name", deviceName)
            addProperty("device_id", deviceID)
            addProperty("device_token", deviceToken)
        }
        Log.d("TAG", "submitOTP: $jsonObject")
        if (isNetworkConnected(requireContext())) {
            lifecycleScope.launch {
                viewModel?.otpverifyCall(jsonObject = jsonObject)
                    ?.observe(requireActivity(), otpObserver)
            }
        } else {
            startActivity(Intent(requireContext(), NoInternetActivity::class.java))
        }
    }


    private val otpObserver = Observer<ApiResult<OtpData>> {
        when (it) {
            is ApiResult.Loading -> loader.onChanged(it.loading)
            is ApiResult.Success -> {

                otpView?.dismiss()
                Utility.userId = it.data.token
                it.data.let {
                    context?.let { it1 ->
                        SavedSharedPreference.setUserData(
                            it1,
                            it.name,
                            it.mobile_number,
                            it.token,
                            "", "", it.email, it.country_code,
                        )
                    }
                }
                findNavController().navigate(R.id.action_home_to_ProfileFragment)
            }
            is ApiResult.Error -> {
                errorsAlert("Error", it.message)
            }
        }
    }


    private fun logIn() {
        val jsonObject = JsonObject().apply {
            addProperty("login_device", "Android")
            addProperty("country_code", countryCode)
            addProperty("mobile_number", mobileNumber)
            addProperty("type", "login")
        }
        if (isNetworkConnected(requireContext())) {
            lifecycleScope.launch {
                viewModel?.loginCall(jsonObject = jsonObject)
                    ?.observe(requireActivity(), logObserver)
            }
        } else {
            startActivity(Intent(requireContext(), NoInternetActivity::class.java))
        }
    }


    @SuppressLint("SetTextI18n")
    private val logObserver = Observer<ApiResult<Any>> {
        when (it) {
            is ApiResult.Loading -> loader.onChanged(it.loading)
            is ApiResult.Success -> {

                val otpMessage = SpannableStringBuilder()
                    .append("OTP send to ")
                    .bold {
                        append("+${countryCode}-")
                        append(mobileNumber)
                    }
                    .append(".")

                otpView?.findViewById<TextView>(R.id.number)?.text = otpMessage
                otpView?.findViewById<TextView>(R.id.resendCode)?.setOnDebounceListener {
                    otpView?.dismiss()
                    logIn()
                }
                startTimer()
                otpView?.show()
            }
            is ApiResult.Error -> {
                errors("Error", it.message)
            }
        }
    }


    private fun startTimer() {
        countDownTimer = object : CountDownTimer(30000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
               try {
                   otpView?.findViewById<TextView>(R.id.resendCode)?.visibility = View.GONE
                   otpView?.findViewById<TextView>(R.id.resend)?.text =
                       (TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60).toString() + " " + getString(
                           R.string.sec
                       )
                   otpView?.findViewById<TextView>(R.id.resend)
                       ?.setCompoundDrawablesRelativeWithIntrinsicBounds(
                           R.drawable.ic_time_1_1x,
                           0,
                           0,
                           0
                       )
               }catch (e: java.lang.Exception){e.printStackTrace()}
            }

            @SuppressLint("SetTextI18n")
            override fun onFinish() {
             try {
                 otpView?.findViewById<TextView>(R.id.resend)?.text =
                     resources.getString(R.string.didnot_receive)
                 otpView?.findViewById<TextView>(R.id.resendCode)?.visibility = View.VISIBLE
                 otpView?.findViewById<TextView>(R.id.resend)
                     ?.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                 otpView?.findViewById<OtpView>(R.id.otpView)?.text = null
             }catch (e: java.lang.Exception){e.printStackTrace()}
            }
        }
        countDownTimer.start()
    }


    private fun homeProfile() {
        val token = SavedSharedPreference.getUserData(requireContext()).token
        val jsonObject = JsonObject().apply {
            addProperty("user_token", token)
        }
        if (isNetworkConnected(requireContext())) {
            lifecycleScope.launch {
            viewModel?.updateprofile(jsonObject = jsonObject)
                ?.observe(requireActivity(), updatedProfile)
            }
        } else {
            startActivity(Intent(requireContext(), NoInternetActivity::class.java))
        }
    }

    private val updatedProfile = Observer<ApiResult<UpdateprofileData>> {
        when (it) {
            is ApiResult.Success -> {
                SavedSharedPreference.setImageUrl(context = requireContext(), imageUrl = it.data.image)
                context?.let { it1 ->
                    Glide.with(it1).load(it.data.image).apply(RequestOptions.circleCropTransform())
                        .error(R.drawable.ic_profile_1x).into(homeFragment.profile)
                }
            }
            is ApiResult.Error -> {
//                errors("Error", it.message)
            }
            else -> {}
        }
    }

    private val terminalData = Observer<ApiResult<TerminalData>> {
        when (it) {
//            is ApiResult.Loading -> loader.onChanged(it.loading)
            is ApiResult.Success -> {
                fromData.clear()
                fromData.addAll(it.data.common)
                terminalList.clear()
                for (i in it.data.common) {
                    terminalList.add(i.terminal_string)
                }
            }
            is ApiResult.Error -> errors("Error", it.message)

            else ->{}
        }
    }

    private val services = Observer<ApiResult<ReadServiceJson>> {
        when (it) {
            is ApiResult.Loading -> loader.onChanged(it.loading)

            is ApiResult.Success -> {

                if (it.data.userData != null){
                    val userData : ReadServiceJson.UserData = it.data.userData
                    SavedSharedPreference.setUserData(
                        ctx = requireContext(),
                        name = userData.name,
                        mobile = userData.mobileNumber,
                        token = userData.token,
                        date = userData.dob,
                        type = "",
                        email = userData.email,
                        code = userData.countryCode
                    )

                    if (!TextUtils.isEmpty(userData.image)){
                        SavedSharedPreference.setImageUrl(context = requireContext(), imageUrl = userData.image)
                    }
                }

                if (it.data.bookingData != null){
                    homeFragment.upcomingServices.visibility = View.VISIBLE
                    homeFragment.bookingList.visibility = View.VISIBLE
                    homeFragment.viewAll.visibility = View.VISIBLE

                    bookTicketData.clear()
                    bookTicketData.addAll(it.data.bookingData)

                    homeFragment.bookingList.adapter = upcomingBookingAdapter

                }else{
                    homeFragment.upcomingServices.visibility = View.GONE
                    homeFragment.bookingList.visibility = View.GONE
                    homeFragment.viewAll.visibility = View.GONE
                }


                serviceData.clear()
                serviceData.addAll(it.data.data)
                partnersData.clear()
                partnersData.addAll(it.data.ourPartners)
                availableServiceData.clear()
                availableServiceData.addAll(it.data.availService)

                homeFragment.services.adapter = serviceAdapter
                homeFragment.partnersList.adapter = partnerAdapter
                homeFragment.availableServicesList.adapter = availabilityAdapter
            }
            is ApiResult.Error -> {
                errors("Error", it.message)
            }
            else -> {}
        }
    }

   /* private fun userDynamicColor(){
        if (isNetworkConnected(requireContext())) {
            viewModel.getDetails()
                .observe(requireActivity(),getDetails)
        } else {
            startActivity(Intent(requireContext(), NoInternetActivity::class.java))
        }
    }
    private val getDetails = Observer<ApiResult<Response<UserDynamicColor>>>{

        when (it) {
            is ApiResult.Loading -> loader.onChanged(it.loading)
            is ApiResult.Success -> {
                Glide.with(this).load(it.data.data.data.footer_logo).into(homeFragment.journeyLogo)
            }
            is ApiResult.Error ->{
                errors("Error", it.message)
            }
            else ->{
                Toast.makeText(context, "loadingError", Toast.LENGTH_SHORT).show()
            }

        }

    }*/

    private fun userDynamicColor(){

                    /** Search button  */
                    val colorString = activity?.let { SavedSharedPreference.getCustomColor(it).brand_colour }
                    val brandColor = Color.parseColor(colorString)
                    homeFragment.bookService.setBackgroundColor(brandColor)

                    /** radio button*/
                    val radioButton = activity?.let { SavedSharedPreference.getCustomColor(it).secondary_colour }
                    val rdBtnColor = Color.parseColor(radioButton)

                    val colorStateList = ColorStateList(
                        arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf()),
                        intArrayOf(rdBtnColor,Color.LTGRAY) // Checked and unchecked colors
                    )
                    homeFragment.direct.buttonTintList = colorStateList
                    homeFragment.transits.buttonTintList = colorStateList

                    /** logo image  */
                    Glide.with(this@HomeFragment)
                        .load(activity?.let { SavedSharedPreference.getCustomColor(it).footer_logo })
                        .into(homeFragment.journeyLogo)


    }


    private fun openBookingDetails(token: String){
        findNavController().navigate(R.id.action_navigation_home_to_navigation_booking_details, bundleOf("token" to token))
    }

    private fun availabilityClicked(availServiceItem: ReadServiceJson.AvailServiceItem) {
    }
}






