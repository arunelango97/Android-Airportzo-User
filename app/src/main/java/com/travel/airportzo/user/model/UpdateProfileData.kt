package com.travel.airportzo.user.model


data class UpdateprofileData(
    val token : String,
    val is_agent : Boolean,
    val is_approved : String,
    val title : String,
    val name : String,
    val image : String,
    val email : String,
    val country_code : String,
    val mobile_number : String,
    val dob : String,
    val pan_card : String,
    val address_proof : String
)
