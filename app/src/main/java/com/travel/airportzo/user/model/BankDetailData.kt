package com.travel.airportzo.user.model

import com.google.gson.annotations.SerializedName

data class BankDetailData (
    val token : String,
    val account_number : String,
    val ifsc_code : String,
    val branch_name : String,
    val is_primary : Boolean,
    val date_time : String
        )