package com.travel.airportzo.user.model

data class OtpData(
    val token : String,
    val name : String,
    val email : String,
    val country_code : String,
    val mobile_number : String
)
