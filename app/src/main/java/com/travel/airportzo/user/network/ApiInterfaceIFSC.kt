package com.travel.airportzo.user.network

import com.travel.airportzo.user.model.IFSCData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiInterfaceIFSC {

    @GET("{ifsc}")
    fun product(@Path("ifsc")ifsc : String) : Call<IFSCData>
}