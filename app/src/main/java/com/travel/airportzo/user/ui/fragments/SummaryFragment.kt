package com.travel.airportzo.user.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.text.bold
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.mukesh.OtpView
import com.travel.airportzo.user.R
import com.travel.airportzo.user.databinding.SummaryFragmentBinding
import com.travel.airportzo.user.model.*
import com.travel.airportzo.user.network.ApiClient
import com.travel.airportzo.user.network.ApiResult
import com.travel.airportzo.user.network.NoInternetActivity
import com.travel.airportzo.user.savedpreference.SavedSharedPreference
import com.travel.airportzo.user.ui.activity.MainActivity
import com.travel.airportzo.user.ui.adapter.CouponAdapter
import com.travel.airportzo.user.ui.adapter.SummaryLocationAdapter
import com.travel.airportzo.user.ui.base.BaseFragment
import com.travel.airportzo.user.utils.Utility
import com.travel.airportzo.user.utils.Utility.TAG
import com.travel.airportzo.user.utils.Utility.finalInput
import com.travel.airportzo.user.utils.Utility.userId
import com.travel.airportzo.user.utils.setOnDebounceListener
import kotlinx.coroutines.launch
import retrofit2.Callback
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.math.log
import kotlin.math.roundToInt


class SummaryFragment : BaseFragment() {

    private val summaryFragment by lazy { SummaryFragmentBinding.inflate(layoutInflater) }

    private val packageServiceData: ArrayList<ServiceTicketData.Station_array> by lazy { arrayListOf() }
    private val summaryPackagesData: ArrayList<ServiceTicketData.Service_array> by lazy { arrayListOf() }
    private val serviceGroup: ArrayList<ServiceTicketData.Service_group> by lazy { arrayListOf() }
    private val serviceGroupPrice: ArrayList<ServiceTicketData.Service_array> by lazy { arrayListOf() }
    private val couponData: ArrayList<CouponData> by lazy { arrayListOf() }
    private val serviceGroupAmount: ArrayList<Int> by lazy { arrayListOf() }
    private val flightName: ArrayList<String> by lazy { arrayListOf() }
    private val passengerStationData: ArrayList<PassengerStationData.PassengerStationData> by lazy { arrayListOf() }
    private val selectedPassengerData: ArrayList<PassengerStationData> by lazy { arrayListOf() }


    private val couponAdapter by lazy { CouponAdapter(couponData, ::onCouponSelected) }


    private val types = arrayOf("INR", "USD")

    private var currencyType: String? = null

    private var mobileNumber: String? = ""
    private var countryCode: String? = ""
    private var otp: String? = ""
    private var places: String? = ""
    private var bookingTotal: String? = ""
    var converter: String = ""
    var shortValue: String = ""
    private var country: String = ""


    var total = ""
    var getTotalAmount = ""
    var serviceCostGst = ""
    var convenienceCost = ""
    var convenienceCostGst = ""
    var serviceCostExclGst = ""

    var usdTotal = 0.0
    var usdgetTotalAmount = 0.0
    var usdserviceCostGst = 0.0
    var usdconvenienceCost = 0.0
    var usdconvenienceCostGst = 0.0
    var usdserviceCostExclGst = 0.0
    var discountCost = "0"


    var packagePosition: Int = 0
    var servicePosition: Int = 0
    var selectedServicePosition: Int = 0

    var packageTotalAmount: Int = 0
    private val totalAmount: ArrayList<Int> by lazy { arrayListOf() }


    companion object {
        var order_id: String = ""
        var selectItem: String = ""
        var totalAmountCheckout: String = ""
        var paymentAmount: String = ""
        var couponCode: String = ""
        var couponStatus: String = ""
        var couponType: String = ""
        var rzpAuthKey: String = ""
        var serviceArrayCount: Int = 0
    }

    private val couponBottomSheet by lazy {
        context?.let {
            BottomSheetDialog(it, R.style.MyBottomSheetDialogTheme).apply {
                setContentView(
                    layoutInflater.inflate(
                        R.layout.coupon_bottomsheet, null
                    )
                )
                setCancelable(true)
                setOnShowListener {
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
        }
    }


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /** brand color */

      Log.d("summaryPackagesData", summaryPackagesData.toString())
        totalArrayListAmount()
        summaryFragment.constraintLayout.setBackgroundColor(Color.parseColor(activity?.let {
            SavedSharedPreference.getCustomColor(
                it
            ).brand_colour
        }))
        summaryFragment.checkOut.setBackgroundColor(Color.parseColor(activity?.let {
            SavedSharedPreference.getCustomColor(
                it
            ).brand_colour
        }))

        summaryFragment.viewAllCoupon.setTextColor(Color.parseColor(activity?.let {
            SavedSharedPreference.getCustomColor(
                it
            ).brand_colour
        }))

        summaryFragment.removeCoupon.setTextColor(Color.parseColor(activity?.let {
            SavedSharedPreference.getCustomColor(
                it
            ).brand_colour
        }))


        for (i in 0 until ChooseServiceFragment.ticketServiceListData.size) {
            for (j in 0 until ChooseServiceFragment.ticketServiceListData[i].service_collection!!.size) {
                if (ChooseServiceFragment.ticketServiceListData[i].service_collection!!.isNotEmpty()) {
                    if (ChooseServiceFragment.ticketServiceListData[i].service_collection!![j].service_group!!.isNotEmpty()) {
                        serviceGroup.addAll(ChooseServiceFragment.ticketServiceListData[i].service_collection!![j].service_group!!)
                    }
                }
            }
        }

        for (a in 0 until ChooseServiceFragment.ticketServiceListData.size) {
            for (b in 0 until ChooseServiceFragment.ticketServiceListData[a].service_collection!!.size) {
                for (c in 0 until ChooseServiceFragment.ticketServiceListData[a].service_collection!![b].service_group!!.size) {
                    if (ChooseServiceFragment.ticketServiceListData[a].service_collection!!.isNotEmpty()) {
                        if (ChooseServiceFragment.ticketServiceListData[a].service_collection!![b].service_group!!.isNotEmpty()) {
                            if (ChooseServiceFragment.ticketServiceListData[a].service_collection!![b].service_group!![c].service_array.isNotEmpty()) {
                                summaryPackagesData.addAll(ChooseServiceFragment.ticketServiceListData[a].service_collection!![b].service_group!![c].service_array)
                            }
                        }
                    }
                }
            }
        }

        for (price in 0 until serviceGroup.size) {
            serviceGroupPrice.addAll(serviceGroup[price].service_array.filter { it.isClicked })
            println(serviceGroupPrice)
            Log.d(TAG, "serviceGroup Price: $serviceGroupPrice")
        }

        for (finalAmount in 0 until serviceGroupPrice.size) {
            serviceGroupAmount.add(serviceGroupPrice[finalAmount].price_adult.toInt())
            println(serviceGroupAmount)
            Log.d(TAG, "serviceGroup Amount: ${serviceGroupAmount.sum()}")

        }

        for (i in 0 until PackageServiceFragment.passengerStationData.size) {
            if (PackageServiceFragment.passengerStationData.isNotEmpty()) {
                passengerStationData.addAll(PackageServiceFragment.passengerStationData)
                Log.d(TAG, " summary passengerStationData:${passengerStationData} ")
            }
        }

        for (j in 0 until PackageServiceFragment.selectedPassengerData.size) {
            if (PackageServiceFragment.selectedPassengerData.isNotEmpty()) {
                selectedPassengerData.addAll(PackageServiceFragment.selectedPassengerData)
                Log.d(TAG, " summary selectedPassengerData:${selectedPassengerData} ")
            }
        }






        summaryFragment.summaryBack.setOnDebounceListener {
            onBackPressed()
        }

        summaryFragment.summarySpinner.adapter = context.let {
            context?.let { it1 ->
                ArrayAdapter(
                    it1,
                    androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                    types
                )
            }
        } as SpinnerAdapter

        summaryFragment.summarySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    (parent!!.getChildAt(0) as TextView).setTextColor(Color.WHITE)
                    selectItem = parent.getItemAtPosition(position).toString()
                    currencyType = selectItem

                    if (summaryFragment.couponCode.text == "Apply Coupon") {
                        if (summaryFragment.summarySpinner.selectedItem == "USD") {
                            currency(currencyType.toString())

                        } else {

                            discountCost = "0"
                            getTotal(serviceArrayCount.toString())
                            currency(currencyType.toString())

                        }


                    } else {
                        onCouponSelected(couponCode)
                    }

                    when (summaryFragment.summarySpinner.selectedItem) {
                        "USD" -> summaryFragment.currencyImage.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_usa_1x
                            )
                        )
                        "INR" -> summaryFragment.currencyImage.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_india_1x
                            )
                        )
                    }


                }
            }





        getTotal(totalServiceCost = PackageServiceFragment.total.sum().toString())








        flightName.clear()
        for (a in 0 until ChooseServiceFragment.ticketServiceListData.size) {
            flightName.add(ChooseServiceFragment.ticketServiceListData[a].airport_code)
        }
        for (i in flightName) {
            if (flightName.size == 1) {
                places = ""
                summaryFragment.summaryAirport.text = i

            } else if (flightName.size >= 2) {
                places = summaryFragment.summaryAirport.text.toString()
                if (places!!.isEmpty()) {
                    places += i
                } else {
                    places = "$places - $i"
                }
                summaryFragment.summaryAirport.text = places
            }
        }


        ChooseServiceFragment.servicePosition?.let { ChooseServiceFragment.ticketServiceListData }
            ?.let { packageServiceData.addAll(it) }


        summaryFragment.checkOut.setOnDebounceListener {
            totalAmountCheckout = summaryFragment.summaryTotalAmountPrice.text.toString()
            if (context?.let { it1 -> SavedSharedPreference.getUserData(it1).token?.isEmpty() }!!) {
                loginView?.show()
            } else {
                loader.onChanged(true)
                createOrder()
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
                ) + " " + resources.getString(R.string.resendcode)
            ) {
                otpView?.findViewById<OtpView>(R.id.otpView)?.requestFocus()
                logIn()
            }
        }

        summaryFragment.viewAllCoupon.setOnDebounceListener { getCouponList() }
        summaryFragment.removeCoupon.setOnDebounceListener { removeCoupon() }

        /* totalServiceCount()*/
        toFindTotal()

    }

    private fun toFindTotal() {
        (PackageServiceFragment.totalCount.size.toString() + " " + getString(R.string.service)).also {
            summaryFragment.summaryServiceText.text = it
        }
    }


    private fun removeCoupon() {
        couponCode = ""
        couponStatus = ""
        couponType = ""
        discountCost = "0"
        summaryFragment.removeCoupon.visibility = View.GONE
        summaryFragment.viewAllCoupon.visibility = View.VISIBLE
        summaryFragment.discountAmount.visibility = View.GONE
        summaryFragment.discountText.visibility = View.GONE
        summaryFragment.couponCode.text = getString(R.string.apply_coupon)
        if (summaryFragment.summarySpinner.selectedItem == "INR") {
            getTotal(totalServiceCost = serviceArrayCount.toString())

        } else {
            getTotal(totalServiceCost = Utility.totalamountCurrency)

        }

    }

    private fun getCouponList() {
        if (isNetworkConnected(requireContext())) {
            lifecycleScope.launch {
                viewModel?.availableCoupon()
                    ?.observe(requireActivity(), couponObserver)
            }
        } else {
            startActivity(Intent(requireContext(), NoInternetActivity::class.java))
        }
    }

    private val couponObserver = Observer<ApiResult<List<CouponData>>> {
        when (it) {
            is ApiResult.Loading -> loader.onChanged(it.loading)
            is ApiResult.Success -> {
                couponData.clear()
                couponData.addAll(it.data)
                couponBottomSheet?.findViewById<RecyclerView>(R.id.couponList)?.adapter =
                    couponAdapter
                couponBottomSheet?.show()
            }
            is ApiResult.Error -> {
                Toast.makeText(requireContext(), "No Coupon Available", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun onCouponSelected(couponData: String) {

        couponCode = couponData
        Log.d("onCouponSelected", couponCode.toString())
        Log.d("onCouponSelected", couponCode.toString())

        val tokenArray = JsonArray()
        for (data in PackageServiceFragment.passengerStationData) {
            tokenArray.add(data.service_token)
        }

        val token = SavedSharedPreference.getUserData(requireContext()).token
        val jsonObj = JsonObject().apply {
            addProperty("couponCode", couponData)
            add("serviceToken", tokenArray)
            addProperty("user_token", token)
            addProperty("currency", selectItem)

        }
        Log.d("razor_order", "onCouponSelected: $jsonObj")
        Log.d("server_call", "applyCoupon: $jsonObj")
        lifecycleScope.launch {
            viewModel?.applyCoupon(jsonObject = jsonObj)
                ?.observe(requireActivity(), applyCouponObserver)
        }
        couponBottomSheet?.dismiss()
    }

    private val applyCouponObserver = Observer<ApiResult<CouponApplied>> {
        when (it) {
            is ApiResult.Loading -> loader.onChanged(it.loading)
            is ApiResult.Success -> {
                loader.onChanged(false)
                Log.d("server_call", "responseData: ${it.data}")
                alertToast(requireContext(), it.data.message)
                Log.d("server_call", "cat: ${it.data.data.type}")
                if (it.data.data.type == "Category") {
                    Log.d("Category", "Category: ")
                    applyCategoryCoupon(it.data.data, it.data.categoryData)
                    Log.d("server_call", "cat: ${it.data.categoryData}")
                } else {
                    Log.d("Category", "Cart: ")
                    applyCoupon(it.data.data, it.data.cartData)
                }
            }
            is ApiResult.Error -> {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun applyCategoryCoupon(
        appliedData: CouponApplied.Data,
        cartData: List<CouponApplied.CategoryData>
    ) {
        Log.d("server_call", "applyCategoryCoupon: $appliedData")
        val cartDataItem = cartData[0]

        calculateCategoryCoupon(appliedData, cartDataItem)
        /* if (summaryFragment.summarySpinner.selectedItem == "INR") {
             var totalServiceCost =
                 summaryFragment.summaryServicePrice.text.toString().replace("₹", "").toDouble()
             totalServiceCost += summaryFragment.summaryGstPrice.text.toString().replace("₹", "")
                 .toDouble()
             calculateCategoryCoupon(appliedData, cartDataItem, totalServiceCost.toInt())
             Log.d("server_call", "applyCategoryCoupon: INR" + totalServiceCost.toString())

         } else {
             var totalServiceCost = summaryFragment.summaryServicePrice.text.toString().replace("$", "").toDouble()
             Log.d("server_call", "applyCategoryCoupon: USD" + totalServiceCost.toString())
             totalServiceCost += summaryFragment.summaryGstPrice.text.toString().replace("$", "")
                 .toDouble()
             calculateCategoryCoupon(appliedData, cartDataItem, totalServiceCost.toInt())
             Log.d("server_call", "applyCategoryCoupon: USD" + totalServiceCost.toString())
         }*/
    }


    @SuppressLint("SetTextI18n")
    private fun applyCoupon(
        appliedData: CouponApplied.Data,
        cartData: List<CouponApplied.CartDataItem>
    ) {

        val cartDataItem = cartData[0]
      val floatValue = cartDataItem.cartDisAmt.toFloat()
      val intValue = floatValue.toInt()
        if (cartDataItem.couponCondition == "Greater Than Or Equal To") {
            if (serviceArrayCount >= intValue) {
                calculateCoupon(appliedData, cartDataItem)
            } else errorsAlert(
                "Invalid Coupon",
                "Coupon cannot be applied! Service cost must be ${cartDataItem.couponCondition} to ₹${cartDataItem.cartDisAmt}"
            )
        }
        else if (cartDataItem.couponCondition == "Lesser Than Or Equal To")
            {
            calculateCoupon(appliedData, cartDataItem)
        }
//       else errorsAlert(
//            "Invalid Coupon",
//            "Coupon cannot be applied! Service cost must be ${cartDataItem.couponCondition} to ₹${cartDataItem.cartDisAmt}"
//        )
    }


    @SuppressLint("SetTextI18n")
    private fun calculateCategoryCoupon(
        appliedData: CouponApplied.Data,
        cartDataItem: CouponApplied.CategoryData,
        /*totalServiceCost: Int*/
    ) {

        Log.d("server_call", "appliedData: ${appliedData}")
        Log.d("server_call", { appliedData.code }.toString())
        summaryFragment.couponCode.text = "${appliedData.code} Coupon Code Applied !"
        summaryFragment.viewAllCoupon.visibility = View.GONE
        summaryFragment.removeCoupon.visibility = View.VISIBLE

        couponCode = appliedData.code
        couponStatus = "true"
        couponType = appliedData.type
        var amountAfterDiscount = 0

        Log.d("server_call", "calculateCategoryCoupon: ${cartDataItem.couponType.toString()}")

        if (cartDataItem.couponType == "Flat") {
            if (summaryFragment.summarySpinner.selectedItem == "USD") {
                var businessTokenOccurrence = 0

                for (data in PackageServiceFragment.selectedPassengerData) {
                    for (serviceData in data.stationData) {
                        if (cartDataItem.businessTypeToken == serviceData.unique_business_token) {
                            businessTokenOccurrence++
                        }
                    }
                }

                summaryFragment.discountAmount.visibility = View.VISIBLE
                summaryFragment.discountText.visibility = View.VISIBLE
                Log.d("server_call", "calculateCategoryCoupon: ${serviceArrayCount.toString()}")
                val totalamount =
                    serviceArrayCount.toDouble() * cartDataItem.currencyValue.toDouble()

                Utility.totalamountCurrency = totalamount.toString()
                val discountCostAmount =
                    (cartDataItem.dis_amt.toInt() * businessTokenOccurrence).toDouble().toString()
                discountCost =
                    (discountCostAmount.toDouble() * cartDataItem.currencyValue.toDouble()).toString()

                getTotal(totalamount.toString())
                Log.d("server_call", "calculateCategoryCoupon: ${totalamount.toString()}")
                Log.d("getTotal", "4 : ${totalamount.toString()}")
                summaryFragment.discountAmount.text = "$" + discountCost

            } else {
                var businessTokenOccurrence = 0

                for (data in PackageServiceFragment.selectedPassengerData) {
                    for (serviceData in data.stationData) {
                        if (cartDataItem.businessTypeToken == serviceData.unique_business_token) {
                            businessTokenOccurrence++
                        }
                    }
                }

                summaryFragment.discountAmount.visibility = View.VISIBLE
                summaryFragment.discountText.visibility = View.VISIBLE

                discountCost = (cartDataItem.dis_amt.toInt() * businessTokenOccurrence).toString()
                Log.d("server_call", "Flat discountCost " + amountAfterDiscount)

                summaryFragment.discountAmount.text =
                    "-${getString(R.string.indianRupee)}${(cartDataItem.dis_amt.toInt() * businessTokenOccurrence)}"

                getTotal(serviceArrayCount.toString())
                Log.d("getTotal", "5 : ${serviceArrayCount.toString()}")
            }


        } else if (cartDataItem.couponType == "Percentage") {
            if (summaryFragment.summarySpinner.selectedItem == "USD") {
                var businessTokenOccurrence = 0

                for (data in PackageServiceFragment.selectedPassengerData) {
                    for (serviceData in data.stationData) {
                        if (cartDataItem.businessTypeToken == serviceData.unique_business_token) {
                            businessTokenOccurrence++
                        }
                    }
                }

                summaryFragment.discountAmount.visibility = View.VISIBLE
                summaryFragment.discountText.visibility = View.VISIBLE

                val totalamount =
                    serviceArrayCount.toDouble() * cartDataItem.currencyValue.toDouble()
                Utility.totalamountCurrency = totalamount.toString()
                Log.d("server_call", "Flat totalamount " + totalamount)
                val discountCostAmount =
                    (serviceArrayCount.toDouble() * cartDataItem.dis_amt.toDouble()) / 100
                val disAmount = discountCostAmount * cartDataItem.currencyValue.toDouble()

                discountCost = String.format("%.2f", disAmount)
                Log.d("server_call", "Flat disAmount " + disAmount)
                getTotal(totalamount.toString())
                Log.d("getTotal", "6 : ${totalamount.toString()}")
                summaryFragment.discountAmount.text = "$ " + discountCost


            } else {
                var businessTokenOccurrence = 0

                for (data in PackageServiceFragment.selectedPassengerData) {
                    for (serviceData in data.stationData) {
                        if (cartDataItem.businessTypeToken == serviceData.unique_business_token) {
                            businessTokenOccurrence++
                        }
                    }
                }

                summaryFragment.discountAmount.visibility = View.VISIBLE
                summaryFragment.discountText.visibility = View.VISIBLE

                val amount =
                    ((serviceArrayCount.toDouble() * cartDataItem.dis_amt.toDouble()) / 100)
                discountCost = amount.toString()


                Log.d("server_call", "Flat discountCost " + discountCost)

                summaryFragment.discountAmount.text = "₹" + discountCost

                getTotal(serviceArrayCount.toString())
                Log.d("getTotal", "7 : ${serviceArrayCount.toString()}")
            }

        }

    }

    @SuppressLint("SetTextI18n")
    private fun calculateCoupon(
        appliedData: CouponApplied.Data,
        cartDataItem: CouponApplied.CartDataItem,
    ) {

        summaryFragment.couponCode.text = "${appliedData.code} Coupon Code Applied !"
        summaryFragment.viewAllCoupon.visibility = View.GONE
        summaryFragment.removeCoupon.visibility = View.VISIBLE

        couponCode = appliedData.code
        couponStatus = "true"
        couponType = appliedData.type


        var amountAfterDiscount = 0
        var totalAmountUsd = 0.0
        var disAmountUsd = 0.0
        var totalServiceCost = 0.0
        if (cartDataItem.couponType == "Flat") {

            summaryFragment.discountAmount.visibility = View.VISIBLE
            summaryFragment.discountText.visibility = View.VISIBLE

            if (summaryFragment.summarySpinner.selectedItem == "USD") {


                var totalamount =
                    serviceArrayCount.toDouble() * cartDataItem.currencyValue.toDouble()
                Utility.totalamountCurrency = totalamount.toString()
                Log.d("server_call", "Flat Inr: ${totalamount}")
                val disAmount =
                    cartDataItem.dis_amt.toDouble() * cartDataItem.currencyValue.toDouble()
                Log.d("server_call", "Flat disAmount: ${disAmount}")

                discountCost = String.format("%.2f", disAmount)
                getTotal(totalamount.toString())
                Log.d("getTotal", "8 : ${totalamount.toString()}")

                summaryFragment.discountAmount.text = "-$${discountCost}"


            } else {

                Log.d("server_call", "Flat serviceArrayCount: ${serviceArrayCount}")

                Log.d("server_call", "Flat discountCost: ${discountCost}")
                Log.d("server_call", "Flat disAmount: ${discountCost}")

                discountCost = cartDataItem.dis_amt
                getTotal(serviceArrayCount.toString())
                Log.d("getTotal", "9 : ${serviceArrayCount.toString()}")
                summaryFragment.discountAmount.text =
                    "-${getString(R.string.indianRupee)}${cartDataItem.dis_amt}"

            }

        } else if (cartDataItem.couponType == "Percentage") {

            summaryFragment.discountAmount.visibility = View.VISIBLE
            summaryFragment.discountText.visibility = View.VISIBLE

            if (summaryFragment.summarySpinner.selectedItem == "INR") {
                val disAmount = getPercentageAmount(
                    amount = serviceArrayCount,
                    percentage = cartDataItem.dis_amt.toInt()
                )
                discountCost = disAmount.roundToInt().toString()
                Log.d("server_call", "Percentage disAmount: ${discountCost}")

                summaryFragment.discountAmount.text =
                    "-${getString(R.string.indianRupee)}${disAmount.roundToInt()}"

                getTotal(serviceArrayCount.toString())
                Log.d("getTotal", "10 : ${serviceArrayCount.toString()}")
            } else {


                totalAmountUsd =
                    (serviceArrayCount.toDouble() * cartDataItem.currencyValue.toDouble())
                disAmountUsd =
                    (cartDataItem.dis_amt.toInt() * cartDataItem.currencyValue.toDouble())
                Log.d("server_call", "Percentage disAmountUsd: ${disAmountUsd}")
                Utility.totalamountCurrency = totalAmountUsd.toString()

                val totalUsd = serviceArrayCount.toDouble() * disAmountUsd / 100
                Log.d("server_call", "Percentage totalUsd: ${totalUsd}")

                discountCost = String.format("%.2f", totalUsd)
                getTotal(totalAmountUsd.toString())
                Log.d("getTotal", "11 : ${totalAmountUsd.toString()}")
                summaryFragment.discountAmount.text =
                    "-$${discountCost}"

                Log.d("server_call", "Percentage totalAmountUsd: ${totalAmountUsd}")
                Log.d("server_call", "Percentage disAmountUsd: ${discountCost}")
            }
        }
        Log.d("server_call", "amountAfterDiscount: $amountAfterDiscount")

    }

    private fun getPercentageAmount(amount: Int, percentage: Int): Double {

        val calculatedPercentage = amount * percentage
        val percentageAmount = calculatedPercentage / 100.toDouble()

        return percentageAmount
    }

    private fun currency(selectedItem: String) {
        Log.d("currency",selectedItem)
        if (selectedItem == "INR") {
            country = "USD"
        } else {
            country =  "INR"
        }


        when (summaryFragment.summarySpinner.selectedItem) {
            "USD" -> summaryFragment.currencyImage.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_usa_1x
                )
            )
            "INR" -> summaryFragment.currencyImage.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_india_1x
                )
            )
        }

        val jsonObject = JsonObject().apply {
            addProperty("currency_from", selectedItem)
            addProperty("currency_to", country)
        }
        Log.d("server_call", "currencyFrom: ${selectedItem.toString()} ")
        Log.d("server_call", "currencyTo: ${country.toString()} ")




        ApiClient.APIinterface().currency(jsonObject)
            .enqueue(object : Callback<CurrencyData> {
                override fun onFailure(call: retrofit2.Call<CurrencyData>, t: Throwable) {
                }

                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: retrofit2.Call<CurrencyData>,
                    response: retrofit2.Response<CurrencyData>

                ) {
                    if (response.isSuccessful) {
                        Log.d("onResponse", "onResponse: " + response.body().toString())
                        converter = response.body()!!.data
                        shortValue = String.format("%.2f", converter.toDouble())
                        Log.d("currencyinr",converter)
                        if (selectedItem == "INR") {
                            Log.d("server_call", "Currency INR: ${total}")
                            summaryFragment.summaryTotalAmountPrice.text =
                                "₹" + total.toDouble() * 1
                            summaryFragment.summaryServicePrice.text =
                                "₹" + serviceCostExclGst.toDouble() * 1
                            summaryFragment.conFeeTaxPrice.text =
                                "₹" + convenienceCostGst.toDouble() * 1
                            summaryFragment.conFeePrice.text = "₹" + convenienceCost.toDouble() * 1
                            summaryFragment.summaryGstPrice.text =
                                "₹" + serviceCostGst.toDouble() * 1

                            Log.d(
                                "onResponse",
                                "INR: " + summaryFragment.summaryTotalAmountPrice.text.toString()
                            )
                            if (!TextUtils.isEmpty(discountCost)) {
                                summaryFragment.discountAmount.text =
                                    "₹" + discountCost.toDouble() * shortValue.toDouble()
                            }
//                            totalServiceCount()

                            summaryFragment.summaryRecyclerView.adapter = context?.let {
                                SummaryLocationAdapter(
                                    it, packageServiceData, selectedItem, shortValue,
                                    ::summaryFragControl,
                                    ::summaryGroup,
                                    ::summaryLocation,
                                )
                            }
                            loader.onChanged(false)
                        } else {

                            Log.d("server_call", "Currency USD: ${total}")
                            Log.d("server_call", "Currency shortValue: ${shortValue}")
                            val totalCost = total.toDouble() / shortValue.toDouble()
                            val serviceCostExclGst =
                                serviceCostExclGst.toDouble() / shortValue.toDouble()
                            val convenienceCostGst =
                                convenienceCostGst.toDouble() / shortValue.toDouble()
                            val convenienceCost =
                                convenienceCost.toDouble() / shortValue.toDouble()
                            val serviceCostGst = serviceCostGst.toDouble() / shortValue.toDouble()

                            if (!TextUtils.isEmpty(discountCost)) {
                                val disCost = discountCost.toDouble() / shortValue.toDouble()
                                summaryFragment.discountAmount.text =
                                    "$" + String.format("%.2f", disCost)
                            }
                            Log.d("server_call", "Currency totalCost: ${totalCost}")
                            summaryFragment.summaryTotalAmountPrice.text =
                                "$" + String.format("%.2f", totalCost)
                            summaryFragment.summaryServicePrice.text =
                                "$" + String.format("%.2f", serviceCostExclGst)
                            summaryFragment.conFeeTaxPrice.text =
                                "$" + String.format("%.2f", convenienceCostGst)
                            summaryFragment.conFeePrice.text =
                                "$" + String.format("%.2f", convenienceCost)
                            summaryFragment.summaryGstPrice.text =
                                "$" + String.format("%.2f", serviceCostGst)
//

                            Log.d("currencyusd",selectedItem)
                            Log.d(
                                "onResponse",
                                "USD: " + summaryFragment.summaryTotalAmountPrice.text.toString()
                            )
                            summaryFragment.summaryRecyclerView.adapter = context?.let {
                                SummaryLocationAdapter(
                                    it, packageServiceData, selectedItem, shortValue,
                                    ::summaryFragControl,
                                    ::summaryGroup,
                                    ::summaryLocation,
                                )
                            }
                            loader.onChanged(false)
                        }
                    } else {
                        Toast.makeText(requireContext(), "currency fail", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            })
    }


    /* private fun summaryFragControl(clicked:ServiceTicketData.Service_array) {
         Log.d("packageServiceData",packageServiceData.size.toString() )
         if (PackageServiceFragment.totalCount.size == 1){
             Toast.makeText(context, "Service Can't be empty", Toast.LENGTH_SHORT).show()
         }else {
             for (i in 0 until packageServiceData.size) {
                 for (j in 0 until packageServiceData[i].service_collection?.size!!) {

                     for (k in 0 until packageServiceData[i].service_collection?.get(j)?.service_group?.size!!) {
                         for (l in 0 until packageServiceData[i].service_collection?.get(j)?.service_group?.get(k)?.service_array?.size!!) {
                             Log.d("totalPrice", packageServiceData[i].service_collection?.get(j)?.service_group?.get(k)?.service_array?.get(l)?.totalAmount.toString())
                             val totalAmount = packageServiceData[i].service_collection?.get(j)?.service_group?.get(k)?.service_array?.get(l)?.totalAmount

                             if (clicked.service_token == packageServiceData[i].service_collection?.get(j)?.service_group?.get(k)?.service_array?.get(l)?.service_token) {

                                 if (packageServiceData[i].service_collection?.get(j)?.service_group?.get(k)?.service_array?.size == 1) {
                                     packageServiceData[i].service_collection?.get(j)?.service_group?.removeAt(k)
                                     PackageServiceFragment.totalCount.removeAt(0)
                                     toFindTotal()
                                     totalArrayListAmount()
                                     getTotal(serviceArrayCount.toString())
                                     (activity as MainActivity).badge(PackageServiceFragment.totalCount)
                                     summaryFragment.summaryRecyclerView.adapter = context?.let {
                                         SummaryLocationAdapter(it, packageServiceData, selectItem, shortValue,
                                             ::summaryFragControl,
                                             ::summaryGroup,
                                             ::summaryLocation,
                                         )
                                     }
                                 } else {
                                     packageServiceData[i].service_collection?.get(j)?.service_group?.get(k)?.service_array?.removeAt(l)
                                     PackageServiceFragment.totalCount.removeAt(0)
                                     toFindTotal()
                                     totalArrayListAmount()
                                     getTotal(serviceArrayCount.toString())
                                     (activity as MainActivity).badge(PackageServiceFragment.totalCount)
                                     summaryFragment.summaryRecyclerView.adapter = context?.let {
                                         SummaryLocationAdapter(it, packageServiceData, selectItem,shortValue,
                                             ::summaryFragControl,
                                             ::summaryGroup,
                                             ::summaryLocation,
                                         )
                                     }

                                 }
                                 return
                             }

                         }

                     }
                 }

             }



         }

     }*/

    private fun summaryFragControl(clicked: ServiceTicketData.Service_array) {


        if (PackageServiceFragment.totalCount.size == 1) {
            Toast.makeText(context, "Service Can't be empty", Toast.LENGTH_SHORT).show()
        } else {
            for (i in 0 until packageServiceData.size) {
                for (j in 0 until packageServiceData[i].service_collection?.size!!) {
                    for (k in 0 until packageServiceData[i].service_collection?.get(j)?.service_group?.size!!) {
                        for (l in 0 until packageServiceData[i].service_collection?.get(j)?.service_group?.get(
                            k
                        )?.service_array?.size!!) {
                            Log.d(
                                "totalPrice",
                                packageServiceData[i].service_collection?.get(j)?.service_group?.get(
                                    k
                                )?.service_array?.get(l)?.totalAmount.toString()
                            )
                            val totalAmount =
                                packageServiceData[i].service_collection?.get(j)?.service_group?.get(
                                    k
                                )?.service_array?.get(l)?.totalAmount

                            if (clicked.service_token == packageServiceData[i].service_collection?.get(
                                    j
                                )?.service_group?.get(k)?.service_array?.get(l)?.service_token
                            ) {

                                if (packageServiceData[i].service_collection?.get(j)?.service_group?.get(
                                        k
                                    )?.service_array?.size == 1
                                ) {

                                    if (selectedPassengerData.isNotEmpty()) {
                                        var isEmpty = false
                                        for (data in selectedPassengerData) {
                                            if (data.stationData.isNotEmpty()) {
                                                val index =
                                                    data.stationData.indexOfFirst { it.service_token == clicked.service_token }
                                                if (index != -1) {
                                                    data.stationData.removeAt(index)
                                                }
                                            }
                                        }
                                        outer@ for (data in selectedPassengerData) {
                                            if (data.stationData.isNotEmpty()) {
                                                isEmpty = false
                                                break@outer
                                            }
                                        }

                                        val positionNoServices =
                                            selectedPassengerData.indexOfFirst { it.stationData.isEmpty() }
                                        if (positionNoServices != -1) {
                                            selectedPassengerData.removeAt(positionNoServices)
                                        }

                                        if (isEmpty) {
                                            selectedPassengerData.clear()
                                        }
                                    }
                                    val finalInput = JsonObject()
                                    val stationarray = JsonArray()
                                    Utility.stationarray = JsonArray()

                                    for (data in PackageServiceFragment.selectedPassengerData) {

                                        val serviceArray = JsonArray()
                                        val obj = JsonObject()

                                        for (serviceData in data.stationData) {
                                            serviceArray.add(JsonObject().apply {
                                                addProperty(
                                                    "service_token",
                                                    serviceData.service_token
                                                )
                                                addProperty("adult_count", serviceData.adult_count)
                                                addProperty(
                                                    "children_count",
                                                    serviceData.children_count
                                                )
                                                addProperty(
                                                    "service_date",
                                                    serviceData.service_date
                                                )
                                                addProperty(
                                                    "service_time",
                                                    serviceData.service_time
                                                )
                                                addProperty("notes", serviceData.notes)
                                            })
                                        }
                                        obj.add("service_array", serviceArray)
                                        obj.addProperty("ttr_token", data.ttr_token)

                                        stationarray.apply { add(obj) }
                                        Utility.stationarray.apply { add(obj) }
                                    }
                                    Log.d("stationArray", Utility.stationarray.toString())

                                    finalInput.addProperty(
                                        "user_token",
                                        SavedSharedPreference.getUserData(requireContext()).token
                                    )
                                    finalInput.add("station_array", stationarray)
                                    Utility.finalInput = finalInput

                                    packageServiceData[i].service_collection?.get(j)?.service_group?.removeAt(
                                        k
                                    )
                                    PackageServiceFragment.totalCount.removeAt(0)
                                    toFindTotal()
//                                    totalArrayListAmount()
                                    PackageServiceFragment.total.removeAt(i)
                                    serviceArrayCount =PackageServiceFragment.total.sum()
//                                    serviceArrayCount = PackageServiceFragment.total.removeAt(0)
                                    getTotal(serviceArrayCount.toString())
                                    (activity as MainActivity).badge(PackageServiceFragment.totalCount)
                                    summaryFragment.summaryRecyclerView.adapter = context?.let {
                                        SummaryLocationAdapter(
                                            it, packageServiceData, selectItem, shortValue,
                                            ::summaryFragControl,
                                            ::summaryGroup,
                                            ::summaryLocation,
                                        )
                                    }


                                } else {
                                    if (selectedPassengerData.isNotEmpty()) {
                                        var isEmpty = false
                                        for (data in selectedPassengerData) {
                                            if (data.stationData.isNotEmpty()) {
                                                val index =
                                                    data.stationData.indexOfFirst { it.service_token == clicked.service_token }
                                                if (index != -1) {
                                                    data.stationData.removeAt(index)
                                                }
                                            }
                                        }
                                        outer@ for (data in selectedPassengerData) {
                                            if (data.stationData.isNotEmpty()) {
                                                isEmpty = false
                                                break@outer
                                            }
                                        }

                                        val positionNoServices =
                                            selectedPassengerData.indexOfFirst { it.stationData.isEmpty() }
                                        if (positionNoServices != -1) {
                                            selectedPassengerData.removeAt(positionNoServices)
                                        }

                                        if (isEmpty) {
                                            selectedPassengerData.clear()
                                        }
                                    }
                                    val finalInput = JsonObject()
                                    val stationarray = JsonArray()
                                    Utility.stationarray = JsonArray()

                                    for (data in PackageServiceFragment.selectedPassengerData) {

                                        val serviceArray = JsonArray()
                                        val obj = JsonObject()

                                        for (serviceData in data.stationData) {
                                            serviceArray.add(JsonObject().apply {
                                                addProperty(
                                                    "service_token",
                                                    serviceData.service_token
                                                )
                                                addProperty("adult_count", serviceData.adult_count)
                                                addProperty(
                                                    "children_count",
                                                    serviceData.children_count
                                                )
                                                addProperty(
                                                    "service_date",
                                                    serviceData.service_date
                                                )
                                                addProperty(
                                                    "service_time",
                                                    serviceData.service_time
                                                )
                                                addProperty("notes", serviceData.notes)
                                            })
                                        }
                                        obj.add("service_array", serviceArray)
                                        obj.addProperty("ttr_token", data.ttr_token)

                                        stationarray.apply { add(obj) }
                                        Utility.stationarray.apply { add(obj) }
                                    }
                                    Log.d("stationArray", Utility.stationarray.toString())

                                    finalInput.addProperty(
                                        "user_token",
                                        SavedSharedPreference.getUserData(requireContext()).token
                                    )
                                    finalInput.add("station_array", stationarray)
                                    Utility.finalInput = finalInput

                                    packageServiceData[i].service_collection?.get(j)?.service_group?.get(
                                        k
                                    )?.service_array?.removeAt(l)
                                    PackageServiceFragment.totalCount.removeAt(0)
                                    toFindTotal()

//                                    totalArrayListAmount()
                                    PackageServiceFragment.total.removeAt(i)

                                    serviceArrayCount =PackageServiceFragment.total.sum()
                                    serviceArrayCount = PackageServiceFragment.total.removeAt(i)
                                    getTotal(serviceArrayCount.toString())
                                    (activity as MainActivity).badge(PackageServiceFragment.totalCount)
                                    summaryFragment.summaryRecyclerView.adapter = context?.let {
                                        SummaryLocationAdapter(
                                            it, packageServiceData, selectItem, shortValue,
                                            ::summaryFragControl,
                                            ::summaryGroup,
                                            ::summaryLocation,
                                        )
                                    }

                                }

//                                totalArrayListAmount()
                                return
                            }
                        }
                    }
                }
            }
        }

    }


    private fun totalArrayListAmount() {
        val total: ArrayList<Int> by lazy { arrayListOf() }

        for (m in 0 until PackageServiceFragment.passengerStationData.size) {

            for (i in 0 until packageServiceData.size) {
                for (j in 0 until packageServiceData[i].service_collection?.size!!) {
                    for (k in 0 until packageServiceData[i].service_collection?.get(j)?.service_group?.size!!) {
                        for (l in 0 until packageServiceData[i].service_collection?.get(j)?.service_group?.get(
                            k
                        )?.service_array?.size!!) {
                            if (packageServiceData[i].service_collection?.get(j)?.service_group?.get(
                                    k
                                )?.service_array?.get(l)!!.service_token == PackageServiceFragment.passengerStationData[m].service_token
                            ) {
                                packageServiceData[i].service_collection?.get(j)?.service_group?.get(
                                    k
                                )?.service_array?.get(l)?.totalAmount?.toInt()
                                    ?.let { total.add(it) }
//                                serviceArrayCount = total.sum()
                                serviceArrayCount =PackageServiceFragment.total.sum()
                                Log.d("paymentAmount", "totalArrayListAmount:$serviceArrayCount")
                                getTotal(serviceArrayCount.toString())
                            }

                        }

                    }
                }
            }
        }

    }


    private fun getTotal(totalServiceCost: String) {


        val jsonObject = JsonObject().apply {
            addProperty("total_service_cost", totalServiceCost)
            addProperty("discount_amount", discountCost)
            addProperty("currency", summaryFragment.summarySpinner.selectedItem.toString())
        }
        Log.d("server_call", "JsonObj: $jsonObject")
        ApiClient.APIinterface().getTotal(jsonObject)
            .enqueue(object : Callback<GetBookingTotal> {
                override fun onFailure(call: retrofit2.Call<GetBookingTotal>, t: Throwable) {
                    Log.d("getTotalFail", "onFailure: " + t.message.toString())
                }

                override fun onResponse(
                    call: retrofit2.Call<GetBookingTotal>,
                    response: retrofit2.Response<GetBookingTotal>
                ) {
                    if (response.isSuccessful) {

                        total = response.body()!!.total_amount
                        serviceCostGst = response.body()!!.service_cost_gst
                        convenienceCost = response.body()!!.convenience_cost
                        convenienceCostGst = response.body()!!.convenience_cost_gst
                        serviceCostExclGst = response.body()!!.service_cost_excl_gst

                        if (summaryFragment.summarySpinner.selectedItem == "USD") {
                            summaryFragment.summaryTotalAmountPrice.text = "$ " + total
                            summaryFragment.summaryServicePrice.text = "$ " + serviceCostExclGst
                            summaryFragment.conFeeTaxPrice.text = "$ " + convenienceCostGst
                            summaryFragment.conFeePrice.text = "$ " + convenienceCost
                            summaryFragment.summaryGstPrice.text = "$ " + serviceCostGst
                        } else {
                            summaryFragment.summaryTotalAmountPrice.text = "₹ " + total
                            summaryFragment.summaryServicePrice.text = "₹ " + serviceCostExclGst
                            summaryFragment.conFeeTaxPrice.text = "₹ " + convenienceCostGst
                            summaryFragment.conFeePrice.text = "₹ " + convenienceCost
                            summaryFragment.summaryGstPrice.text = "₹ " + serviceCostGst
                        }
                    } else {
                        Toast.makeText(requireContext(), "getTotal fail", Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }

    private fun createOrder() {

        finalInput.addProperty(
            "user_token", SavedSharedPreference.getUserData(requireContext()).token
        )
        finalInput.add("station_array", Utility.stationarray)
        finalInput.addProperty("currency", selectItem)
        finalInput.addProperty("coupon_code", couponCode)
        finalInput.addProperty("coupon_status", couponStatus)
        finalInput.addProperty("coupon_type", couponType)

        Log.d(TAG, "createOrder stationarray : ${Utility.stationarray}")
        Log.d(TAG, "createOrder finalInput : ${finalInput}")



        ApiClient.APIinterface().createOrder(finalInput)
            .enqueue(object : Callback<CreateOrderData> {
                override fun onFailure(call: retrofit2.Call<CreateOrderData>, t: Throwable) {
                }

                override fun onResponse(
                    call: retrofit2.Call<CreateOrderData>,
                    response: retrofit2.Response<CreateOrderData>
                ) {
                    try {
                        if (response.isSuccessful) {
                            loader.onChanged(false)
                            order_id = response.body()!!.order_id.toString()
                            paymentAmount = response.body()!!.payment_amount
                            rzpAuthKey = response.body()!!.rzp_authkey.toString()

                            Log.d(
                                TAG,
                                "summaryFragment createOrder paymentAmount :${paymentAmount} "
                            )

                            val passengerCount =
                                PackageServiceFragment.passengerStationData[0].adult_count + PackageServiceFragment.passengerStationData[0].children_count
                            val bundle = Bundle().apply {
                                putInt("passenger_count", passengerCount)
                            }
                            findNavController().navigate(
                                R.id.action_cart_to_CheckoutFragment,
                                bundle
                            )
                        } else {
                            context?.let { it1 -> alertToast(it1, "failure") }
                        }
                    } catch (e: Exception) {
                        alertToast(requireContext(), "failure")
                    }
                }
            })
    }

    private fun submitOTP(mobileNumber: String, otp: String, countryCode: String?) {
        val jsonObject = JsonObject().apply {
            addProperty("login_device", "Android")
            addProperty("country_code", countryCode)
            addProperty("mobile_number", mobileNumber)
            addProperty("otp", otp)
            addProperty("device_name", HomeFragment.deviceName)
            addProperty("device_id", HomeFragment.deviceID)
            addProperty("device_token", HomeFragment.deviceToken)
        }

        if (isNetworkConnected(requireContext())) {
            Log.d("server_call", "otpverifyCall: $jsonObject")
            lifecycleScope.launch {
                viewModel?.otpverifyCall(jsonObject = jsonObject)
                    ?.observe(requireActivity(), otpObserver)
            }
        } else {
            startActivity(Intent(requireContext(), NoInternetActivity::class.java))
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
            viewModel?.loginCall(jsonObject = jsonObject)?.observe(requireActivity(), logObserver)
        } else {
            startActivity(Intent(requireContext(), NoInternetActivity::class.java))
        }
    }

    @SuppressLint("SetTextI18n")
    private val logObserver = Observer<ApiResult<Any>> {
        when (it) {
            is ApiResult.Loading -> loader.onChanged(it.loading)
            is ApiResult.Success -> {

                val otpString = SpannableStringBuilder().apply {
                    append(resources.getString(R.string.otp_sent_to) + " ")
                    bold {
                        append("+")
                        append(countryCode)
                        append(mobileNumber)
                    }
                }
                otpView?.findViewById<TextView>(R.id.number)?.text = otpString.toString()
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                    otpView?.findViewById<TextView>(R.id.number)?.text = Html.fromHtml(
//                        resources.getString(R.string.otp_sent_to) + " " + "<b>" + mobileNumber + "</b>",
//                        Html.FROM_HTML_MODE_LEGACY
//                    )
//                }
                startTimer()
                otpView?.show()
            }
            is ApiResult.Error -> {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            }
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
                createOrder()
            }
            is ApiResult.Error -> {
                errorsAlert("Error", it.message)
            }
        }
    }


    private fun startTimer() {
        val timer = object : CountDownTimer(30000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
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
            }

            @SuppressLint("SetTextI18n")
            override fun onFinish() {
                otpView?.findViewById<TextView>(R.id.resend)?.text =
                    resources.getString(R.string.didnot_receive) + " " + resources.getString(R.string.resendcode)
                otpView?.findViewById<TextView>(R.id.resend)
                    ?.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                otpView?.findViewById<OtpView>(R.id.otpView)?.text = null
            }
        }
        timer.start()
    }

    private fun onBackPressed() {
        Navigation.findNavController(requireView()).popBackStack()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment


        return summaryFragment.root
    }


    fun summaryLocation(position: Int) {
        servicePosition = position
        Log.d("ragu", "summaryLocation: " + servicePosition)
    }

    fun summaryGroup(position: Int) {
        Log.d("ragu", "packagePosition: " + packagePosition)
        packagePosition = position

    }


    override fun onResume() {
        super.onResume()
        totalArrayListAmount()
        if (summaryFragment.couponCode.text == "Apply Coupon") {
            currency(summaryFragment.summarySpinner.selectedItem.toString())

        } else {
            onCouponSelected(couponCode)
        }

    }


}






