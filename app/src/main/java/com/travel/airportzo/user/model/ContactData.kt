package com.travel.airportzo.user.model

import com.google.gson.annotations.SerializedName

data class ContactData(

	@field:SerializedName("status_code")
	val statusCode: Int,

	@field:SerializedName("data")
	val data: Data,

	@field:SerializedName("title")
	val title: String,

	@field:SerializedName("message")
	val message: String
){
	data class Data(

		@field:SerializedName("subject")
		val subject: String,

		@field:SerializedName("name")
		val name: String,

		@field:SerializedName("message")
		val message: String,

		@field:SerializedName("email")
		val email: String,

		@field:SerializedName("token")
		val token: String
	)

}