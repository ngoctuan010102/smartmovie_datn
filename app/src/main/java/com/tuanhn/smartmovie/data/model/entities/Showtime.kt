package com.tuanhn.smartmovie.data.model.entities

data class Showtime(
    var showtime_id: Int = 0,
    var film_id: Int = 0,
    var room_id: Int = 0,
    var showtime_date: String = "",
    var start_time: String = "",
    var end_time: String = ""
)
