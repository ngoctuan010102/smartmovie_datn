package com.tuanhn.smartmovie.data.model.dao

import androidx.lifecycle.LiveData
import androidx.room.Insert
import androidx.room.Query
import com.tuanhn.smartmovie.data.model.entities.Favorite
import com.tuanhn.smartmovie.data.model.entities.Film

interface FavoriteDao {
    @Query("SELECT * FROM favorite")
    fun getAllFavorite(): LiveData<List<Favorite>>

    @Query("SELECT * FROM favorite")
    fun getFavorite(): List<Favorite>

    @Insert
    suspend fun insertFavorite(favorite: Favorite)
}