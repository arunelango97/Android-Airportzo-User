package com.travel.airportzo.user.model

data class CreateOrderData(
    val status_code : String,
    val message : String?,
    val order_id : String?,
    val payment_amount : String,
    val rzp_authkey : String?,
    val receipt : String?,
    val user_name : String?,
    val user_email : String?,
    val user_mobile : String?,
)
