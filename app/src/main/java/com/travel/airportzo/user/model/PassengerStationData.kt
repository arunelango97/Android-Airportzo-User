package com.travel.airportzo.user.model


data class PassengerStationData(
    val ttr_token: String,
    val stationData : ArrayList<PassengerStationData>
) {
    data class PassengerStationData(
        val service_token: String,
        val unique_business_token: String,
        var adult_count: Int,
        var children_count: Int,
        var service_date: String,
        var service_time: String,
        var sp_company_token : String?,
        var notes:String,

    )
}
