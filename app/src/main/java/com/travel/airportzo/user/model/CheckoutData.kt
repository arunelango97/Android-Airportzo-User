package com.travel.airportzo.user.model

data class CheckoutData(
    val token:String?,
    val title:String?,
    val name:String?,
    val name_view:String,
    val country_code:String,
    val mobile_number:String,
    val email_id:String,
    val date_of_birth:String,
    val other:ArrayList<OtherPassenger>,
    val service:ArrayList<Service>,
    val gst:ArrayList<Gst>
){
    data class OtherPassenger(
        val token:String?,
        val title:String?,
        val name:String?,
        val name_view:String,
        val country_code:String,
        val mobile_number:String,
        val email_id:String,
        val date_of_birth:String,
    )

    data class Service(
        val token:String?,
        val title:String?,
        val name:String?,
        val name_view:String,
        val country_code:String,
        val mobile_number:String,
        val email_id:String,
        val date_of_birth:String,
    )

    data class Gst(
        val gstName:String,
        val gstno:String
    )
}
