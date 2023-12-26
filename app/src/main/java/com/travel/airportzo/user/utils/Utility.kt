package com.travel.airportzo.user.utils

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.travel.airportzo.user.model.ServiceTicketData
import java.util.Date

object Utility {
    @SuppressLint("StaticFieldLeak")
    var userId: String = ""

//    var BASE_URL = "https://airportzo.net.in/api/mVYJvjVGQS_0.4/"   //Stage
     var BASE_URL = "https://airportzostage.in/api/wrfTnImFgN_0.5/"   //Stage
//     var BASE_URL = "https://airportzo.net.in/api/wrfTnImFgN_0.5/"   //Dev
   //    var BASE_URL = "https://airportzo.net.in/api/mVYJvjVGQS_0.4/"   //Live
   /** razapr payKey */
   // var razor_testKey = "rzp_test_HIRzt8W1mofBI8" //Stage
  //  var razor_testKey = "rzp_test_w1dbYsdHNBm2p9" //Dev

    var FLIGHTDATE: Date? = null

    var tempDate = ""
    var tempTime = ""

    fun toastMessage(context: Context, message: String) {
        val toast = Toast.makeText(context, message, Toast.LENGTH_LONG)
        toast.show()
        Handler(Looper.getMainLooper()).postDelayed({ toast.cancel() }, 2000)
    }

    fun clearBackStackAndStartActivity(context: Context, activityClass: Class<*>) {
        val intent = Intent(context, activityClass)
        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    fun Context.Alertdialog(message: String){
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage(message)
            .setCancelable(false)

            .setNegativeButton("", DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
            })
            .setPositiveButton("Ok", DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
            })
            .show()
    }

    lateinit var finalInput : JsonObject
     var stationarray = JsonArray()
     var bottom:String=""
    var business=ArrayList<String>()
    var uploadimage = ""
    var uploadagentimage = ""
    var profileimage = ""
    var chatimage = ""

    var departureDate = ""
    var dateTime = ""
    var dataList: ArrayList<ServiceTicketData.Station_array> = ArrayList()
    var datePosition = 0
    var totalamountCurrency =""
    var TAG = "Airportzo"
}