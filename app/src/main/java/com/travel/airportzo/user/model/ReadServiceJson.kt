package com.travel.airportzo.user.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ReadServiceJson(

	@field:SerializedName("booking_data")
	val bookingData: List<BookingDataItem>?,

	@field:SerializedName("status_code")
	val statusCode: Int,

	@field:SerializedName("data")
	val data: List<DataItem>,

	@field:SerializedName("avail_service")
	val availService: List<AvailServiceItem>,

	@field:SerializedName("our_partners")
	val ourPartners: List<OurPartnersItem>,

	@field:SerializedName("user_data")
	val userData: UserData?,

	@field:SerializedName("message")
	val message: String
){

	data class DataItem(

		@field:SerializedName("image")
		val image: String,

		@field:SerializedName("price")
		val price: String,

		@field:SerializedName("name")
		val name: String,

		@field:SerializedName("description")
		val description: String,

		@field:SerializedName("token")
		val token: String
	)

	data class OurPartnersItem(

		@field:SerializedName("image")
		val image: String,

		@field:SerializedName("date_time")
		val dateTime: String,

		@field:SerializedName("name")
		val name: String,

		@field:SerializedName("id")
		val id: String,

		@field:SerializedName("token")
		val token: String,

		@field:SerializedName("status")
		val status: String
	)

	data class UserData(

		@field:SerializedName("image")
		val image: String,

		@field:SerializedName("title")
		val title: String,

		@field:SerializedName("pan_card")
		val panCard: String,

		@field:SerializedName("token")
		val token: String,

		@field:SerializedName("country_code")
		val countryCode: String,

		@field:SerializedName("dob")
		val dob: String,

		@field:SerializedName("name")
		val name: String,

		@field:SerializedName("is_approved")
		val isApproved: String,

		@field:SerializedName("is_agent")
		val isAgent: Boolean,

		@field:SerializedName("mobile_number")
		val mobileNumber: String,

		@field:SerializedName("address_proof")
		val addressProof: String,

		@field:SerializedName("email")
		val email: String,

		@field:SerializedName("status")
		val status: String
	)

	data class AvailServiceItem(

		@field:SerializedName("image")
		val image: String,

		@field:SerializedName("date_time")
		val dateTime: String,

		@field:SerializedName("name")
		val name: String,

		@field:SerializedName("id")
		val id: String,

		@field:SerializedName("token")
		val token: String,

		@field:SerializedName("status")
		val status: String
	)

	data class BookingDataItem(

		@field:SerializedName("convenience_fee")
		val convenienceFee: String,

		@field:SerializedName("coupon_token")
		val couponToken: String,

		@field:SerializedName("description_two")
		val descriptionTwo: String,

		@field:SerializedName("invoice_pdf")
		val invoicePdf: String,

		@field:SerializedName("gst_name")
		val gstName: String,

		@field:SerializedName("user_name")
		val userName: String,

		@field:SerializedName("currency_value")
		val discountAmount: String,

		@field:SerializedName("e_ticket")
		val eTicket: String,

		@field:SerializedName("header_logo")
		val headerLogo: String,

		@field:SerializedName("type")
		val type: String,

		@field:SerializedName("total_discount_amt")
		val totalDiscountAmt: String,

		@field:SerializedName("service_dates")
		val serviceDates: List<String>,

		@field:SerializedName("booking_number")
		val bookingNumber: String,

		@field:SerializedName("service_amount")
		val serviceAmount: String,

		@field:SerializedName("date_time")
		val dateTime: String,

		@field:SerializedName("user_mobile")
		val userMobile: String,

		@field:SerializedName("payment_id")
		val paymentId: String,

		@field:SerializedName("currency")
		val currency: String,

		@field:SerializedName("id")
		val id: String,

		@field:SerializedName("user_token")
		val userToken: String,

		@field:SerializedName("total_children")
		val totalChildren: String,

		@field:SerializedName("payment_view")
		val paymentView: String,

		@field:SerializedName("distributor_email")
		val distributorEmail: String,

		@field:SerializedName("user_email")
		val userEmail: String,

		@field:SerializedName("journey")
		val journey: String,

		@field:SerializedName("pancard_number")
		val pancardNumber: String,

		@field:SerializedName("user_image")
		val userImage: String,

		@field:SerializedName("service_gst")
		val serviceGst: String,

		@field:SerializedName("description_one")
		val descriptionOne: String,

		@field:SerializedName("discount_type")
		val discountType: String,

		@field:SerializedName("token")
		val token: String,

		@field:SerializedName("total_adult")
		val totalAdult: String,

		@field:SerializedName("cf_tax")
		val cfTax: String,

		@field:SerializedName("total_amount")
		val totalAmount: String,

		@field:SerializedName("total_service")
		val totalService: String,

		@field:SerializedName("distributor_name")
		val distributorName: String,

		@field:SerializedName("booking_type")
		val bookingType: String,

		@field:SerializedName("for_others")
		val forOthers: Boolean,

		@field:SerializedName("gst_number")
		val gstNumber: String,

		@field:SerializedName("brand_colour")
		val brandColour: String,

		@field:SerializedName("airportzo_cancel_fee")
		val airportzoCancelFee: String,

		@field:SerializedName("coupon_type")
		val couponType: String,

		@field:SerializedName("status")
		val status: String,

		@field:SerializedName("service_distributor_token")
		val serviceDistributorToken: String
	)
}