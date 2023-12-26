package com.travel.airportzo.user.viewmodel

import com.google.gson.JsonObject
import com.travel.airportzo.user.network.ApiInterface

class MainRepository(private val apis: ApiInterface) {

    suspend fun loginCall(jsonObject: JsonObject) = apis.loginCall(jsonObject)
    suspend fun otpverifyCall(jsonObject: JsonObject) = apis.otpverifyCall(jsonObject)
    suspend fun terminalList() = apis.terminalList()
    suspend fun searchList(jsonObject: JsonObject) = apis.searchList(jsonObject)
    suspend fun createOrder(jsonObject: JsonObject) = apis.createOrder(jsonObject)
    suspend fun bookingHistory(jsonObject: JsonObject) = apis.bookingHistory(jsonObject)
    suspend fun availableCoupon() = apis.availableCoupon()
    suspend fun getOrder(jsonObject: JsonObject) = apis.getOrder(jsonObject)
    suspend fun services(jsonObject: JsonObject)=apis.services(jsonObject)
    suspend fun contactDetail(jsonObject: JsonObject)=apis.contactDetail(jsonObject)
    suspend fun readContact()=apis.readContact()
    suspend fun applyCoupon(jsonObject: JsonObject)=apis.applyCoupon(jsonObject)
    suspend fun servicedetail(jsonObject: JsonObject)=apis.servicedetail(jsonObject)
    suspend fun createpassenger(jsonObject: JsonObject)=apis.createpassenger(jsonObject)
    suspend fun readdpassenger(jsonObject: JsonObject)=apis.readdpassenger(jsonObject)
    suspend fun updatepassenger(jsonObject: JsonObject)=apis.updatepassenger(jsonObject)
    suspend fun deletepassenger(jsonObject: JsonObject)=apis.deletepassenger(jsonObject)
    suspend fun creategst(jsonObject: JsonObject)=apis.creategst(jsonObject)
    suspend fun readdgst(jsonObject: JsonObject)=apis.readdgst(jsonObject)
    suspend fun updategst(jsonObject: JsonObject)=apis.updategst(jsonObject)
    suspend fun deletegst(jsonObject: JsonObject)=apis.deletegst(jsonObject)
    suspend fun update(jsonObject: JsonObject)=apis.update(jsonObject)
    suspend fun updateprofile(jsonObject: JsonObject)=apis.updateprofile(jsonObject)
    suspend fun reported()=apis.reported()
    suspend fun updatereport(jsonObject: JsonObject)=apis.updatereport(jsonObject)
    suspend fun updatereview(jsonObject: JsonObject)=apis.updatereview(jsonObject)
    suspend fun updateComment(jsonObject: JsonObject)=apis.updateComment(jsonObject)
    suspend fun countries()=apis.countries()
    suspend fun region(jsonObject: JsonObject)=apis.region(jsonObject)
    suspend fun cities(jsonObject: JsonObject)=apis.cities(jsonObject)
    suspend fun becomeagentt(jsonObject: JsonObject)=apis.becomeagentt(jsonObject)
    suspend fun accountcreate(jsonObject: JsonObject)=apis.accountcreate(jsonObject)
    suspend fun accountread(jsonObject: JsonObject)=apis.accountread(jsonObject)
    suspend fun accountupdate(jsonObject: JsonObject)=apis.accountupdate(jsonObject)
    suspend fun accountdelete(jsonObject: JsonObject)=apis.accountdelete(jsonObject)
    suspend fun agentdashboard(jsonObject: JsonObject)=apis.agentdashboard(jsonObject)
    suspend fun cancelBookingOrder(jsonObject: JsonObject)=apis.cancelBookingOrder(jsonObject)
    suspend fun cancelBooking(jsonObject: JsonObject)=apis.cancelBooking(jsonObject)
    suspend fun faqQuestion()=apis.faqQuestion()
    suspend fun manageAccount(jsonObject: JsonObject)=apis.manageAccount(jsonObject)
    suspend fun accountLogout(jsonObject: JsonObject)=apis.accountLogout(jsonObject)

//    suspend fun getDetails() = apis.getDetails()



//    suspend fun getTotal(jsonObject: JsonObject) = apis.getTotal(jsonObject)

}