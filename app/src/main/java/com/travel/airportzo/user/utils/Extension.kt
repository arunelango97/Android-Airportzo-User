package com.travel.airportzo.user.utils

import android.content.Context
import android.util.Log
import android.view.View
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


fun View.setOnDebounceListener(onClick: (View) -> Unit) {
    val debounceOnClickListener = object : DebounceClickListener() {
        override fun onDebounceClick(view: View) {
            onClick(view)
        }
    }
    setOnClickListener(debounceOnClickListener)
}

fun Context.roundOffDecimal(number: Double): Double {
    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.FLOOR
    return df.format(number).toDouble()
}

fun Context.getCurrentDate():String{
    val calendar: Calendar = Calendar.getInstance()
    calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1)
    val simpleDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return simpleDateFormat.format(calendar.time)
}

fun Context.getGMTTime(gmt: String): String{
    val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val calendar = Calendar.getInstance()
    format.timeZone = TimeZone.getTimeZone(gmt.replace(" ",""))
    return format.format(calendar.time).uppercase()
}


fun Context.getGMTDate(gmt: String): String{
    val format = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
    val calendar = Calendar.getInstance()
    format.timeZone = TimeZone.getTimeZone(gmt.replace(" ",""))
    return format.format(calendar.time)

}

fun Context.formatDate(format: String, date: Date) = SimpleDateFormat(format, Locale.getDefault()).format(date)