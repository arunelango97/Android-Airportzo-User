package com.travel.airportzo.user.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiIFSC {
    var base_url = "https://ifsc.razorpay.com/"

    var gson: Gson = GsonBuilder()
        .setLenient()
        .create()

//    private var certificatePinner = CertificatePinner.Builder().add("propcierge.in", "sha256/i9AWChB7o5MaJmRZRy1saTK8+moAgd+gorHRnVXr0cU=").add("propcierge.in", "sha256/uRO+QrhGp6fAJJge1vNIzJBhcOQ/jv6DU0qkzDllX9k=").build()

//
//    private var okHttpClient = OkHttpClient.Builder()
//        .certificatePinner(ApiIFSC.certificatePinner)
//        .readTimeout(3, TimeUnit.MINUTES)
//        .writeTimeout(3, TimeUnit.MINUTES)
//        .connectTimeout(3, TimeUnit.MINUTES)
//        .build()

    private val client: Retrofit
        get() = Retrofit.Builder()
            .baseUrl(base_url)
            .addConverterFactory(GsonConverterFactory.create(gson))
//            .client(ApiIFSC.okHttpClient)
            .build()

    fun ApiInterfaceifsc(): ApiInterfaceIFSC {
        return client.create<ApiInterfaceIFSC>(
            ApiInterfaceIFSC::class.java)
    }
}