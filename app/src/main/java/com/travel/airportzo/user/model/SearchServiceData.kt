package com.travel.airportzo.user.model



import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SearchServiceData(
    val departure_ttr_token: String,
    val arrival_ttr_token: String,
    val departure_date: String,
    val flight_number: String,
) : Parcelable