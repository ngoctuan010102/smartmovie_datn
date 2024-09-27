package com.tuanhn.smartmovie.data.model.entities

data class Seat(
    var seat_id: Int = 0,
    var room_id: Int = 0,
    var seat_number: String ="",
    var price: Float = 0.0F
)
