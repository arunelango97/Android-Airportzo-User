package com.travel.airportzo.user.model

import com.google.firebase.Timestamp

data class ChatData(
   val date_time: Timestamp?,
   val message:String?,
   val message_type:String?,
   val sender_id:String?,
   val myMessage: String?,
   val tpmysg:Int?
)
