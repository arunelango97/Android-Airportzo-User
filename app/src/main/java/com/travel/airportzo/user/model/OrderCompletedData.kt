package com.travel.airportzo.user.model

class OrderCompletedData (

     val status_code : String,
     val message : String,
     val data : Data
){
    data class Data (
         val journey:String,
         val token : String
    )
}