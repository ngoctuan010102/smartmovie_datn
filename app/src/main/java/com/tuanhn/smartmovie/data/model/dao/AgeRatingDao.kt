package com.tuanhn.smartmovie.data.model.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.tuanhn.smartmovie.data.model.entities.AgeRating

@Dao
interface AgeRatingDao {
    @Query("SELECT * FROM age_ratings")
    fun getAllAgeRating(): LiveData<List<AgeRating>>

    @Insert
    suspend fun insertAgeRating(ageRating: AgeRating)

}