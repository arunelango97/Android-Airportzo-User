package com.travel.airportzo.user.model



data class ServiceTicketData(
    val station_array: ArrayList<Station_array>
) {
    data class Station_array(
        val ttr_token: String,
        val category_name: String,
        val airport_token : String,
        val airport_code: String,
        val airport_name: String,
        val terminal_name: String,
        val city_name: String,
        var journey_date: String,
        val gmt_view:String,
        val service_collection: ArrayList<Service_collection>? = null
    )

    data class Service_collection(
        val title: String,
        val service_type: String,
        val service_group: ArrayList<Service_group>? = null
    )

    data class Service_group(
        val sp_company_token: String,
        val sp_company_name: String,
        val sp_company_logo: String,
        val sp_company_image: String,
        val minimum_price: String,
        val business_names: ArrayList<String>,
        val description : String,
//        var isClicked: Boolean,
        val terms_conditions : String,
         val privacy_policy : String,
        val cancellation_policy : String,
        val reschedule_policy : String,
        val about_description:String,
        val cancellation_policy_detail : ArrayList<Cancellation_policy_detail>,
        val service_array: ArrayList<Service_array>,
//        val service_provider_company_location_token : Int,
         val photos : List<String>,
         val amenities : List<Amenities>,
         val reviews : List<Reviews>
    )

    data class  Cancellation_policy_detail(
        val hours: String,
        val percentage: String
    )

    data class Amenities (
         val name : String,
          val image : String
    )

    data class Reviews (
         val title : String,
         val name : String,
         val name_view : String,
         val image : String,
         val rating : String,
         val review : String,
         val review_date_time:String
    )

    data class Service_array(

        val token: String,
        val service_token: String,
        val sp_company_token: String,
        val unique_business_token: String,
        val sp_company_name: String,
        val sp_company_logo: String,
        val sp_company_image: String,
        val service_type: String,
        val service_name: String,
        var isClicked: Boolean,
        val price_adult: String,
        val additional_price_adult: String,
        val price_children: String,
        val additional_price_children: String,
        val business_names: ArrayList<String>,
        val service_features: ArrayList<String>,
        var totalAmount: Int = 0,
        var service_date:String?,
        var service_time:String?,



//        val service__provider_company_location_token:String,
//        val description : String,
//        val terms_conditions : String,
//        val privacy_policy : String,
//        val cancellation_policy : String,
//        val cancellation_policy_detail :ArrayList<Cancellation_policy_detail>,
    )

}
