package com.travel.airportzo.user.ui.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.travel.airportzo.user.R
import com.travel.airportzo.user.databinding.ServiceFragmentBinding
import com.travel.airportzo.user.model.*
import com.travel.airportzo.user.network.ApiResult
import com.travel.airportzo.user.savedpreference.SavedSharedPreference
import com.travel.airportzo.user.utils.setOnDebounceListener
import com.travel.airportzo.user.ui.adapter.ServiceImageAdapter
import com.travel.airportzo.user.ui.adapter.ServiceIncludeAdapter
import com.travel.airportzo.user.ui.adapter.ServicePartnerAdapter
import com.travel.airportzo.user.ui.base.BaseFragment
import com.travel.airportzo.user.utils.Utility
import com.travel.airportzo.user.utils.getCurrentDate
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ServiceFragment : BaseFragment() {

    private val serviceFragment by lazy { ServiceFragmentBinding.inflate(layoutInflater) }

    private val serviceImage : ArrayList<ServiceDetailData.Avail_services>  by lazy { arrayListOf() }
    private val partner : ArrayList<ServiceDetailData.Partners>  by lazy { arrayListOf() }
    private val transitsOneFlight:ArrayList<SearchServiceData> by lazy { arrayListOf() }
    private val directFlights : ArrayList<SearchServiceData>  by lazy { arrayListOf() }
    private val fromData: ArrayList<TerminalData.Common> by lazy { arrayListOf() }
    private val serviceDescription : ArrayList<String>  by lazy { arrayListOf() }
    private val terminalList : ArrayList<String> by lazy { arrayListOf() }

    private val descriptionService by lazy { context?.let { ServiceIncludeAdapter(it,serviceDescription) } }
    private val imageService by lazy { context?.let { ServiceImageAdapter(it,serviceImage) } }
    private val partnersAdapter by lazy { context?.let { ServicePartnerAdapter(it,partner) } }

    private var directBoolean : Boolean = true
    private var serviceToken:String=""

    private var backcolor:Int=0

    private var fromToken :String = ""
    private var toToken :String = ""

    private var fromOneToken :String = ""
    private var toOneToken :String = ""

    private var fromTwoToken :String = ""
    private var toTwoToken :String = ""

    private var fromThreeToken :String = ""
    private var toThreeToken :String = ""


//
//    private val directFlight by lazy { context?.let {
//            BottomSheetDialog(it, R.style.MyBottomSheetDialogTheme).apply { setContentView(layoutInflater.inflate(R.layout.bottomsheet_directflight, null))
//                setCancelable(true)}
//        }
//    }

//    private val transit by lazy {
//        context?.let {
//            BottomSheetDialog(it, R.style.MyBottomSheetDialogTheme).apply { setContentView(layoutInflater.inflate(R.layout.bottomsheet_transit, null))
//                setCancelable(true)}
//        }
//    }

    private val directFlight by lazy { context?.let {
        BottomSheetDialog(it, R.style.MyBottomSheetDialogTheme).apply {
            setContentView(layoutInflater.inflate(R.layout.bottomsheet_directflight, null))

            val transitSearchBtn = findViewById<MaterialButton>(R.id.bookService)
            val colorString =
                activity?.let { SavedSharedPreference.getCustomColor(it).brand_colour }
            val brandColor = Color.parseColor(colorString)
            transitSearchBtn?.setBackgroundColor(brandColor)



            setCancelable(true)
            setOnShowListener {
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }
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



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        return serviceFragment.root
    }



    @SuppressLint("CutPasteId", "SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /** brand color*/
        val colorString = activity?.let { SavedSharedPreference.getCustomColor(it).brand_colour }
        val brandColor = Color.parseColor(colorString)
        serviceFragment.bBookService.setBackgroundColor(brandColor)
        serviceFragment.bookService.setBackgroundColor(brandColor)



        serviceToken = arguments?.getString("token")!!
        backcolor= arguments?.getInt("color")!!
        serviceFragment.layout.setBackgroundColor(backcolor)
        serviceDetails()



        val transitsOneFrom = transit?.findViewById<AutoCompleteTextView>(R.id.aJourney1FromEdit)
        val transitsOneTo =transit?.findViewById<AutoCompleteTextView>(R.id.aJourney1toEdit)
        val transitsTwoFrom =transit?.findViewById<TextView>(R.id.aJourney2FromEdit)
        val transitsTwoTo =transit?.findViewById<AutoCompleteTextView>(R.id.aJourney2toEdit)
        val transitsThreeFrom =transit?.findViewById<TextView>(R.id.aJourney3FromEdit)
        val transitsThreeTo =transit?.findViewById<AutoCompleteTextView>(R.id.aJourney3toEdit)


        val transitsOneData =  transit?.findViewById<TextView>(R.id.aJourney1DepartureOnEdit)
        val transitsOneNo = transit?.findViewById<TextView>(R.id.aJourney1DepartureFlightEdit)
        val transitsTwoData = transit?.findViewById<TextView>(R.id.aJourney2DepartureOnEdit)
        val transitsTwoNo = transit?.findViewById<TextView>(R.id.aJourney2DepartureFlightEdit)
        val transitsThreeData = transit?.findViewById<TextView>(R.id.aJourney3DepartureOnEdit)
        val transitsThreeNo = transit?.findViewById<TextView>(R.id.aJourney3DepartureFlightEdit)


        serviceFragment.back.setOnDebounceListener {
            onBackPressed()
        }

        serviceFragment.bBookService.setOnDebounceListener {
            directFlight?.show()
            directFlight?.findViewById<TextView>(R.id.DepartEdit)?.text = requireContext().getCurrentDate()
            directFlight?.findViewById<RadioButton>(R.id.direct)?.setTextColor(context?.let {
            ContextCompat.getColorStateList(
                it, R.color.black)
        })
            directFlight?.findViewById<RadioButton>(R.id.direct)?.isChecked=true
        }

        serviceFragment.bookService.setOnDebounceListener {
            directFlight?.show()
            directFlight?.findViewById<TextView>(R.id.DepartEdit)?.text = requireContext().getCurrentDate()
            directFlight?.findViewById<RadioButton>(R.id.direct)?.setTextColor(context?.let {
                ContextCompat.getColorStateList(
                    it, R.color.black)
            })
            directFlight?.findViewById<RadioButton>(R.id.direct)?.isChecked=true
        }

        directFlight?.findViewById<RadioButton>(R.id.transits)?.setOnClickListener {
            directFlight?.dismiss()
            transit?.show()
            transit?.findViewById<TextView>(R.id.aJourney1DepartureOnEdit)?.text = requireContext().getCurrentDate()
            transit?.findViewById<TextView>(R.id.aJourney2DepartureOnEdit)?.text = requireContext().getCurrentDate()
            transit?.findViewById<TextView>(R.id.aJourney3DepartureOnEdit)?.text = requireContext().getCurrentDate()
        }
        directFlight?.findViewById<RadioButton>(R.id.direct)?.setOnClickListener {
            directFlight?.show()
            directFlight?.findViewById<TextView>(R.id.DepartEdit)?.text = requireContext().getCurrentDate()
        }

        transit?.findViewById<ImageView>(R.id.close)?.setOnDebounceListener {
            transit?.dismiss()
            directFlight?.show()
            directFlight?.findViewById<TextView>(R.id.DepartEdit)?.text = requireContext().getCurrentDate()
            directFlight?.findViewById<RadioButton>(R.id.direct)?.setTextColor(context?.let {
                ContextCompat.getColorStateList(
                    it, R.color.black)
            })
            directFlight?.findViewById<RadioButton>(R.id.direct)?.isChecked=true
        }


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

        val fromAdapter: ArrayAdapter<String> = context?.let { ArrayAdapter<String>(it, R.layout.spinner_item, android.R.id.text1,terminalList) }!!
        val from=directFlight?.findViewById<AutoCompleteTextView>(R.id.from)
        from?.threshold = 2
        transitsOneFrom?.threshold = 2
        transitsOneFrom?.setAdapter(spinnerAdapter)
        from?.setAdapter(spinnerAdapter)

        //to lists
        val toAdapter: ArrayAdapter<String> = context?.let { ArrayAdapter<String>(it, R.layout.spinner_item,android.R.id.text1, terminalList) }!!
        val to=directFlight?.findViewById<AutoCompleteTextView>(R.id.to)
        to?.threshold = 2
        to?.setAdapter(spinnerAdapter)
        transitsOneTo?.threshold=2
        transitsOneTo?.setAdapter(spinnerAdapter)
        transitsOneTo?.doAfterTextChanged {
            val transitsOutOneTo=transitsOneTo.text.toString()
            transitsTwoFrom?.text=transitsOutOneTo
        }


        transitsTwoTo?.threshold=2
        transitsTwoTo?.setAdapter(toAdapter)
        transitsTwoTo?.doAfterTextChanged {
            val transitsOutTwoTo=transitsTwoTo.text.toString()
            transitsThreeFrom?.text=transitsOutTwoTo
        }


        val directFrom = directFlight?.findViewById<AutoCompleteTextView>(R.id.from)
        val directTo = directFlight?.findViewById<AutoCompleteTextView>(R.id.to)
        val directDate = directFlight?.findViewById<TextView>(R.id.DepartEdit)
        val directNo = directFlight?.findViewById<EditText>(R.id.DepartFlightEdit)


        directDate?.setOnDebounceListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(requireContext(), { _, year, monthOfYear, dayOfMonth ->
                    val dateType = "" + year + "-" + (monthOfYear + 1) + "-" + dayOfMonth
                    val parser = SimpleDateFormat("yyyy-MM-dd")
                    val formatter = SimpleDateFormat("dd MMM, yyyy")
                    val output = formatter.format(parser.parse(dateType)!!)
                    directDate.text = output
                }, year, month, day)
            datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            datePickerDialog.show()
        }


        transitsOneData?.setOnDebounceListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(requireContext(), { _, year, monthOfYear, dayOfMonth ->
                val dateType = "" + year + "-" + (monthOfYear+1) + "-" + dayOfMonth
                val parser = SimpleDateFormat("yyyy-MM-dd")
                val formatter = SimpleDateFormat("dd MMM, yyyy")
                val output = formatter.format(parser.parse(dateType)!!)
                transitsOneData.text = output
            }, year, month, day
            )
            datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            datePickerDialog.show()
        }

        transitsTwoData?.setOnDebounceListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(requireContext(), { _, year, monthOfYear, dayOfMonth ->
                val dateType = "" + year + "-" + (monthOfYear+1) + "-" + dayOfMonth
                val parser = SimpleDateFormat("yyyy-MM-dd")
                val formatter = SimpleDateFormat("dd MMM, yyyy")
                val output = formatter.format(parser.parse(dateType)!!)
                transitsTwoData.text = output
            }, year, month, day
            )
            datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            datePickerDialog.show()
        }

        transitsThreeData?.setOnDebounceListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(requireContext(), { _, year, monthOfYear, dayOfMonth ->
                val dateType = "" + year + "-" + (monthOfYear+1) + "-" + dayOfMonth
                val parser = SimpleDateFormat("yyyy-MM-dd")
                val formatter = SimpleDateFormat("dd MMM, yyyy")
                val output = formatter.format(parser.parse(dateType)!!)
                transitsThreeData.text = output
            }, year, month, day
            )
            datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            datePickerDialog.show()
        }


        directFlight?.findViewById<Button>(R.id.bookService)?.setOnDebounceListener {

            Utility.stationarray = JsonArray()
//            PackageServiceFragment.datee.clear()
//            PackageServiceFragment.timee.clear()
            PackageServiceFragment.totalCount.clear()
            PackageServiceFragment.adultTotalCount.clear()
            PackageServiceFragment.childTotalCount.clear()
            PackageServiceFragment.total.clear()
            directFlights.clear()
            ChooseServiceFragment.data.clear()
            PackageServiceFragment.passengerStationData.clear()
            PackageServiceFragment.total.clear()


            val fromDirect = directFrom?.text.toString()
            val toDirect = directTo?.text.toString()
            val dateDirect = directDate?.text.toString()
            val noDirect = directNo?.text.toString()

            if (fromDirect.isEmpty()) {
                alertToast(requireContext(), "Please enter all details")
            } else if (toDirect.isEmpty()) {
                alertToast(requireContext(), "Please enter all details")
            } else if (dateDirect.isEmpty()) {
                alertToast(requireContext(), "Please enter all details")
            } else if (noDirect.isEmpty()) {
                alertToast(requireContext(), "Please enter all details")
            } else {
                for (i in fromData) {
                    if (i.terminal_string == fromDirect) {
                        fromToken = i.ttr_token
                    }
                }

                for (i in fromData) {
                    if (i.terminal_string == toDirect) {
                        toToken = i.ttr_token
                    }
                }

                directBoolean = true


                directFlights.clear()
                directFlights.add(SearchServiceData(fromToken, toToken, dateDirect, noDirect))
                findNavController().navigate(
                    R.id.action_home_to_ChooseServiceFragment,
                    bundleOf(
                        "directFlight" to directFlights,
                        "directBoolean" to directBoolean,
                        "serviceToken" to serviceToken
                    )
                )
                directFlight?.dismiss()
            }
        }

        transit?.findViewById<TextView>(R.id.aAddTransits)?.setOnDebounceListener {
            if(transit?.findViewById<ConstraintLayout>(R.id.aLayoutJourney3)?.isVisible == true){
                transit?.findViewById<ConstraintLayout>(R.id.aLayoutJourney4)?.visibility = View.VISIBLE
                transit?.findViewById<View>(R.id.aJourney3ImageLine)?.visibility = View.VISIBLE
                transit?.findViewById<TextView>(R.id.aRemoveTransits)?.visibility = View.VISIBLE
                transit?.findViewById<View>(R.id.aJourney3ImageLine)?.visibility = View.VISIBLE
                transit?.findViewById<View>(R.id.aJourney2View3)?.visibility=View.VISIBLE
                transit?.findViewById<TextView>(R.id.aAddTransits)?.visibility = View.GONE
            }else{
                transit?.findViewById<ConstraintLayout>(R.id.aLayoutJourney3)?.visibility = View.VISIBLE
                transit?.findViewById<View>(R.id.aJourney2ImageLine)?.visibility = View.VISIBLE
                transit?.findViewById<View>(R.id.aJourney3ImageLine)?.visibility = View.GONE
                transit?.findViewById<TextView>(R.id.aRemoveTransits)?.visibility = View.VISIBLE
                transit?.findViewById<View>(R.id.aJourney2View3)?.visibility=View.GONE
                //four
                transit?.findViewById<TextView>(R.id.aAddTransits)?.visibility = View.GONE
            }
        }

        transit?.findViewById<TextView>(R.id.aRemoveTransits)?.setOnDebounceListener {
            if (transit?.findViewById<ConstraintLayout>(R.id.aLayoutJourney4)?.isVisible == true){
                transit?.findViewById<ConstraintLayout>(R.id.aLayoutJourney4)?.visibility = View.GONE
                transit?.findViewById<View>(R.id.aJourney3ImageLine)?.visibility = View.GONE
                transit?.findViewById<TextView>(R.id.aRemoveTransits)?.visibility = View.VISIBLE
                transit?.findViewById<TextView>(R.id.aAddTransits)?.visibility = View.VISIBLE
            }else{
                transit?.findViewById<ConstraintLayout>(R.id.aLayoutJourney3)?.visibility = View.GONE
                transit?.findViewById<View>(R.id.aJourney3ImageLine)?.visibility = View.GONE
                transit?.findViewById<TextView>(R.id.aRemoveTransits)?.visibility = View.GONE
                transit?.findViewById<TextView>(R.id.aAddTransits)?.visibility = View.VISIBLE
                transit?.findViewById<View>(R.id.aJourney2ImageLine)?.visibility = View.GONE
            }
        }


        transit?.findViewById<Button>(R.id.aSearchServiceButton)?.setOnDebounceListener {

            transitsOneFlight.clear()
            directFlights.clear()
            PackageServiceFragment.totalCount.clear()
            Utility.stationarray= JsonArray()
//            PackageServiceFragment.datee.clear()
//            PackageServiceFragment.timee.clear()
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
            }else if(twoTo.isEmpty()) {
                alertToast(requireContext(), "Please enter all details")
            }else if (twoData.isEmpty()) {
                alertToast(requireContext(), "Please enter all details")
            }else if(twoNo.isEmpty()) {
                alertToast(requireContext(), "Please enter all details")
//            }else if (threeTo.isEmpty()) {
//                alertToast(requireContext(), "Please enter Arrival")
            }else {
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
                    directFlights.add(SearchServiceData(fromThreeToken, toThreeToken, threeData, threeNo))

                    HomeFragment.transitsDirectFlight.clear()
                    HomeFragment.transitsDirectFlight.addAll(directFlights)

                } else {
                    directFlights.clear()

                    directFlights.add(SearchServiceData(fromOneToken, toOneToken, oneData, oneNo))
                    directFlights.add(SearchServiceData(fromTwoToken, toTwoToken, twoData, twoNo))

                    HomeFragment.transitsDirectFlight.clear()
                    HomeFragment.transitsDirectFlight.addAll(directFlights)

                }

                directBoolean =true


                val jsonObject = JsonObject().apply {
                    add("journey_array", Gson().toJsonTree(directFlights))
                }

                println(jsonObject)
                findNavController().navigate(R.id.action_home_to_ChooseServiceFragment, bundleOf("directFlight" to directFlights,"directBoolean" to directBoolean,"serviceToken" to serviceToken))
                transit?.dismiss()
            }
        }
lifecycleScope.launch {
    viewModel?.terminalList()?.observe(requireActivity(), terminalSearch)
}
    }

    private fun onBackPressed() {
        Navigation.findNavController(requireView()).popBackStack()
    }

    private val terminalSearch=Observer<ApiResult<TerminalData>>{
        when(it){
            is ApiResult.Loading -> loader.onChanged(it.loading)
            is ApiResult.Success -> {
                fromData.clear()
                fromData.addAll(it.data.common)
                for (i in it.data.common){
//                    terminalList.add(i.terminal_string2)
                    terminalList.add(i.terminal_string)
                }
            }
            is ApiResult.Error -> {
//                errors("Error", it.message)
            }
        }
    }

    private fun serviceDetails() {

        val jsonObject = JsonObject().apply {
            addProperty("token",serviceToken )
        }
        Log.d("serviceDetailApi", "image URL:${jsonObject.toString()} ")
        lifecycleScope.launch {
            viewModel?.servicedetail(jsonObject = jsonObject)
                ?.observe(requireActivity(), serviceDetail)
        }
    }

    private val serviceDetail=Observer<ApiResult<ServiceDetailData>>{
        when(it){
            is ApiResult.Success -> {
                Log.d("bannerIMg", "image URL:${it.data} ")
                serviceFragment.apply {
                    stepOne.text=it.data.how_it_works[0].steps
                    stepOneDes.text = it.data.how_it_works[0].content
                    Glide.with(requireContext()).load(it.data.how_it_works[0].image).into(stepA)

                    stepTwo.text=it.data.how_it_works[1].steps
                    stepTwoDes.text = it.data.how_it_works[1].content
                    Glide.with(requireContext()).load(it.data.how_it_works[1].image).into(stepB)

                    stepThree.text=it.data.how_it_works[2].steps
                    stepThreeDes.text = it.data.how_it_works[2].content
                    Glide.with(requireContext()).load(it.data.how_it_works[2].image).into(stepC)

                    serviceName.text=it.data.name
                    sServiceAmount.text=it.data.price.toString()
                    name.text=it.data.name
                    aAboutPara.text=it.data.description
                    context?.let { it1 -> Glide.with(it1).load(it.data.image).into(bannerImage) }
                }
                serviceImage.clear()
                serviceImage.addAll(it.data.avail_services)
                Log.d("TAG", "serviceFragment: "+serviceImage.toString())
                serviceFragment.aAvailableServiceRec.adapter=imageService

                serviceDescription.clear()
                serviceDescription.addAll(it.data.services)
                serviceFragment.sServiceIncludeRecycler.adapter=descriptionService

                partner.clear()
                partner.addAll(it.data.partners)
                serviceFragment.pPartnerRec.adapter=partnersAdapter


            }
            is ApiResult.Error -> {
//                errors("Error", it.message)
            }
            else -> {}
        }
    }


}