package com.travel.airportzo.user.model

data class GetBookingTotal(
     val convenience_cost: String,
     val convenience_cost_gst: String,
     val service_cost_excl_gst: String,
     val service_cost_gst: String,
     val total_amount: String
)
