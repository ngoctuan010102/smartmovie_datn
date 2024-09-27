package com.tuanhn.smartmovie.data.model.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Room(
    var room_id: Int = 0,
    var room_name: String = "",
    var capacity: Int = 0
) : Parcelable
