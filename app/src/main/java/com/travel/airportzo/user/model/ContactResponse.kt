package com.travel.airportzo.user.model

import com.google.gson.annotations.SerializedName

data class ContactResponse(

	@field:SerializedName("status_code")
	val statusCode: Int,

	@field:SerializedName("data")
	val data: List<DataItem>,

	@field:SerializedName("title")
	val title: String,

	@field:SerializedName("message")
	val message: String
){
	data class DataItem(

		@field:SerializedName("mail_us")
		val mailUs: String,

		@field:SerializedName("corporate_address")
		val corporateAddress: String,

		@field:SerializedName("id")
		val id: String,

		@field:SerializedName("mobile_number")
		val mobileNumber: String,

		@field:SerializedName("token")
		val token: String,

		@field:SerializedName("whatsapp_number")
		val whatsappNumber: String
	)

}