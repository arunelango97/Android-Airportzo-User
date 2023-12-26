package com.travel.airportzo.user.model


data class CancelBookingServiceData(
    val distributor_name: String,
    val distributor_email: String,
    val header_logo: String,
    val brand_colour: String,
    val id: String,
    val token: String,
    val for_others: Boolean,
    val booking_number: String,
    val user_token: String,
    val user_name: String,
    val user_mobile: String,
    val user_email: String,
    val journey: String,
    val service_amount: String,
    val service_gst: String,
    val convenience_fee: String,
    val cf_tax: String,
    val total_amount: String,
    val currency: String,
    val payment_view: String,
    val total_service: String,
    val total_adult: String,
    val total_children: String,
    val service_dates: List<String>,
    val type: String,
    val booking_type: String,
    val date_time: String,
    val status: String,
    val e_ticket: String,
    val pancard_number: String,
    val gst_name: String,
    val gst_number: Int,
    val description_one: String,
    val description_two: String,
    val payment_id: String,
    val invoice_pdf: String,
    val order_detail: ArrayList<Order_detail>,
    val passenger_detail: ArrayList<Passenger_detail>,
    val journey_detail: ArrayList<Journey_detail>
) {
    data class Order_detail(

        val airport_token: String,
        val gmt_view: String,
        val airport_name: String,
        val airport_code: String,
        val terminal_token: String,
        val terminal_name: String,
        val station_number: String,
        val order_detail_array: ArrayList<Order_detail_array>,
        val airport_type: String,
        val flight_number: String
    )

    data class Order_detail_array(

        val id: String,
        val token: String,
        val booking_token: String,
        val company_token: String,
        val company_name: String,
        val company_email: String,
        val company_logo: String,
        val company_image: String,
        val service_date_time: String,
        val service_date_time_raw: String,
        val service_token: String,
        val service_name: String,
        val service_type: String,
        val service_location_token: String,
        val journey_date: String,
        val date_time: String,
        val flight_number: String,
        val status: String,
        val adult_service_amount: String,
        val total_adult: String,
        val children_service_amount: String,
        val total_children: String,
        val net_amount: String,
        val rating: String,
        val review: String,
        val description: String,
        val report_reason_token: String,
        val report_reason: String,
        val report_description: String,
        val reported_date_time: String,
        val notes: String,
        val cancellation_policy_detail: ArrayList<ServiceTicketData.Cancellation_policy_detail>
    )

    data class Cancellation_policy_detail(

        val hours: String,
        val percentage: String
    )

    data class Passenger_detail(

        val passenger_type: String,
        val passenger_array: ArrayList<GetOrderDetailData.Passenger_array>
    )

    data class Passenger_array(

        val id: String,
        val token: String,
        val booking_token: String,
        val passenger_type: String,
        val user_passenger_token: String,
        val title: String,
        val name: String,
        val name_view: String,
        val country_code: String,
        val mobile_number: String,
        val mobile_view: String,
        val email_id: String,
        val date_of_birth: String,
        val age: String
    )

    data class Journey_detail(

        val id: String,
        val token: String,
        val booking_token: String,
        val depart_ttr_token: String,
        val depart_airport_code: String,
        val depart_airport: String,
        val depart_terminal: String,
        val arrival_airport_code: String,
        val arrival_airport: String,
        val arrival_terminal: String,
        val arrival_ttr_token: String,
        val depart_date: String,
        val flight_number: String
    )

}