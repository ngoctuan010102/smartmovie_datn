package com.tuanhn.smartmovie.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.tuanhn.smartmovie.data.di.IODispatcher
import com.tuanhn.smartmovie.data.model.dao.AgeRatingDao
import com.tuanhn.smartmovie.data.model.dao.FilmDao
import com.tuanhn.smartmovie.data.model.entities.AgeRating
import com.tuanhn.smartmovie.data.model.entities.Film
import com.tuanhn.smartmovie.data.network.ApiService
import com.tuanhn.smartmovie.data.network.respond.Cinema
import com.tuanhn.smartmovie.data.network.respond.Images
import com.tuanhn.smartmovie.data.network.respond.SearchFilmRespond
import com.tuanhn.smartmovie.data.utils.Constants
import com.tuanhn4.smartmovie.data.utils.UiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class APIRepository @Inject constructor(
    private val apiServiceDI: ApiService,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
    private val filmDao: FilmDao,
    private val ageRatingDao: AgeRatingDao,

    ) {

    private val apiKey = Constants.apiKey

    //SupervisorJob()

    private val scope = CoroutineScope(ioDispatcher)

    private val client = "STUD_356"

    private val authentication = "Basic U1RVRF8zNTZfWFg6bDlIUWx1ZzluZ2oy"

    private val territory = "XX"

    private val apiVersion = "v200"

    private val geolocation = "-22.0;14.0"

    private val currentDateTime = LocalDateTime.now()

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    private val formattedDateTime = currentDateTime.format(formatter)

    private val _callAPIShowTime = MutableLiveData<UiState<List<Cinema>>>(UiState.Loading)

    val callAPIShowTime: LiveData<UiState<List<Cinema>>> = _callAPIShowTime

    private val _callAPISearch = MutableLiveData<UiState<List<SearchFilmRespond>>>(UiState.Loading)

    val callAPISearch: LiveData<UiState<List<SearchFilmRespond>>> = _callAPISearch

    suspend fun getAPIShowTime(n: Int, filmID: Int) {

        scope.launch {

            try {

                val respond = apiServiceDI.getFilmShowTime(
                    client,
                    apiKey,
                    authentication,
                    territory,
                    apiVersion,
                    geolocation,
                    LocalDateTime.now().toString() + "Z",
                    n,
                    filmID,
                    formattedDateTime
                )

                if (respond.isSuccessful) {
                    val cinemas = respond.body()?.cinemas
                    cinemas?.let {
                        _callAPIShowTime.postValue(UiState.Success(cinemas))
                    }
                } else {
                    _callAPIShowTime.postValue(UiState.Error(Error("Failed")))
                }

            } catch (e: Exception) {
                _callAPIShowTime.postValue(UiState.Error(Error("Failed")))
            }
        }
    }

    suspend fun getAPISearch(n: Int, query: String) {
        scope.launch {
            try {
                val respond = apiServiceDI.searchFilm(
                    client,
                    apiKey,
                    authentication,
                    territory,
                    apiVersion,
                    geolocation,
                    LocalDateTime.now().toString() + "Z",
                    n,
                    query
                )
                if (respond.isSuccessful) {
                    val films = respond.body()?.films
                    films?.let {films->
                        _callAPISearch.postValue(UiState.Success(films))
                    }
                } else {
                    _callAPISearch.postValue(UiState.Error(Error("Failed")))
                }

            } catch (e: Exception) {
                _callAPISearch.postValue(UiState.Error(Error("Failed")))
            }
        }
    }

    suspend fun getAPIFilmComingSoon(n: Int) {
        scope.launch {
            try {
                val response = apiServiceDI.getFilmsComingSoon(
                    client,
                    apiKey,
                    authentication,
                    territory,
                    apiVersion,
                    geolocation,
                    LocalDateTime.now().toString() + "Z",
                    n
                )
                Log.d("sscccds", response.body()?.films.toString())
                response.body()?.films?.let { listFilm ->
                    for (film in listFilm) {
                        val releaseDate = film.release_dates

                        val imagesJson = Gson().toJson(film.images)

                        val images = Gson().fromJson(imagesJson, Images::class.java)

                        val poster: String? =
                            images.poster?.values?.firstOrNull()?.medium?.film_image

                        val still: String? =
                            images.still?.values?.firstOrNull()?.medium?.film_image


                        val newFilm = Film(
                            0,
                            film.film_id,
                            film.imdb_id,
                            film.imdb_title_id,
                            film.film_name,
                            film.other_titles.toString(),
                            releaseDate[0].release_date,
                            film.film_trailer,
                            film.synopsis_long,
                            poster,
                            still,
                            false
                        )

                        val ageRating = film.age_rating

                        val newAgeRating = AgeRating(
                            0,
                            film.film_id,
                            ageRating[0].rating,
                            ageRating[0].age_rating_image,
                            ageRating[0].age_advisory
                        )

                        val list = filmDao.getFilms()

                        val checkList =
                            list.none { film -> film.film_id == newFilm.film_id }

                        if (checkList) {
                            filmDao.insertFilm(newFilm)
                            ageRatingDao.insertAgeRating(newAgeRating)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.d("SH", "EFailed")
            }
        }
    }

    suspend fun getAPIFilmNowShowing(n: Int) {
        scope.launch {
            try {
                val response = apiServiceDI.getFilmsNowPlaying(
                    client,
                    apiKey,
                    authentication,
                    territory,
                    apiVersion,
                    geolocation,
                    LocalDateTime.now().toString() + "Z",
                    n
                )
                Log.d("sscccds", response.body()?.films.toString())
                    response.body()?.films?.let { listFilm ->
                        for (film in listFilm) {
                            val releaseDate = film.release_dates

                            val imagesJson = Gson().toJson(film.images)

                            val images = Gson().fromJson(imagesJson, Images::class.java)

                            val poster: String? =
                                images.poster?.values?.firstOrNull()?.medium?.film_image

                            val still: String? =
                                images.still?.values?.firstOrNull()?.medium?.film_image


                            val newFilm = Film(
                                0,
                                film.film_id,
                                film.imdb_id,
                                film.imdb_title_id,
                                film.film_name,
                                film.other_titles.toString(),
                                releaseDate[0].release_date,
                                film.film_trailer,
                                film.synopsis_long,
                                poster,
                                still,
                                true
                            )

                            val ageRating = film.age_rating

                            val newAgeRating = AgeRating(
                                0,
                                film.film_id,
                                ageRating[0].rating,
                                ageRating[0].age_rating_image,
                                ageRating[0].age_advisory
                            )

                            val list = filmDao.getFilms()

                            val checkList =
                                list.none { film -> film.film_id == newFilm.film_id }

                            if (checkList) {
                                filmDao.insertFilm(newFilm)
                                ageRatingDao.insertAgeRating(newAgeRating)
                            }
                        }
                }
            } catch (e: Exception) {
                Log.d("SH", "EFailed")
            }
        }
    }
}