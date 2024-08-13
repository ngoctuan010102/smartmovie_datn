package com.tuanhn.smartmovie.data.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tuanhn.smartmovie.data.model.dao.AgeRatingDao
import com.tuanhn.smartmovie.data.model.dao.FilmDao
import com.tuanhn.smartmovie.data.model.entities.AgeRating
import com.tuanhn.smartmovie.data.model.entities.Film

@Database(
    entities = [Film::class, AgeRating::class],
    version = 1,
    exportSchema = false
)
abstract class AppDB : RoomDatabase() {

    abstract fun getFilmDao(): FilmDao

    abstract fun getAgeRating(): AgeRatingDao


}