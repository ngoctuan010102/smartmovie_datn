package com.tuanhn.smartmovie.data.model.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "age_ratings")
data class AgeRating(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val film_id: Int,
    val rating: String,
    val age_rating_image: String,
    val age_advisory: String?
)