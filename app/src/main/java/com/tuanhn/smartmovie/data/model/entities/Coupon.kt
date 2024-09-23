package com.tuanhn.smartmovie.data.model.entities

data class Coupon(
    val id: Int = 0,
    val discountValue: Int = 0,
    val startDate: String = "",
    val endDate: String = "",
    val status: String ="",
    var limitedCount: Int = 0
)
