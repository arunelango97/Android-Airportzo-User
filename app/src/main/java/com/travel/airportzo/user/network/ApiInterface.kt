package com.travel.airportzo.user.network

import UserDynamicDataColor
import com.google.gson.JsonObject
import com.travel.airportzo.user.model.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiInterface {

    @POST("terminals/read.php")
    suspend fun terminalList(): Response<TerminalData>


    @POST("users/user-authentication.php")
    suspend fun loginCall(@Body jsonObject: JsonObject): Response<Any>

    @POST("users/user-verification.php")
    suspend fun otpverifyCall(@Body jsonObject: JsonObject): Response<OtpData>


    @POST("services/read-for-journeys.php")
    suspend fun searchList(@Body jsonObject: JsonObject): Response<ServiceTicketData>

//    @POST("razor/create-order.php")
//    suspend fun createOrder(@Body jsonObject: JsonObject): CreateOrderData

    @POST("razor/create-order.php")
    fun createOrder(@Body params: JsonObject): Call<CreateOrderData>

    @POST("users-booking/create.php")
    fun booked(@Body params: JsonObject): Call<OrderCompletedData>

    @POST("users-booking/read-history.php")
    suspend fun bookingHistory(@Body jsonObject: JsonObject): Response<List<MyBookingData>>

    @GET("users-booking/available-coupon.php")
    suspend fun availableCoupon(): Response<List<CouponData>>

    @POST("users-booking/get-order-detail.php")
    suspend fun getOrder(@Body jsonObject: JsonObject): Response<GetOrderDetailData>

    @POST("users-booking/calculate-price.php")
    fun getTotal(@Body params: JsonObject): Call<GetBookingTotal>

    @POST("services/read.php")
    suspend fun services(@Body params: JsonObject): ReadServiceJson

    @GET("contact-info/read.php")
    suspend fun readContact(): ContactResponse

    @POST("users/contact-info.php")
    suspend fun contactDetail(@Body params: JsonObject): ContactData

    @POST("users-booking/coupon.php")
    suspend fun applyCoupon(@Body params: JsonObject): CouponApplied

    @POST("services/read-one.php")
    suspend fun servicedetail(@Body params: JsonObject): Response<ServiceDetailData>

    @POST("users-passenger/create.php")
    suspend fun createpassenger(@Body params: JsonObject): Response<PassengerCreateData>

    @POST("users-passenger/read.php")
    suspend fun readdpassenger(@Body params: JsonObject): Response<List<PassengerCreateData>>

    @POST("users-passenger/update.php")
    suspend fun updatepassenger(@Body params: JsonObject): Response<PassengerCreateData>

    @POST("users-passenger/delete.php")
    suspend fun deletepassenger(@Body params: JsonObject): Response<Any>

    @POST("users-gst/create.php")
    suspend fun creategst(@Body params: JsonObject): Response<PassengerCreateGst>

    @POST("users-gst/read.php")
    suspend fun readdgst(@Body params: JsonObject): Response<List<PassengerCreateGst>>

    @POST("users-gst/update.php")
    suspend fun updategst(@Body params: JsonObject): Response<PassengerCreateGst>

    @POST("users-gst/delete.php")
    suspend fun deletegst(@Body params: JsonObject): Response<Any>

    @POST("users/update.php")
    suspend fun update(@Body params: JsonObject): Response<UpdateprofileData>

    @POST("users/read-detail.php")
    suspend fun updateprofile(@Body params: JsonObject): Response<UpdateprofileData>

    @POST("report-reason/read.php")
    suspend fun reported(): Response<List<ReportedData>>

    @POST("report-reason/update-report.php")
    suspend fun updatereport(@Body params: JsonObject): Response<UpdateReportClass>

    @POST("users-booking/update-review.php")
    suspend fun updatereview(@Body params: JsonObject): Response<DefaultResponse>

    @POST("users-booking/update-comment.php")
    suspend fun updateComment(@Body params: JsonObject): Response<DefaultResponse>

    @POST("become-agent/read-countries.php")
    suspend fun countries(): Response<List<CountryData>>

    @POST("become-agent/read-regions-for-country.php")
    suspend fun region(@Body params: JsonObject): Response<List<RegionData>>

    @POST("become-agent/read-cities-for-region.php")
    suspend fun cities(@Body params: JsonObject): Response<List<CityData>>

    @POST("become-agent/apply.php")
    suspend fun becomeagentt(@Body params: JsonObject): Response<BecomeAgentData>

    @POST("users-bank/create.php")
    suspend fun accountcreate(@Body params: JsonObject): Response<BankDetailData>

    @POST("users-bank/read.php")
    suspend fun accountread(@Body params: JsonObject): Response<List<BankDetailData>>

    @POST("users-bank/update.php")
    suspend fun accountupdate(@Body params: JsonObject): Response<BankDetailData>

    @POST("users-bank/delete.php")
    suspend fun accountdelete(@Body params: JsonObject): Response<Any>

    @POST("users-booking/read-monthly-history.php")
    suspend fun agentdashboard(@Body params: JsonObject): Response<List<AgentDashboardData>>

    @POST("users-booking/cancel-booking.php")
    suspend fun cancelBooking(@Body params: JsonObject): Response<CancelBookingServiceData>

    @POST("users-booking/cancel-order.php")
    suspend fun cancelBookingOrder(@Body params: JsonObject): Response<CancelBookingServiceData>

    @POST("currency.php")
    fun currency(@Body params: JsonObject): Call<CurrencyData>

    @POST("users/user-close-account.php")
    fun closeAccount(@Body params: JsonObject): Call<CloseAccount>

    @GET("service-distributor/get-detail.php")
    fun getDetails():Call<UserDynamicDataColor>

    @GET("faq/read.php")
    suspend fun faqQuestion(): Response<List<FaqData>>

    @POST("users/manage-accounts.php")
    suspend fun manageAccount(@Body params: JsonObject): Response<List<ManageAccountData>>

    @POST("users/user-logout-device.php")
    suspend fun accountLogout(@Body params: JsonObject): Response<Any>

}