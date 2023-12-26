package com.travel.airportzo.user.network

sealed class ApiResult<out T : Any> {
    class Success<out T : Any>(val data: T) : ApiResult<T>()

    class Error(val header: String? = "Error", val message: String) : ApiResult<Nothing>()

    class Loading (val loading : Boolean) : ApiResult<Nothing>()

}