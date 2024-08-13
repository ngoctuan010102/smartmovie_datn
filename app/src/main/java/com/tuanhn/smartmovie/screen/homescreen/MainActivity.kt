package com.tuanhn.smartmovie.screen.homescreen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.tuanhn.smartmovie.R
import com.tuanhn.smartmovie.viewmodels.ViewModelAPI
import com.tuanhn.smartmovie.ui.viewmodels.ViewModelDB
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModelDB: ViewModelDB by viewModels()
    private val viewModelAPI: ViewModelAPI by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModelAPI.getAPIFilmNowShowing(20)
    }
}