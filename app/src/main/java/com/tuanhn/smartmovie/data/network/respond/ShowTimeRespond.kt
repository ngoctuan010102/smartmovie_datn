package com.tuanhn.smartmovie.data.network.respond

data class ShowTimeRespond(
    val films: FilmRespond,
    val cinemas: List<Cinema>
)
data class Cinema(
    val cinema_id: Int,
    val cinema_name: String,
    val distance: Double,
    val logo_url: String,
    val showings: Showings
)

data class Showings(
    val Standard: StandardShowing
)

data class StandardShowing(
    val film_id: Int,
    val film_name: String,
    val times: List<ShowTime>
)

data class ShowTime(
    val start_time: String,
    val end_time: String
)