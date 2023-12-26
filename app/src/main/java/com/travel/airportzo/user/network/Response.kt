package com.travel.airportzo.user.network

class Response<out T : Any?> (
    val status_code: Int?= 0,
    val title: String?= null,
    val message: String?= null,
    val data: T
)