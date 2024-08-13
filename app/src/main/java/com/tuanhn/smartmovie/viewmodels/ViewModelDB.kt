package com.tuanhn.smartmovie.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuanhn.smartmovie.data.model.entities.AgeRating
import com.tuanhn.smartmovie.data.model.entities.Film
import com.tuanhn.smartmovie.data.repository.DatabaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewModelDB @Inject constructor(
    private val dbRepository: DatabaseRepository
) : ViewModel() {

    fun insertFilm(film: Film) {
        viewModelScope.launch {
            dbRepository.insertFilm(film)
        }
    }

    fun insertAgeRating(ageRating: AgeRating){
        viewModelScope.launch {
            dbRepository.insertAgeRating(ageRating)
        }
    }

    fun getAllFilms() = dbRepository.getAllFilms()

    fun getAllAgeRating() = dbRepository.getAllAgeRating()
    /*

        fun deleteFavorite(favorite: Favorite) {
            viewModelScope.launch {
                dbRepository.deleteFavorite(favorite)
            }
        }

        fun insertFavorite(favorite: Favorite){
            viewModelScope.launch {
                dbRepository.insertFavorite(favorite)
            }
        }

        fun getAllNowPlaying() = dbRepository.getStateNowPlaying()

        fun getAllPopular() = dbRepository.getStatePopular()

        fun getAllTopRated() = dbRepository.getStateTopRated()

        fun getAllUpComing() = dbRepository.getStateUpcoming()

        fun getAllFavorite() = dbRepository.getAllFavorite()

        fun solveFavorite(id: String) {
            viewModelScope.launch {
                dbRepository.solveFavorite(id)
            }
        }
    */

}