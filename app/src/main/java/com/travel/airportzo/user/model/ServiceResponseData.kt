package com.travel.airportzo.user.model

import com.google.gson.annotations.SerializedName

data class ServiceResponseData(
	val status_code: String ,
	val data: List<DataItem> ,
	val avail_service: List<AvailServiceItem> ,
	val our_partners: List<OurPartnersItem> ,
	val title: String ,
	val message: String
){

	data class DataItem(
		val image: String ,
		val price: String ,
		val name: String ,
		val description: String ,
		val token: String 
	)

	data class AvailServiceItem(
		val image: String ,
		val date_time: String ,
		val name: String ,
		val id: String ,
		val token: String ,
		val status: String
	)

	data class OurPartnersItem(
		val image: String ,
		val date_time: String ,
		val name: String ,
		val id: String ,
		val token: String ,
		val status: String
	)
}

