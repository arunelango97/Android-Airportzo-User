package com.travel.airportzo.user.model

import com.google.gson.annotations.SerializedName

data class CouponApplied(

	@field:SerializedName("status_code")
	val statusCode: Int,

	@field:SerializedName("data")
	val data: Data,

	@field:SerializedName("cartData")
	val cartData: List<CartDataItem>,

	@field:SerializedName("categoryData")
	val categoryData: List<CategoryData>,

	@field:SerializedName("message")
	val message: String
){

	data class CartDataItem(

		@field:SerializedName("coupon_token")
		val couponToken: String,

		@field:SerializedName("coupon_condition")
		val couponCondition: String,

		@field:SerializedName("users_per_coupon")
		val usersPerCoupon: String,

		@field:SerializedName("cart_dis_amt")
		val cartDisAmt: String,

		@field:SerializedName("discount_amount")
		val discountAmount: String,

		@field:SerializedName("gst_type")
		val gstType: String,

		@field:SerializedName("currency_value")
		val currencyValue: String,

		@field:SerializedName("dis_amt")
		val dis_amt: String,

		@field:SerializedName("users_per_customer")
		val usersPerCustomer: String,

		@field:SerializedName("coupon_type")
		val couponType: String
	)

	data class CategoryData(

		@field:SerializedName("business_type_token")
		val businessTypeToken: String,

		@field:SerializedName("business_name")
		val businessName: String,

		@field:SerializedName("coupon_type")
		val couponType: String,

		@field:SerializedName("discount_amount")
		val discountAmount: String,

		@field:SerializedName("currency_value")
		val currencyValue: String,

		@field:SerializedName("dis_amt")
		val dis_amt: String,

		@field:SerializedName("gst_type")
		val gstType: String
	)

	data class Data(

		@field:SerializedName("code")
		val code: String,

		@field:SerializedName("type")
		val type: String,

		@field:SerializedName("status")
		val status: String
	)
}
