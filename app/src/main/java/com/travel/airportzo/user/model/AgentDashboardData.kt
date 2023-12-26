package com.travel.airportzo.user.model


data class AgentDashboardData (
    val distributor_name : String,
    val distributor_email : String,
    val header_logo : String,
    val brand_colour : String,
    val id : String,
    val token : String,
    val for_others : Boolean,
    val booking_number : String,
    val user_token : String,
    val user_name : String,
    val user_mobile : String,
    val user_email : String,
    val journey : String,
    val service_amount : String,
    val service_gst : String,
    val convenience_fee : String,
    val cf_tax : String,
    val total_amount : String,
    val currency : String,
    val payment_view : String,
    val total_service : String,
    val total_adult : String,
    val total_children : String,
    val service_dates : List<String>,
    val type : String,
    val booking_type : String,
    val date_time : String,
    val status : String,
    val e_ticket : String,
    val pancard_number : String,
    val gst_name : String,
    val gst_number : String,
    val description_one : String,
    val description_two : String,
    val payment_id : String,
    val invoice_pdf : String
        )