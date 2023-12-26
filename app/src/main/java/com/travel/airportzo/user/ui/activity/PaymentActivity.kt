package com.travel.airportzo.user.ui.activity

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import com.travel.airportzo.user.databinding.PaymentActivityBinding
import com.travel.airportzo.user.model.OrderCompletedData
import com.travel.airportzo.user.network.ApiClient
import com.travel.airportzo.user.savedpreference.SavedSharedPreference
import com.travel.airportzo.user.ui.base.BaseActivity
import com.travel.airportzo.user.ui.fragments.*
import com.travel.airportzo.user.utils.Utility
import com.travel.airportzo.user.utils.Utility.TAG
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PaymentActivity : BaseActivity(), PaymentResultWithDataListener {

    private val activityPaymentBinding by lazy { PaymentActivityBinding.inflate(layoutInflater) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityPaymentBinding.root)


        Checkout.preload(this)
        makePayment()
    }

    private fun makePayment() {

        val checkout = Checkout()
        checkout.setKeyID(SummaryFragment.rzpAuthKey)


        val activity: Activity = this

        try {
            val options = JSONObject()
            options.put("name", "AirportZo")
            options.put("description", "")
            options.put("currency", "INR")
            options.put("amount", 500)
            options.put("order_id", SummaryFragment.order_id)

            val prefill = JSONObject()
            prefill.put("email", "airportZo@gmail.com")
            prefill.put("contact", "9488260528")
            options.put("prefill", prefill)

            checkout.open(activity, options)
            Log.d("razorpay_response", options.toString())
        } catch (e: Exception) {
            Toast.makeText(this, "Error in payment: " + e.message, Toast.LENGTH_LONG).show()

            e.printStackTrace()
        }
    }


    override fun onPaymentSuccess(p0: String?, paymentData: PaymentData?) {
        Log.d("razor_order",paymentData.toString())
        Log.d("razor_order",p0.toString())
        Checkout.clearUserData(this)
        Toast.makeText(this, "success", Toast.LENGTH_SHORT).show()
        callApi(paymentData?.paymentId)


        PackageServiceFragment.totalCount.clear()
        PackageServiceFragment.passengerStationData.clear()
        CheckoutFragment.array = JsonObject()
        CheckoutFragment.checkoutDataOther.clear()
        CheckoutFragment.checkoutDataService.clear()
        launchActivity(javaClass = MainActivity::class.java, isClearPreviousTask = true)
        finish()
    }

    private fun callApi(paymentId: String?) {
        Log.d("razor_order","booked")
        val userToken = SavedSharedPreference.getUserData(this).token
        val jsonObject = JsonObject().apply {
            addProperty("user_token", userToken)
            add("contact_passenger", CheckoutFragment.array)
            add("other_passenger", Gson().toJsonTree(CheckoutFragment.checkoutDataOther))
            add("greet_passenger", Gson().toJsonTree(CheckoutFragment.checkoutDataService))
            addProperty("panNumber", CheckoutFragment.panNumber)
            addProperty("e_ticket", CheckoutFragment.uploadCloneTicket)
            addProperty("gst_name", CheckoutFragment.gstName)
            addProperty("gst_number", CheckoutFragment.gstNumber)
            addProperty("greet", CheckoutFragment.placard)
            add("station_array", Utility.stationarray)
            add("journey_array", Gson().toJsonTree(HomeFragment.transitsDirectFlight))
            addProperty("razorpay_payment_id", paymentId)
            addProperty("razorpay_order_id", SummaryFragment.order_id)
            addProperty(
                "razorpay_signature",
                "1e7ae3569d3a565e63d7840e1e028d6b4f04e1a4c242a0a09ba6108f5dd0d18c"
            )
            addProperty("currency", SummaryFragment.selectItem)
            addProperty("device_type", "Android")
            addProperty("coupon_code", SummaryFragment.couponCode)
            addProperty("coupon_status", SummaryFragment.couponStatus)
            addProperty("coupon_type", SummaryFragment.couponType)
        }

        Log.d("razor_order", "booked: ${jsonObject.toString()}")
        Log.d(TAG, "callApi:${Utility.stationarray} ")

        ApiClient.APIinterface().booked(jsonObject)

            .enqueue(object : Callback<OrderCompletedData> {
                override fun onFailure(call: Call<OrderCompletedData>, t: Throwable) {
                    Log.d("razor_order",t.toString())
                }

                override fun onResponse(
                    call: Call<OrderCompletedData>,
                    response: Response<OrderCompletedData>
                ) {
                    if (response.isSuccessful) {
                        response.body()!!.data.token
                        response.body()!!.data.journey
                        PackageServiceFragment.totalCount.clear()
                    } else {
                        Toast.makeText(this@PaymentActivity, "fail", Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }

    override fun onPaymentError(p0: Int, response: String?, paymentData: PaymentData?) {
        Log.d("razor_order",response.toString())
        Toast.makeText(this, "fail", Toast.LENGTH_SHORT).show()
        onBackPressed()
    }
}