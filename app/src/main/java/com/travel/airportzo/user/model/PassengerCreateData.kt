package com.travel.airportzo.user.model


data class PassengerCreateData(
    val token : String,
    val title : String,
    val name : String,
    val name_view : String,
    val country_code : String,
    val mobile_number : String,
    val email_id : String,
    val date_of_birth : String,
    val age : String,
    var selected: Boolean = false
)
