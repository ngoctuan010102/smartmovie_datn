package com.tuanhn.smartmovie.data.repository

import com.tuanhn.smartmovie.data.di.IODispatcher
import com.tuanhn.smartmovie.data.model.dao.AgeRatingDao
import com.tuanhn.smartmovie.data.model.dao.FilmDao
import com.tuanhn.smartmovie.data.model.entities.AgeRating
import com.tuanhn.smartmovie.data.model.entities.Film

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseRepository @Inject constructor(
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
    private val filmDao: FilmDao,
    private val ageRatingDao: AgeRatingDao,

    )  {

    suspend fun insertFilm(film: Film) {
        withContext(ioDispatcher) {
            filmDao.insertFilm(film)
        }
    }

    suspend fun insertAgeRating(ageRating: AgeRating) {
        withContext(ioDispatcher) {
            ageRatingDao.insertAgeRating(ageRating)
        }
    }

    fun getAllFilms() = filmDao.getAllFilms()

    fun getAllAgeRating() = ageRatingDao.getAllAgeRating()


}