package com.tuanhn.smartmovie.data.model.entities

import androidx.room.Entity

@Entity(tableName = "favorite")
data class Favorite(
    val id_favorite: String = "",
    val user_Name: String = "",
    val film_id: Int = 0
)
