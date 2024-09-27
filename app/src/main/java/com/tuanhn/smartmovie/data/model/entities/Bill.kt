package com.tuanhn.smartmovie.data.model.entities

data class Bill(
    val bill_id: Int = 0,
    val film_id: Int = 0,
    val cinemaName: String = "",
    val seats: String = "",
    val totalMoney: Float = 0.0F,
    val user: String = "",
    val date: String = ""
)
