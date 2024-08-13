package com.tuanhn.smartmovie.data.model.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.tuanhn.smartmovie.data.model.entities.Film

@Dao
interface FilmDao {
    @Query("SELECT * FROM films")
    fun getAllFilms(): LiveData<List<Film>>

    @Query("SELECT * FROM films")
    fun getFilms(): List<Film>
    @Insert
    suspend fun insertFilm(film: Film)

}