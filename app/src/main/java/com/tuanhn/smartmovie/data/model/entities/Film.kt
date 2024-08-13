package com.tuanhn.smartmovie.data.model.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "films")
@Parcelize
data class Film(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val film_id: Int,
    val imdb_id: Int,
    val imdb_title_id: String,
    val film_name: String,
    val other_titles: String?,
    val releaseDate: String,
    val film_trailer: String?,
    val synopsis_long: String,
    val poster: String?,
    val still: String?
): Parcelable