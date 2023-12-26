package com.travel.airportzo.user.model

data class TerminalData(

    val common: ArrayList<Common>,
    val arrival: ArrayList<Arrival>,
    val departure: ArrayList<Departure>,
    val transit: ArrayList<Transit>
) {
    data class Common(

        val ttr_token: String,
        val airport_token: String,
        val airport_name: String,
        val airport_code: String,
        val terminal_token: String,
        val terminal_name: String,
        val terminal_string: String,
        val terminal_string1: String,
        val terminal_string2: String
    )

    data class Arrival(

        val ttr_token: String,
        val category_token: String,
        val category_name: String,
        val airport_token: String,
        val airport_name: String,
        val airport_code: String,
        val terminal_token: String,
        val terminal_name: String,
        val type_tokens: String,
        val type_names: String,
        val terminal_string: String
    )

    data class Departure(

        val ttr_token: String,
        val category_token: String,
        val category_name: String,
        val airport_token: String,
        val airport_name: String,
        val airport_code: String,
        val terminal_token: String,
        val terminal_name: String,
        val type_tokens: String,
        val type_names: String,
        val terminal_string: String
    )

    data class Transit(

        val ttr_token: String,
        val category_token: String,
        val category_name: String,
        val airport_token: String,
        val airport_name: String,
        val airport_code: String,
        val terminal_token: String,
        val terminal_name: String,
        val type_tokens: String,
        val type_names: String,
        val terminal_string: String
    )
}
