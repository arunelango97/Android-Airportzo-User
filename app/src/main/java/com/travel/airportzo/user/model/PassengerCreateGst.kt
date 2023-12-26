package com.travel.airportzo.user.model


data class PassengerCreateGst(
    val token : String,
    val name : String,
    val gstin : String,
    val selected:Boolean,
)
