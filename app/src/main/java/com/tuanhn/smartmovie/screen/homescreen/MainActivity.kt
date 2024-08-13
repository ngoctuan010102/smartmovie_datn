package com.tuanhn.smartmovie.screen.homescreen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.tuanhn.smartmovie.R
import com.tuanhn.smartmovie.databinding.ActivityMainBinding
import com.tuanhn.smartmovie.viewmodels.ViewModelAPI
import com.tuanhn.smartmovie.ui.viewmodels.ViewModelDB
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModelDB: ViewModelDB by viewModels()

    private val viewModelAPI: ViewModelAPI by viewModels()

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        with(binding) {
            tvGenres.text = getString(R.string.genres_title)
            tvArtists.text = getString(R.string.artists_title)
            tvHome.text = getString(R.string.home_title)
            tvSearch.text = getString(R.string.search_title)
        }
        setContentView(binding.root)
    }
}