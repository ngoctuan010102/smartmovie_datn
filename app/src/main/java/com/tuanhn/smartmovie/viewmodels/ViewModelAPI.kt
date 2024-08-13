package com.tuanhn.smartmovie.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuanhn.smartmovie.data.network.respond.Cinema
import com.tuanhn.smartmovie.data.repository.APIRepository
import com.tuanhn4.smartmovie.data.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewModelAPI @Inject constructor(
    private val apiRepository: APIRepository
) : ViewModel() {


    fun getAPIFilmNowShowing(n: Int) {
        viewModelScope.launch {
            apiRepository.getAPIFilmNowShowing(n)
        }
    }

    fun getAPIShowTime(n: Int, id: Int) {
        viewModelScope.launch {
         apiRepository.getAPIShowTime(n, id)
        }
    }

    fun getStateShowTime() = apiRepository.callAPIShowTime
}