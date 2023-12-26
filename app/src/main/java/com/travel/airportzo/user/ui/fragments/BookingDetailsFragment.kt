package com.travel.airportzo.user.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.*
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.text.bold
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.JsonObject
import com.mukesh.OtpView
import com.travel.airportzo.user.R
import com.travel.airportzo.user.databinding.BookingDetailsFragmentBinding
import com.travel.airportzo.user.model.*
import com.travel.airportzo.user.network.ApiResult
import com.travel.airportzo.user.network.NoInternetActivity
import com.travel.airportzo.user.savedpreference.SavedSharedPreference
import com.travel.airportzo.user.ui.activity.ChatActivity
import com.travel.airportzo.user.ui.adapter.*
import com.travel.airportzo.user.ui.base.BaseFragment
import com.travel.airportzo.user.utils.setOnDebounceListener
import kotlinx.coroutines.launch
import java.io.File
import java.lang.reflect.Method
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt


class BookingDetailsFragment : BaseFragment() {

    private var orderDetailData: GetOrderDetailData? = null
    private val bookingDetailFragment by lazy { BookingDetailsFragmentBinding.inflate(layoutInflater) }

    private val bookingLocationData: ArrayList<GetOrderDetailData.Order_detail> by lazy { arrayListOf() }
    private val bookingPackageData: ArrayList<GetOrderDetailData.Order_detail_array> by lazy { arrayListOf() }
    private val bookingOtherPassengerData: ArrayList<GetOrderDetailData.Passenger_array> by lazy { arrayListOf() }
    private val bookingJourneyData: ArrayList<GetOrderDetailData.Journey_detail> by lazy { arrayListOf() }
    private val cancelBookingPackageData: ArrayList<GetOrderDetailData.Order_detail_array> by lazy { arrayListOf() }

    private var isCancelSpecificOrder: Boolean = false

    companion object {
       val gmt_view = ""
    }

    private val cancelBookingAdapter by lazy {
        context?.let {
            CancelAdapter(
                it,
                cancelBookingPackageData,
                ::onCancelBooking
            )
        }
    }
    private val otherPassengerAdapter by lazy {
        context?.let {
            OtherPassengerAdapter(
                it,
                bookingOtherPassengerData
            )
        }
    }
    private val journeyAdapter by lazy { context?.let { JourneyAdapter(it, bookingJourneyData) } }
    private val bookingLocationAdapter by lazy {
        context?.let {
            BookingLocationAdapter(
                it,
                bookingLocationData,
                ::airportName,
                ::review,
                ::addComment,
                ::viewReport,
                ::clicked,
                ::onCancel,
                ::onChat
            )
        }
    }

    private val reportData: ArrayList<String> by lazy { arrayListOf() }
    private val reportToken: ArrayList<ReportedData> by lazy { arrayListOf() }

    private val amttotal: ArrayList<Int> by lazy { arrayListOf() }
    private val amtfee: ArrayList<Int> by lazy { arrayListOf() }
    private val amtrefund: ArrayList<Int> by lazy { arrayListOf() }

    private var hourlist = ArrayList<Int>()
    var totalamtt = ArrayList<Int>()
    var feeamountt = ArrayList<Int>()
    var cancelamtt = ArrayList<Int>()

    var dropdown: String = ""
    var detailtoken: String = ""
    private var outedit: String = ""
    private var namee: String = ""
    private var terminal: String = ""
    private var ordertoken: String = ""
    private var cancelordertoken: String = ""
    private var cancelbookingtoken: String = ""
    private var cancelservicebookingtoken: String = ""
    private var orderreporttoken: String = ""
    var reportedtoken: String = ""
    private var bookingtoken: String = ""
    private var bookingstatus: String = ""
    private var bookingNumber: String = ""
    private var airportfee: Int = 0
    private var refunddairport: Int = 0
    private var totalairport: Int = 0
    private var ratings: String = ""
    var description: String = ""
    private var outspinner: String = ""
    private var hours: Int = 0
    private var percentage: Int = 0
    private var flightData: String = ""
    private var pdfurl: String = ""
    private var cancelledPdf: ArrayList<String> = ArrayList()

    private val db = Firebase.firestore

    private val reportProblem by lazy {
        context?.let {
            BottomSheetDialog(it, R.style.MyBottomSheetDialogTheme).apply {
                setContentView(layoutInflater.inflate(R.layout.bottomsheet_report, null))

                /** brand color*/
                val reportProblem = findViewById<MaterialButton>(R.id.checkOut)
                reportProblem?.setBackgroundColor(Color.parseColor(activity?.let { it1 ->
                    SavedSharedPreference.getCustomColor(
                        it1
                    ).brand_colour
                }))

                setCancelable(true)
                setOnShowListener {
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
        }
    }

    private val cancelBooking by lazy {
        context?.let {
            BottomSheetDialog(it, R.style.MyBottomSheetDialogTheme).apply {
                setContentView(layoutInflater.inflate(R.layout.bottomsheet_bookingcancel, null))
                /** brand color*/
                val checkOutBtn = findViewById<MaterialButton>(R.id.checkOut)
                checkOutBtn?.setBackgroundColor(Color.parseColor(activity?.let { it1 ->
                    SavedSharedPreference.getCustomColor(
                        it1
                    ).brand_colour
                }))
                setCancelable(true)
                setOnShowListener {
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
        }
    }

    private val checkStatusSheet by lazy {
        context?.let {
            BottomSheetDialog(it, R.style.MyBottomSheetDialogTheme).apply {
                setContentView(layoutInflater.inflate(R.layout.bottomsheet_checkstatus, null))
                setCancelable(true)
                setOnShowListener {
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
        }
    }

    private val cancelBookingService by lazy {
        context?.let {
            BottomSheetDialog(it, R.style.MyBottomSheetDialogTheme).apply {
                setContentView(layoutInflater.inflate(R.layout.bottomsheet_servicecancel, null))
                setCancelable(true)
            }
        }
    }


    private val review by lazy {
        context?.let {
            BottomSheetDialog(it, R.style.MyBottomSheetDialogTheme).apply {
                setContentView(layoutInflater.inflate(R.layout.bottomsheet_review, null))

                /** Brand Color*/
                val submitBtn = findViewById<MaterialButton>(R.id.submit)
                submitBtn?.setBackgroundColor(Color.parseColor(activity?.let { it1 ->
                    SavedSharedPreference.getCustomColor(
                        it1
                    ).brand_colour
                }))

                setCancelable(true)
                setOnShowListener {
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
        }
    }


    private val commentSheet by lazy {
        context?.let {
            BottomSheetDialog(it, R.style.MyBottomSheetDialogTheme).apply {
                setContentView(layoutInflater.inflate(R.layout.bottomsheet_addcomment, null))

                /** Brand Color*/
                val submitBtn = findViewById<MaterialButton>(R.id.submit)
                submitBtn?.setBackgroundColor(Color.parseColor(activity?.let { it1 ->
                    SavedSharedPreference.getCustomColor(
                        it1
                    ).brand_colour
                }))

                setCancelable(true)
                setOnShowListener {
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
        }
    }


    private fun onChat(
        orderDetailArray: GetOrderDetailData.Order_detail_array,
        holder: BookingServiceAdapter.MyViewHolder
    ) {
        val chatToken = orderDetailArray.token
        val chatService = orderDetailArray.company_name

        db.collection("service").document(chatToken).get()
            .addOnSuccessListener { document ->
                Log.d("manoj", document.data?.get("participants").toString())
                if (document.data?.get("participants") == null) {
                    Toast.makeText(requireContext(), "No Agent Allocated", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    startActivity(
                        Intent(requireContext(), ChatActivity::class.java)
                            .putExtra("chatToken", chatToken)
                            .putExtra("chatService", chatService)
                    )
                }
            }
    }

    @SuppressLint("NewApi", "SetTextI18n")
    private fun onCancelBooking(
        orderDetailArray: GetOrderDetailData.Order_detail_array,
        holder: CancelAdapter.MyViewHolder
    ) {
//        if (orderDetailArray.status == "Cancelled") {
//            Toast.makeText(requireContext(), "Already Cancelled", Toast.LENGTH_SHORT).show()
//        } else {

            cancelservicebookingtoken = orderDetailArray.booking_token

            holder.cancelAdapterBinding.airportName.text = orderDetailArray.airport_name
            holder.cancelAdapterBinding.companyName.text = orderDetailArray.company_name
            holder.cancelAdapterBinding.cancelAdapterServiceDateTime.text =
                orderDetailArray.service_date_time + " (GMT ${orderDetailArray.airport_gmt})"
            holder.cancelAdapterBinding.cancelAdapterServiceCost.text =
                "${getString(R.string.indianRupee)} ${orderDetailArray.net_amount}"
            holder.cancelAdapterBinding.convenienceFee.text =
                "${getString(R.string.indianRupee)} ${(orderDetailArray.agent_conv_fee.toInt() + orderDetailArray.user_conv_fee.toInt())}"
            holder.cancelAdapterBinding.cancelAdapterCancellationFee.text =
                "${getString(R.string.indianRupee)} ${orderDetailArray.cancellation_detail.cancellation_fee}"
            holder.cancelAdapterBinding.cancelAdapterRefundAmount.text =
                "${getString(R.string.indianRupee)} ${orderDetailArray.cancellation_detail.refund_amount}"
            holder.cancelAdapterBinding.cancelAdapterPlatformFees.text =
                "${getString(R.string.indianRupee)} ${orderDetailArray.cancellation_detail.airportzo_fee}"

            if (orderDetailArray.status.lowercase() == "completed") {
                holder.cancelAdapterBinding.status.visibility = View.VISIBLE
                holder.cancelAdapterBinding.serviceStatus.visibility = View.VISIBLE
                holder.cancelAdapterBinding.cancelOrder.visibility = View.GONE
                holder.cancelAdapterBinding.serviceStatus.text = orderDetailArray.status
            } else if (orderDetailArray.status.lowercase() == "cancelled") {
                holder.cancelAdapterBinding.status.visibility = View.VISIBLE
                holder.cancelAdapterBinding.serviceStatus.visibility = View.VISIBLE
                holder.cancelAdapterBinding.cancelOrder.visibility = View.GONE
                holder.cancelAdapterBinding.serviceStatus.text = orderDetailArray.cancellation_detail.refund_status
                holder.cancelAdapterBinding.status.text = "Refund Status"
            } else {
                holder.cancelAdapterBinding.status.visibility = View.GONE
                holder.cancelAdapterBinding.serviceStatus.visibility = View.GONE
                holder.cancelAdapterBinding.cancelOrder.visibility = View.VISIBLE
            }

            val discount =
                orderDetailArray.adult_discount.toInt() + orderDetailArray.child_discount.toInt() +
                        orderDetailArray.add_adult_discount.toInt() + orderDetailArray.add_child_discount.toInt()

            if (discount == 0) {
                holder.cancelAdapterBinding.discountAmount.text = "-"
            } else {
                holder.cancelAdapterBinding.discountAmount.text =
                    "${getString(R.string.indianRupee)} $discount"
            }

            if (orderDetailArray.cancellation_detail.cancellation_hours == "0") {
                holder.cancelAdapterBinding.cancelAdapterCancelBeforeHours.text = "-"
            } else {
                holder.cancelAdapterBinding.cancelAdapterCancelBeforeHours.text =
                    "${orderDetailArray.cancellation_detail.cancellation_hours} Hrs"
            }

            context?.let {
                Glide.with(it).load(orderDetailArray.company_logo)
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.cancelAdapterBinding.companyImage)
            }

            holder.cancelAdapterBinding.cancelOrder.setOnDebounceListener {

                cancelordertoken = orderDetailArray.token
                cancelbookingtoken = orderDetailArray.booking_token

                val checkbox = cancelBooking?.findViewById<CheckBox>(R.id.cancelCheckbox)
                if (checkbox?.isChecked == true) {
                    isCancelSpecificOrder = true
                    logIn()
                    otpView?.findViewById<MaterialButton>(R.id.verifyOtp)?.setOnDebounceListener {
                        submitOTP(
                            mobileNumber = SavedSharedPreference.getUserData(requireContext()).mobile.toString(),
                            countryCode = SavedSharedPreference.getUserData(requireContext()).code.toString(),
                            otp = otpView?.findViewById<OtpView>(R.id.otpView)?.text.toString()
                        )
                    }
                    checkbox.isChecked = false
                } else {
                    Toast.makeText(requireContext(), "CheckBox is empty", Toast.LENGTH_SHORT).show()
                }
            }

            if (orderDetailData != null) {

                var totalServiceCost = 0
                var totalDiscount = 0
                var totalCancellationAmount = 0.0
                var totalPlatformFee = 0
                var totalRefund = 0
                var totalConvenience = 0

                for (data in cancelBookingPackageData) {
                    if (data.status == "Pending") {

                        val addDiscount =
                            data.adult_discount.toInt() + data.child_discount.toInt() +
                                    data.add_adult_discount.toInt() + data.add_child_discount.toInt()

                        totalServiceCost += data.net_amount.toDouble().toInt()
                        totalDiscount += addDiscount
                        totalCancellationAmount += data.cancellation_detail.cancellation_fee.toDouble()
                        totalPlatformFee += data.cancellation_detail.airportzo_fee.toInt()
                        totalRefund += data.cancellation_detail.refund_amount.toInt()
                        totalConvenience += (data.agent_conv_fee.toInt() + data.user_conv_fee.toInt())
                    }
                }

                cancelBooking?.findViewById<TextView>(R.id.serviceCost)?.text =
                    "${getString(R.string.indianRupee)} $totalServiceCost"
                cancelBooking?.findViewById<TextView>(R.id.totalConvenienceFee)?.text =
                    "${getString(R.string.indianRupee)} $totalConvenience"
                cancelBooking?.findViewById<TextView>(R.id.totalDiscount)?.text =
                    "${getString(R.string.indianRupee)} $totalDiscount"
                cancelBooking?.findViewById<TextView>(R.id.cancellationFee)?.text =
                    "-${getString(R.string.indianRupee)} ${totalCancellationAmount.roundToInt()}"
                cancelBooking?.findViewById<TextView>(R.id.platformFee)?.text =
                    "-${getString(R.string.indianRupee)} $totalPlatformFee"
                cancelBooking?.findViewById<TextView>(R.id.refundAmount)?.text =
                    "${getString(R.string.indianRupee)} $totalRefund"
            }
//        }
        cancelBooking?.findViewById<Button>(R.id.checkOut)?.setOnDebounceListener {
            val checkbox = cancelBooking?.findViewById<CheckBox>(R.id.cancelCheckbox)
            if (checkbox?.isChecked == true) {
                isCancelSpecificOrder = false
                logIn()
                otpView?.findViewById<MaterialButton>(R.id.verifyOtp)?.setOnDebounceListener {
                    submitOTP(
                        mobileNumber = SavedSharedPreference.getUserData(requireContext()).mobile.toString(),
                        countryCode = SavedSharedPreference.getUserData(requireContext()).code.toString(),
                        otp = otpView?.findViewById<OtpView>(R.id.otpView)?.text.toString()
                    )
                }
                checkbox.isChecked = false
            } else {
                Toast.makeText(requireContext(), "CheckBox is empty", Toast.LENGTH_SHORT).show()
            }
        }
    }


    @SuppressLint("NewApi")
    private fun onCancel(
        orderDetailArray: GetOrderDetailData.Order_detail_array,
        holder: BookingServiceAdapter.MyViewHolder
    ) {
//        if (orderDetailArray.cancellation_policy_detail.isEmpty()) {
//            Toast.makeText(requireContext(), "No Cancel in this Service ", Toast.LENGTH_SHORT)
//                .show()
//        } else {
//            hourlist.clear()
//            for (i in 0 until orderDetailArray.cancellation_policy_detail.size) {
//                hourlist.add(orderDetailArray.cancellation_policy_detail[i].hours.toInt())
//            }
//            val cancelimage = cancelBookingService?.findViewById<ImageView>(R.id.cancelPackageImage)
//            val totalamount = orderDetailArray.net_amount
//            val datetimeone = orderDetailArray.service_date_time_raw
//
//
//            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
//            val current = LocalDateTime.now().format(formatter)
//
//            println(current)
//
//            val formate = SimpleDateFormat("yyyy-MM-dd HH:mm")
//
//
//            var outone = formate.parse(datetimeone)
//            var outtwo = formate.parse(current)
//
//            val diff = outone.time - outtwo.time
//
//            val seconds = diff / 1000
//            val minutes = seconds / 60
//            hours = (minutes / 60).toInt()
//
//
//            fun List<Int>.closestValue(value: Int) = minBy { abs(value - it) }
//            val values = ArrayList(hourlist)
//            val totalhours = values.closestValue(hours.toInt())
//            for (i in 0 until orderDetailArray.cancellation_policy_detail.size) {
//                if (orderDetailArray.cancellation_policy_detail.isNotEmpty()) {
//                    if (orderDetailArray.cancellation_policy_detail[i].hours == totalhours.toString()) {
//                        percentage =
//                            orderDetailArray.cancellation_policy_detail[i].percentage.toInt()
//                        print(percentage)
//                    }
//                }
//            }
//
//            if (percentage == 0) {
//                cancelBookingService?.findViewById<TextView>(R.id.cancelRefundAmount)?.text =
//                    getString(R.string.indianRupee) + totalamount
//                cancelBookingService?.findViewById<TextView>(R.id.cancelFeeAmount)?.text =
//                    getString(R.string.indianRupee) + "0"
//                cancelBookingService?.findViewById<TextView>(R.id.cancelFeeAmount)?.text =
//                    getString(R.string.indianRupee) + "0"
//                cancelBookingService?.findViewById<TextView>(R.id.cancelRefundAmount)?.text =
//                    getString(R.string.indianRupee) + totalamount
//            } else if (hours <= 0) {
//                cancelBookingService?.findViewById<TextView>(R.id.cancelRefundAmount)?.text =
//                    getString(R.string.indianRupee) + "0"
//                cancelBookingService?.findViewById<TextView>(R.id.cancelFeeAmount)?.text =
//                    getString(R.string.indianRupee) + totalamount
//                cancelBookingService?.findViewById<TextView>(R.id.cancelFeeAmount)?.text =
//                    getString(R.string.indianRupee) + totalamount
//                cancelBookingService?.findViewById<TextView>(R.id.cancelRefundAmount)?.text =
//                    getString(R.string.indianRupee) + "0"
//                cancelBookingService?.findViewById<TextView>(R.id.cancelBeforeHours)?.text = "0"
//            } else if (hours > 48) {
//                cancelBookingService?.findViewById<TextView>(R.id.cancelRefundAmount)?.text =
//                    getString(R.string.indianRupee) + totalamount
//                cancelBookingService?.findViewById<TextView>(R.id.cancelFeeAmount)?.text =
//                    getString(R.string.indianRupee) + "0"
//                cancelBookingService?.findViewById<TextView>(R.id.cancelFeeAmount)?.text =
//                    getString(R.string.indianRupee) + "0"
//                cancelBookingService?.findViewById<TextView>(R.id.cancelRefundAmount)?.text =
//                    getString(R.string.indianRupee) + totalamount
//            } else {
//                val calculation = totalamount.toInt() * percentage
//                val total = calculation / 100
//                val refund = totalamount.toInt() - total
//                if (refund > airportfee) {
//                    totalairport = total + airportfee
//                    refunddairport = refund - airportfee
//                }
//
//                cancelBookingService?.findViewById<TextView>(R.id.cancelRefundAmount)?.text =
//                    getString(R.string.indianRupee) + refund.toString()
//                cancelBookingService?.findViewById<TextView>(R.id.cancelFeeAmount)?.text =
//                    getString(R.string.indianRupee) + total.toString()
//                cancelBookingService?.findViewById<TextView>(R.id.cancelFeeAmount)?.text =
//                    "- ${getString(R.string.indianRupee)} $totalairport"
//                cancelBookingService?.findViewById<TextView>(R.id.cancelRefundAmount)?.text =
//                    "₹ $refunddairport"
//            }
//            cancelBookingService?.findViewById<TextView>(R.id.textView2)?.text =
//                orderDetailArray.service_date_time
//            cancelBookingService?.findViewById<TextView>(R.id.cancelBeforeHours)?.text =
//                "$hours" + "hrs"
//            cancelBookingService?.findViewById<TextView>(R.id.cCardName)?.text =
//                orderDetailArray.company_name
//            cancelBookingService?.findViewById<TextView>(R.id.cancelTime)?.text =
//                "Booked on :" + orderDetailArray.date_time
//            cancelBookingService?.findViewById<TextView>(R.id.cancelServiceCostAmount)?.text =
//                orderDetailArray.net_amount
//            cancelBookingService?.findViewById<TextView>(R.id.cancelTotalAmount)?.text =
//                getString(R.string.indianRupee) + orderDetailArray.net_amount
//            cancelBookingService?.findViewById<TextView>(R.id.platformAmount)?.text =
//                getString(R.string.indianRupee) + orderDetailArray.net_amount
//            cancelBookingService?.findViewById<TextView>(R.id.cancelServiceName)?.text = namee
//
//
//            context?.let {
//                if (cancelimage != null) {
//                    Glide.with(it).load(orderDetailArray.company_logo)
//                        .apply(RequestOptions.circleCropTransform()).into(cancelimage)
//                }
//            }
//            cancelBookingService?.show()
//            cancelordertoken = orderDetailArray.token
//            cancelbookingtoken = orderDetailArray.booking_token
//        }
//        cancelBookingService?.findViewById<Button>(R.id.checkOut)?.setOnDebounceListener {
//            val checkbox = cancelBookingService?.findViewById<CheckBox>(R.id.cancelCheckbox)
//            if (checkbox?.isChecked == true) {
//                cancelbookingservices()
//                findNavController().navigate(R.id.action_navigation_booking_details_to_navigation_booking)
//                cancelBookingService?.dismiss()
//            } else {
//                Toast.makeText(requireContext(), "CheckBox is empty", Toast.LENGTH_SHORT).show()
//            }
//        }
    }


    private fun airportName(orderDetail: GetOrderDetailData.Order_detail) {
        namee = orderDetail.airport_name
        terminal = orderDetail.terminal_name
    }


    private fun review(orderDetailArray: GetOrderDetailData.Order_detail_array) {
        val reviewimage = review?.findViewById<ImageView>(R.id.img2)
        ordertoken = orderDetailArray.token
        orderDetailArray.company_name.also {
            review?.findViewById<TextView>(R.id.packageName)?.text = it
        }
        review?.findViewById<TextView>(R.id.packageLocation)?.text = namee
        context?.let {
            if (reviewimage != null) {
                Glide.with(it).load(orderDetailArray.company_logo)
                    .apply(RequestOptions.circleCropTransform()).into(reviewimage)
            }
        }
        review?.show()
        reportProblem?.dismiss()
    }

    private fun viewReport(orderDetailArray: GetOrderDetailData.Order_detail_array) {
        errorsAlert(
            title = orderDetailArray.report_reason,
            message = orderDetailArray.report_description
        )
    }

    private fun addComment(orderDetailArray: GetOrderDetailData.Order_detail_array) {
        val userImage = commentSheet?.findViewById<ImageView>(R.id.userImage)
        val comment = commentSheet?.findViewById<EditText>(R.id.reviewEdit)
        val submit = commentSheet?.findViewById<MaterialButton>(R.id.submit)
        val commentContent = commentSheet?.findViewById<TextView>(R.id.commentContent)
        val commentTime = commentSheet?.findViewById<TextView>(R.id.commentTime)
        val closeButton = commentSheet?.findViewById<ImageButton>(R.id.closeButton)

        closeButton?.setOnDebounceListener { commentSheet?.dismiss() }

        if (orderDetailArray.comment == "") {
            comment?.visibility = View.VISIBLE
            submit?.visibility = View.VISIBLE
            commentContent?.visibility = View.GONE
            commentTime?.visibility = View.GONE
        } else {
            comment?.visibility = View.INVISIBLE
            submit?.visibility = View.INVISIBLE
            commentContent?.visibility = View.VISIBLE
            commentTime?.visibility = View.VISIBLE

            commentContent?.text = orderDetailArray.comment
            commentTime?.text = orderDetailArray.comment_date_time
        }

        val userImageUrl = SavedSharedPreference.getImageUrl(context = requireContext())
        val userName = SavedSharedPreference.getUserData(requireContext()).name
        Glide.with(requireContext()).load(userImageUrl)
            .apply(RequestOptions.circleCropTransform()).into(userImage!!)
        commentSheet?.findViewById<TextView>(R.id.userName)!!.text = userName
        commentSheet?.findViewById<RatingBar>(R.id.rating)!!.rating =
            orderDetailArray.rating.toInt().toFloat()
        commentSheet?.show()

        submit?.setOnDebounceListener {
            if (TextUtils.isEmpty(comment?.text.toString())) {
                alertToast(requireContext(), "Please add comment")
                return@setOnDebounceListener
            }

            addComment(bookingToken = orderDetailArray.token, comment = comment?.text.toString())
            commentSheet?.dismiss()
        }
    }


    private fun clicked(
        orderDetailArray: GetOrderDetailData.Order_detail_array,
        holder: BookingServiceAdapter.MyViewHolder
    ) {

        val reportSpinnerAdapter = ArrayAdapter(
            requireContext(),
            com.mukesh.R.layout.support_simple_spinner_dropdown_item,
            reportData
        )

        val passengerNameSpinner = reportProblem?.findViewById<Spinner>(R.id.passengerNameSpinner)
        passengerNameSpinner!!.adapter = reportSpinnerAdapter

        passengerNameSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                for (data in reportToken) {
                    if (data.reason == reportData[position]) {
                        reportedtoken = data.token
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

//        reportbottom()
        val image = reportProblem?.findViewById<ImageView>(R.id.img2)
        orderreporttoken = orderDetailArray.token
        orderDetailArray.company_name.also {
            reportProblem?.findViewById<TextView>(R.id.packageName)?.text = it
        }
        reportProblem?.findViewById<TextView>(R.id.packageLocation)?.text = namee
        context?.let {
            if (image != null) {
                Glide.with(it).load(orderDetailArray.company_logo)
                    .apply(RequestOptions.circleCropTransform()).into(image)
            }
        }
        review?.dismiss()
        reportProblem?.show()


        for (data in reportToken) {
            if (data.reason == reportData[passengerNameSpinner.selectedItemPosition]) {
                reportedtoken = data.token
            }
        }

    }


    private fun reportbottom() {


        reportProblem?.findViewById<Spinner>(R.id.passangername_spinner)?.adapter = context.let {
            context?.let { it1 ->
                ArrayAdapter(
                    it1,
                    androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                    reportData
                )
            }
        } as CustomSpinnerAdapter

        reportProblem?.findViewById<Spinner>(R.id.passangername_spinner)?.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    (parent!!.getChildAt(0) as TextView).setTextColor(Color.BLACK)
                    dropdown = parent.getItemAtPosition(position).toString()
                    for (i in 0 until reportToken.size) {
                        if (dropdown == reportToken[i].reason) {
                            reportedtoken = reportToken[i].token
                        }
                    }
                }
            }
    }


    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bookingDetailFragment.checkStatus.setOnDebounceListener {

            val statusArray: ArrayList<GetOrderDetailData.Order_detail_array> = ArrayList()

            for (data in orderDetailData!!.order_detail) {
                statusArray.addAll(data.order_detail_array)
            }

            val checkStatusAdapter =
                CheckStatusAdapter(context = requireContext(), bookingPackageData = statusArray)

            val recyclerview =
                checkStatusSheet?.findViewById<RecyclerView>(R.id.statusRecyclerview)
            val closeButton =
                checkStatusSheet?.findViewById<ImageButton>(R.id.closeButton)

            recyclerview?.adapter = checkStatusAdapter

            checkStatusSheet?.show()

            closeButton?.setOnDebounceListener { checkStatusSheet?.dismiss() }

        }

        if (bookingstatus == "Cancelled") {
            bookingDetailFragment.cancelButton.visibility = View.GONE
            bookingDetailFragment.checkStatus.visibility = View.VISIBLE
        } else {
            bookingDetailFragment.cancelButton.setOnDebounceListener {
                cancelBookingPackageData.clear()
                amttotal.clear()
                amtfee.clear()
                amtrefund.clear()
                hourlist.clear()

                for (k in 0 until bookingPackageData.size) {
                    if (bookingPackageData[k].cancellation_policy_detail.isEmpty()) {
                        Toast.makeText(
                            requireContext(),
                            "No Cancel in this Service",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
//                    else if (bookingPackageData[k].status == "Cancelled") {
////                        Toast.makeText(requireContext(), "Already Cancelled", Toast.LENGTH_SHORT)
////                            .show()
//                    }
                    else {
                        cancelBookingPackageData.add(bookingPackageData[k])
                        val recyclerview =
                            cancelBooking?.findViewById<RecyclerView>(R.id.cancelRecycler)
                        recyclerview?.adapter = cancelBookingAdapter
                        cancelBooking?.show()
                    }
                }
            }
        }
lifecycleScope.launch {
        viewModel?.reported()?.observe(requireActivity(), reported)
}

        if (CheckoutFragment.passengerCreateDataOthers.isEmpty() || CheckoutFragment.passengerCreateDataServices.isEmpty()) {
            bookingDetailFragment.bookingServiceContactDetails.visibility = View.GONE
            bookingDetailFragment.bookingServiceContactMobile.visibility = View.GONE
            bookingDetailFragment.bookingOtherPassengerDetails.visibility = View.GONE
            bookingDetailFragment.bookingOtherPassengerRecycler.visibility = View.GONE
            bookingDetailFragment.bookingServiceContactPerson.visibility = View.GONE
        }

        bookingDetailFragment.bookingTicketImage.setOnDebounceListener {
            Toast.makeText(context, "Download succesfully", Toast.LENGTH_SHORT).show()
        }

        bookingDetailFragment.cContactBtn.setOnDebounceListener {
            downLoadPdf()
        }

//Report Problem

        val transistOnefrom = reportProblem?.findViewById<EditText>(R.id.descriptionEdit)
        val transistOneto = reportProblem?.findViewById<Spinner>(R.id.passangername_spinner)

        reportProblem?.findViewById<Button>(R.id.reportProblem)?.setOnDebounceListener {

            outspinner = transistOneto?.selectedItem.toString()
            outedit = transistOnefrom?.text.toString()

            if (outspinner.isEmpty()) {
                Toast.makeText(context, "Please choose reason", Toast.LENGTH_SHORT).show()
            } else if (outedit.isEmpty()) {
                Toast.makeText(context, "Description field is empty", Toast.LENGTH_SHORT).show()
            } else {
                sendreport()
                reportProblem?.findViewById<EditText>(R.id.descriptionEdit)?.text?.clear()
                reportProblem?.dismiss()
            }
        }

        //Review

        val rating = review?.findViewById<RatingBar>(R.id.rating)
        val ratingdes = review?.findViewById<EditText>(R.id.reviewEdit)

        review?.findViewById<Button>(R.id.submit)?.setOnDebounceListener {
            ratings = rating?.rating.toString()
            description = ratingdes?.text.toString()

            if (ratings.isEmpty()) {
                Toast.makeText(context, "Review is empty", Toast.LENGTH_SHORT).show()
            } else if (description.isEmpty()) {
                Toast.makeText(context, "Description field is empty", Toast.LENGTH_SHORT).show()
            } else {
                sendreview()
                rating?.rating = 0F
                ratingdes?.text?.clear()
                Toast.makeText(context, "your review has been submitted", Toast.LENGTH_SHORT).show()
                review?.dismiss()
            }
        }


        flightData = arguments?.getString("token")!!
        println(flightData)

        bookingDetailFragment.bookingBack.setOnDebounceListener {
            Navigation.findNavController(requireView()).popBackStack()
        }

        bookingticketList()

//        bookingDetailFragment.downloadSpinner.onItemSelectedListener =
//            object : AdapterView.OnItemSelectedListener {
//                override fun onNothingSelected(parent: AdapterView<*>?) {
//                }
//
//                override fun onItemSelected(
//                    parent: AdapterView<*>?,
//                    view: View?,
//                    position: Int,
//                    id: Long
//                ) {
//                    (parent!!.getChildAt(0) as TextView).setTextColor(Color.WHITE)
//                    alertToast(requireContext(), position.toString())
//                    when (position) {
//
////                        0 -> {
////                            pdfurl.let {
////                                val fileName = File(it).name
////                                downloadFile(fileName = fileName, url = it)
////                            }
////                        }
////
////                        1 -> {
////                            for (data in cancelledPdf) {
////                                val fileName = File(data).name
////                                downloadFile(fileName = fileName, url = data)
////                            }
////                        }
//                    }
//                }
//            }
    }


    private fun cancelCompleteBooking() {
        val token = SavedSharedPreference.getUserData(requireContext()).token
        val jsonObject = JsonObject().apply {
            addProperty("user_token", token)
            addProperty("booking_token", cancelservicebookingtoken)
        }
        if (isNetworkConnected(requireContext())) {
            lifecycleScope.launch {
            viewModel?.cancelBooking(jsonObject = jsonObject)
                ?.observe(requireActivity(), cancelservicebooking)
            }
        } else {
            startActivity(Intent(requireContext(), NoInternetActivity::class.java))
        }
    }

    private val cancelservicebooking = Observer<ApiResult<Any>> {

        when (it) {
            is ApiResult.Success -> {
                cancelBooking?.dismiss()
                showCancelResponseDialog(bookingNumber)
            }
            is ApiResult.Error -> {
                errors("Error", it.message)
            }
            is ApiResult.Loading -> loader.onChanged(it.loading)
        }
    }


    private fun sendreview() {
        val token = SavedSharedPreference.getUserData(requireContext()).token
        val jsonObject = JsonObject().apply {
            addProperty("order_token", ordertoken)
            addProperty("rating", ratings)
            addProperty("review", description)
        }
        if (isNetworkConnected(requireContext())) {
            lifecycleScope.launch{
            viewModel?.updatereview(jsonObject = jsonObject)?.observe(requireActivity(), updatereview)
            }
        } else {
            startActivity(Intent(requireContext(), NoInternetActivity::class.java))
        }
    }

    private fun addComment(bookingToken: String, comment: String) {
        val jsonObject = JsonObject().apply {
            addProperty("booking_detail_token", bookingToken)
            addProperty("comment_desc", comment)
        }
        if (isNetworkConnected(requireContext())) {
            lifecycleScope.launch {
            viewModel?.updateComment(jsonObject = jsonObject)
                ?.observe(requireActivity(), updatereview)
            }
        } else {
            startActivity(Intent(requireContext(), NoInternetActivity::class.java))
        }
    }

    private val updatereview = Observer<ApiResult<DefaultResponse>> {
        when (it) {
            is ApiResult.Success -> {
                alertToast(requireContext(), it.data.message)
            }
            is ApiResult.Error -> {
                errors("Error", it.message)
            }
            else -> {}
        }
    }


    private fun sendreport() {
        val jsonObject = JsonObject().apply {
            addProperty("token", bookingtoken)
            addProperty("detail_token", orderreporttoken)
            addProperty("report_token", reportedtoken)
            addProperty("description", outedit)
        }
        if (isNetworkConnected(requireContext())) {
            lifecycleScope.launch {
                viewModel?.updatereport(jsonObject = jsonObject)
                    ?.observe(requireActivity(), updateReport)
            }
        } else {
            startActivity(Intent(requireContext(), NoInternetActivity::class.java))
        }
    }

    private val updateReport = Observer<ApiResult<UpdateReportClass>> {
        when (it) {
            is ApiResult.Success -> {
                alertToast(requireContext(), it.data.message)
            }
            is ApiResult.Error -> {
                errors("Error", it.message)
            }
            else -> {}
        }
    }


    private val reported = Observer<ApiResult<List<ReportedData>>> {
        when (it) {
            is ApiResult.Success -> {
                reportData.clear()
                for (i in 0 until it.data.size)
                    reportData.addAll(listOf(it.data[i].reason))
                reportToken.addAll(it.data)
            }
            is ApiResult.Error -> {
                errors("Error", it.message)
            }
            else -> {}
        }
    }


    private fun cancelSpecificOrder() {
        val token = SavedSharedPreference.getUserData(requireContext()).token
        val jsonObject = JsonObject().apply {
            addProperty("user_token", token)
            addProperty("booking_token", cancelbookingtoken)
            addProperty("order_detail_token", cancelordertoken)
        }
        if (isNetworkConnected(requireContext())) {
            lifecycleScope.launch {
            viewModel?.cancelBookingOrder(jsonObject = jsonObject)
                ?.observe(requireActivity(), cancelservice)
            }
        } else {
            startActivity(Intent(requireContext(), NoInternetActivity::class.java))
        }
    }

    private val cancelservice = Observer<ApiResult<Any>> {

        when (it) {
            is ApiResult.Success -> {
                loader.onChanged(false)
                cancelBooking?.dismiss()
                showCancelResponseDialog(bookingNumber)
            }
            is ApiResult.Error -> {
                errors("Error", it.message)
            }
            is ApiResult.Loading -> loader.onChanged(it.loading)
        }
    }


    private fun bookingticketList() {
        val jsonObject = JsonObject().apply {
            addProperty("token", flightData)
//            addProperty("token", "9UTfb91fb2")
        }
        if (isNetworkConnected(requireContext())) {
            Log.d("server_call", "getOrder: $jsonObject")
            lifecycleScope.launch {
            viewModel?.getOrder(jsonObject = jsonObject)?.observe(requireActivity(), orderDetails)
            }
        } else {
            startActivity(Intent(requireContext(), NoInternetActivity::class.java))
        }
    }

    @SuppressLint("SetTextI18n")
    private val orderDetails = Observer<ApiResult<GetOrderDetailData>> {
        when (it) {
            is ApiResult.Loading -> loader.onChanged(it.loading)
            is ApiResult.Success -> {
                orderDetailData = it.data
                if (it.data.gst_name.isEmpty() && it.data.gst_number.isEmpty()) {
                    bookingDetailFragment.bookingGstDetails.visibility = View.GONE
                    bookingDetailFragment.bookingGstContactPerson.visibility = View.GONE
                    bookingDetailFragment.bookingGstContactPersonName.visibility = View.GONE
                    bookingDetailFragment.bookinggstNumber.visibility = View.GONE
                    bookingDetailFragment.bookingGstNo.visibility = View.GONE
                }
                pdfurl = it.data.invoice_pdf

                cancelledPdf.clear()
                if (it.data.serviceCancelledPDf != null){
                    cancelledPdf.addAll(it.data.serviceCancelledPDf)
                }

                bookingDetailFragment.discountAmount.text =
                    "- ${getString(R.string.indianRupee)} ${it.data.total_discount_amt}"
                bookingDetailFragment.bookingPortName.text = it.data.journey
                bookingDetailFragment.bookingIdNo.text = it.data.booking_number
                bookingDetailFragment.bookingTotalServiceNo.text = it.data.total_service
                bookingDetailFragment.bookingPassengerDetails.text = it.data.total_adult
                if (it.data.total_children == "0") {
                    bookingDetailFragment.childCount.visibility = View.GONE
                    bookingDetailFragment.child.visibility = View.GONE
                }
                bookingDetailFragment.childCount.text = it.data.total_children
                bookingDetailFragment.bookingTotal.text = "${getString(R.string.indianRupee)}${it.data.total_amount}"
                bookingDetailFragment.convenienceAmount.text =
                    getString(R.string.indianRupee) + " " + it.data.convenience_fee
                bookingDetailFragment.convenienceTaxAmount.text =
                    getString(R.string.indianRupee) + " " + it.data.cf_tax

                val serviceType = StringBuilder()

                for (orderData in it.data.order_detail) {
                    if (!TextUtils.isEmpty(serviceType.toString())) {
                        serviceType.append(", ")
                    }
                    serviceType.append(orderData.airport_type)
                }

                bookingDetailFragment.bookingJourneyType.text = serviceType.toString()
                bookingDetailFragment.bookingDateView.text = it.data.date_time
                bookingtoken = it.data.token
                airportfee = it.data.airportzo_cancel_fee.toInt()
                bookingstatus = it.data.status
                bookingNumber = it.data.booking_number
                if (it.data.e_ticket.isEmpty()) {
                    bookingDetailFragment.bookingTicketDetails.visibility = View.GONE
                    bookingDetailFragment.bookingTicketImage.visibility = View.GONE
                } else {
                    Glide.with(requireContext()).load(it.data.e_ticket)
                        .error(R.drawable.invoice)
                        .into(bookingDetailFragment.bookingTicketImage)
                }

                bookingDetailFragment.bookingTime.text = it.data.service_dates[0]
                bookingDetailFragment.bookingServiceCostPrice.text =
                    getString(R.string.indianRupee) + " " + it.data.service_amount
                bookingDetailFragment.bookingGstCostPrice.text =
                    getString(R.string.indianRupee) + " " + it.data.service_gst
                bookingDetailFragment.bookingTotalAmountNo.text =
                    getString(R.string.indianRupee) + " " + it.data.total_amount
                bookingDetailFragment.bookinggstNumber.text = it.data.gst_number
                bookingDetailFragment.bookingGstContactPersonName.text = it.data.gst_name

                for (list in 0 until it.data.order_detail.size) {
                    bookingLocationData.add(it.data.order_detail[list])
                }

                var showCancelButton = false
                outer@ for (data in bookingLocationData) {
                    for (order in data.order_detail_array) {
                        if (order.status == "Pending") {
                            showCancelButton = true
                            break@outer
                        }
                    }
                }

                if (showCancelButton) {
                    bookingDetailFragment.cancelButton.visibility = View.VISIBLE
                    bookingDetailFragment.checkStatus.visibility = View.GONE
                } else {
                    bookingDetailFragment.cancelButton.visibility = View.GONE
                    bookingDetailFragment.checkStatus.visibility = View.VISIBLE
                }

                bookingDetailFragment.bookingCompletedRecycler.adapter = bookingLocationAdapter

                for (list in 0 until it.data.order_detail.size) {
                    bookingPackageData.addAll(it.data.order_detail[list].order_detail_array)
                }

                for (i in 0 until it.data.passenger_detail.size) {
                    when (it.data.passenger_detail[i].passenger_type) {
                        "Contact" -> {
                            bookingDetailFragment.bookingContactPassengerName.text =
                                it.data.passenger_detail[0].passenger_array[0].name_view
                            bookingDetailFragment.bookingContactPassengerNo.text =
                                "+${it.data.passenger_detail[0].passenger_array[0].mobile_view}"
                            bookingDetailFragment.bookingContactPassengerEmail.text =
                                it.data.passenger_detail[0].passenger_array[0].email_id
                            bookingDetailFragment.bookingContactPassengerAge.text =
                                it.data.passenger_detail[0].passenger_array[0].age
                        }
                        "Greeter" -> {
                            bookingDetailFragment.bookingServiceContactPersonName.text =
                                it.data.passenger_detail[0].passenger_array[0].name
                            bookingDetailFragment.bookingServiceContactPersonMobileNo.text =
                                it.data.passenger_detail[0].passenger_array[0].mobile_number
                        }
                        "Others" -> {
                            bookingOtherPassengerData.addAll(it.data.passenger_detail[i].passenger_array)
                            bookingDetailFragment.bookingOtherPassengerRecycler.adapter =
                                otherPassengerAdapter

                            if (it.data.passenger_detail[i].passenger_array.isNotEmpty()) {
                                bookingDetailFragment.bookingOtherPassengerDetails.text =
                                    "Other Passenger Details"
                                bookingDetailFragment.bookingOtherPassengerDetails.visibility =
                                    View.VISIBLE
                                bookingDetailFragment.bookingOtherPassengerRecycler.visibility =
                                    View.VISIBLE
                            }
                        }
                    }
                }


                for (list in 0 until it.data.journey_detail.size) {
                    bookingJourneyData.add(it.data.journey_detail[list])
                    bookingDetailFragment.bookingFlightRecycler.adapter = journeyAdapter
                }
            }

            is ApiResult.Error -> {
                MaterialAlertDialogBuilder(requireContext())
                    .setCancelable(false)
                    .setTitle("Alert")
                    .setMessage(it.message)
                    .setPositiveButton("Ok", DialogInterface.OnClickListener { dialog, which ->
                        Navigation.findNavController(requireView()).popBackStack()
                        dialog.dismiss()
                    })
                    .show()
            }
            else -> {}
        }
    }


    private fun checkpermission() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            downLoadPdf()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1
            )
        }
    }

    private fun downLoadPdf() {
        if (cancelledPdf.isEmpty()) {

            pdfurl.let {
                val fileName = File(it).name
                downloadFile(fileName = fileName, url = it)
            }


//            val dialogBuilder = android.app.AlertDialog.Builder(context)
//            dialogBuilder.setMessage("Do you want to download your invoice")
//                .setCancelable(false)
//                .setNegativeButton("No") { dialog, _ ->
//                    dialog.dismiss()
//                }
//                .setPositiveButton("Yes") { dialog, _ ->
//                    dialog.dismiss()
//
//
//                }
//                .show()
        } else {
            showDownloadSpinner()
        }
    }


    private fun onDropDownSelected(string: String) {
        (bookingDetailFragment.downloadSpinner.getChildAt(0) as TextView).setTextColor(Color.TRANSPARENT)
        hideSpinnerDropDown(bookingDetailFragment.downloadSpinner)
        when (string) {
            "Download Booking Invoice" -> {
                pdfurl.let {
                    val fileName = File(it).name
                    downloadFile(fileName = fileName, url = it)
                }
            }

            "Download Credit Invoice" -> {
                for (data in cancelledPdf) {
                    val fileName = File(data).name
                    downloadFile(fileName = fileName, url = data)
                }
            }
        }
    }

    private fun showDownloadSpinner() {

        val spinnerArray: ArrayList<String> = ArrayList()
        spinnerArray.apply {
            add("Download Booking Invoice")
            add("Download Credit Invoice")
        }

        bookingDetailFragment.downloadSpinner.adapter = CustomSpinnerAdapter(
            context = requireContext(),
            spinnerData = spinnerArray,
            itemClicker = ::onDropDownSelected
        )


        bookingDetailFragment.downloadSpinner.performClick()

    }

    private fun hideSpinnerDropDown(spinner: Spinner?) {
        try {
            val method: Method = Spinner::class.java.getDeclaredMethod("onDetachedFromWindow")
            method.isAccessible = true
            method.invoke(spinner)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun downloadFile(fileName: String, url: String) {
        val request = DownloadManager.Request(Uri.parse(url))
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setTitle(fileName)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(false)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
        val downloadManager =
            requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadID = downloadManager.enqueue(request)
        alertToast(requireContext(), "Download Completed.")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    downLoadPdf()
                } else {
                    alertToast(requireActivity(), "Permission Denied")
                }
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        return bookingDetailFragment.root
    }

    @SuppressLint("SuspiciousIndentation")
    private fun showCancelResponseDialog(bookingId: String) {
        val materialAlertDialog = MaterialAlertDialogBuilder(requireContext())
        val customAlertView =
            LayoutInflater.from(requireContext()).inflate(R.layout.cancel_dialog, null, false)
        materialAlertDialog.setView(customAlertView)
        materialAlertDialog.setCancelable(false)
        val okButton = customAlertView.findViewById<MaterialButton>(R.id.okay)
        val message = customAlertView.findViewById<MaterialTextView>(R.id.messageTextView)
        val cancelImg = customAlertView.findViewById<ImageView>(R.id.cancelSuss)

        /** brand color*/
        val colorString = activity?.let { SavedSharedPreference.getCustomColor(it).brand_colour }
        val brandColor = Color.parseColor(colorString)
          okButton.setBackgroundColor(brandColor)
        cancelImg.setColorFilter(Color.parseColor(colorString))




        val messageString = SpannableStringBuilder()
            .append("You have successfully cancelled the booking ID ")
            .bold { append(bookingId) }
            .append(". It will take 2-4 business days for the refundable amount to be deposited in your bank account")
        message.text = messageString

        val alertDialog = materialAlertDialog.show()

        okButton.setOnDebounceListener {
            alertDialog.dismiss()
            bookingDetailFragment.bookingBack.performClick()
        }

    }





    private fun logIn() {
        val jsonObject = JsonObject().apply {
            addProperty("login_device", "Android")
            addProperty("country_code", SavedSharedPreference.getUserData(requireContext()).code)
            addProperty("mobile_number", SavedSharedPreference.getUserData(requireContext()).mobile)
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
                val mobileNumber = "${SavedSharedPreference.getUserData(requireContext()).code} ${
                    SavedSharedPreference.getUserData(requireContext()).mobile
                }"

                val otpMessage = SpannableStringBuilder()
                    .append("OTP send to ")
                    .bold {
                        append(mobileNumber)
                    }
                    .append(".")
                otpView?.findViewById<TextView>(R.id.number)?.text = otpMessage
                otpView?.findViewById<TextView>(R.id.enterOtpText)?.text =
                    "Please enter the OTP to cancel the booking"
                otpView?.findViewById<TextView>(R.id.editNumber)?.visibility = View.GONE
                otpView?.findViewById<TextView>(R.id.resendCode)?.visibility = View.GONE
                startTimer()
                otpView?.show()
            }
            is ApiResult.Error -> {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startTimer() {
        val timer = object : CountDownTimer(30000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                try {
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
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            @SuppressLint("SetTextI18n")
            override fun onFinish() {
                try{
                    otpView?.findViewById<TextView>(R.id.resend)?.text =
                        resources.getString(R.string.didnot_receive)
                    otpView?.findViewById<TextView>(R.id.resendCode)?.visibility = View.VISIBLE
                    otpView?.findViewById<TextView>(R.id.resend)
                        ?.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                    otpView?.findViewById<OtpView>(R.id.otpView)?.text = null
                    otpView?.findViewById<TextView>(R.id.resendCode)?.setOnDebounceListener {
                        otpView?.dismiss()
                        logIn()
                    }
                }
                catch(e: Exception){
                    e.printStackTrace()
                }
            }
        }
        timer.start()
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
//            is ApiResult.Loading -> loader.onChanged(it.loading)
            is ApiResult.Success -> {
                otpView?.dismiss()
                if (isCancelSpecificOrder) cancelSpecificOrder()
                else cancelCompleteBooking()
            }
            is ApiResult.Error -> {
                errors("Error", it.message)
            }
            else -> {}
        }
    }
}






