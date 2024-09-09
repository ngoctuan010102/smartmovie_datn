package com.tuanhn.smartmovie.screen.homescreen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import com.tuanhn.smartmovie.R
import com.tuanhn.smartmovie.databinding.ActivityMainBinding
import com.tuanhn.smartmovie.viewmodels.ViewModelAPI
import com.tuanhn.smartmovie.viewmodels.ViewModelDB
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

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

        /*        val movie = intent.getStringExtra("movieId")

                movie?.let {
                    val action = HomeFragmentDirections.actionHomeFragmentToDetailFilm(item)

                    Navigation.findNavController(view).navigate(action)
                    val navController = findNavController(R.id.fragmentContainerView)
                    val ac = NavDirections.ac
                    navController.navigate(R.id.detailFilm)
                }*/
    }
}