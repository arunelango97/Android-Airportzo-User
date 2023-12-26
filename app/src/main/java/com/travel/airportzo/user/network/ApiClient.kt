package com.travel.airportzo.user.network

import com.google.gson.GsonBuilder
import com.travel.airportzo.user.utils.Utility
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    private var certificatePinner = CertificatePinner.Builder()
        .add("airportzostage.in", "sha256/do/zd6Cj0P55Jn3z7fVofavj9X0aVD1hzEw8G/37e4Y=")
         .build()
    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .certificatePinner(certificatePinner)
            .connectTimeout(3, TimeUnit.MINUTES)
            .readTimeout(3, TimeUnit.MINUTES)
            .writeTimeout(3, TimeUnit.MINUTES)
            .build()
    }

    //        .add("airportzo.net.in", "sha256/Y9mvm0exBk1JoQ57f9Vm28jKo5lFm/woKcVxrYxu80o=")
//            .add("airportzostage.in", "sha256/do/zd6Cj0P55Jn3z7fVofavj9X0aVD1hzEw8G/37e4Y=")

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Utility.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .client(okHttpClient)
            .build()
    }

    val apis: ApiInterface = getRetrofit().create(ApiInterface::class.java)

    fun APIinterface(): ApiInterface {
        return getRetrofit().create<ApiInterface>(
            ApiInterface::class.java)
    }
}