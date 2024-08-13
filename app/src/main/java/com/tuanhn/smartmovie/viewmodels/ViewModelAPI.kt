package com.tuanhn.smartmovie.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuanhn.smartmovie.data.repository.APIRepository
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

    fun getAPISearch(n: Int, query: String){
        viewModelScope.launch {
            apiRepository.getAPISearch(n, query)
        }
    }

    fun getStateSearch() = apiRepository.callAPISearch

    fun getStateShowTime() = apiRepository.callAPIShowTime
}