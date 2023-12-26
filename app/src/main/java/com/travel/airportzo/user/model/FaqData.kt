package com.travel.airportzo.user.model

import com.google.gson.annotations.SerializedName

//data class FaqData(
//    @SerializedName("data")
//    val data: ArrayList<Data> = arrayListOf() ,
//    @SerializedName("message")
//    val message: String = "",
//    @SerializedName("status_code")
//    val statusCode: Int = 0,
//    @SerializedName("title")
//    val title: String = ""
//) {
    data class FaqData(
        val answer: String ,
        val question: String ,
        val token: String
    )
//}
