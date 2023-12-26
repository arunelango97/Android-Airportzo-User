package com.travel.airportzo.user.model


data class ServiceDetailData (
         val token : String,
         val name : String,
         val image : String,
         val price : Int,
         val description : String,
         val images : ArrayList<String>,
         val avail_services : ArrayList<Avail_services>,
         val how_it_works: ArrayList<Steps>,
         val services : ArrayList<String>,
         val partners : ArrayList<Partners>
        ){

    data class Avail_services (
        val name : String,
        val image : String
    )

    data class Partners (
        val name : String,
        val image : String
    )

    data class Steps(
            val content: String? = "",
            val image: String? = "",
            val steps: String? = ""
        )
}
