package com.tuanhn.smartmovie.screen.homescreen.bookticket

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.squareup.picasso.Picasso
import com.tuanhn.smartmovie.adapter.ShowTimeAdapter
import com.tuanhn.smartmovie.data.network.respond.Cinema
import com.tuanhn.smartmovie.databinding.FragmentDetailFilmBinding
import com.tuanhn.smartmovie.viewmodels.ViewModelAPI
import com.tuanhn4.smartmovie.data.utils.UiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFilm : Fragment() {
    private val args: DetailFilmArgs by navArgs()

    private val viewModelAPI: ViewModelAPI by viewModels()

    private var binding: FragmentDetailFilmBinding? = null

    private var adapterCinema1: ShowTimeAdapter? = null

    private var adapterCinema2: ShowTimeAdapter? = null

    private var adapterCinema3: ShowTimeAdapter? = null

    private var adapterCinema4: ShowTimeAdapter? = null

    private var adapterCinema5: ShowTimeAdapter? = null

    private var adapterCinema6: ShowTimeAdapter? = null

    private var adapterCinema7: ShowTimeAdapter? = null

    private var adapterCinema8: ShowTimeAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailFilmBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpView(view)

        initialAdapter()

        callAPI()
    }

    private fun callAPI() {

        viewModelAPI.getAPIShowTime(10, args.film.film_id)

        viewModelAPI.getStateShowTime().observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is UiState.Loading -> {
                    Log.d("API", "LOADING")
                }

                is UiState.Success -> {
                    Log.d("API", "SUCCESS")
                    val list = state.data
                    for (position in list.indices) {
                        updateCinema(list[position], position + 1)
                    }
                }

                is UiState.Error -> {
                    Log.d("API", "FAIL")
                }
            }
        })
    }

    private fun updateCinema(item: Cinema, position: Int) {
        when (position) {
            1 -> {
                binding?.cinema1?.text = "${item.cinema_name} ${item.distance.toInt()}km"

                val listUpdate = item.showings.Standard.times

                adapterCinema1?.updateShowTime(listUpdate, item.cinema_name)

            }

            2 -> {
                binding?.cinema2?.text = "${item.cinema_name} ${item.distance.toInt()}km"

                val listUpdate = item.showings.Standard.times

                adapterCinema2?.updateShowTime(listUpdate, item.cinema_name)

            }

            3 -> {
                binding?.cinema3?.text = "${item.cinema_name} ${item.distance.toInt()}km"

                val listUpdate = item.showings.Standard.times

                adapterCinema3?.updateShowTime(listUpdate, item.cinema_name)

            }

            4 -> {
                binding?.cinema4?.text = "${item.cinema_name} ${item.distance.toInt()}km"

                val listUpdate = item.showings.Standard.times

                adapterCinema4?.updateShowTime(listUpdate, item.cinema_name)

            }

            5 -> {
                binding?.cinema5?.text = "${item.cinema_name} ${item.distance.toInt()}km"

                val listUpdate = item.showings.Standard.times

                adapterCinema5?.updateShowTime(listUpdate, item.cinema_name)
            }

            6 -> {
                binding?.cinema6?.text = "${item.cinema_name} ${item.distance.toInt()}km"

                val listUpdate = item.showings.Standard.times

                adapterCinema6?.updateShowTime(listUpdate, item.cinema_name)

            }

            7 -> {
                binding?.cinema7?.text = "${item.cinema_name} ${item.distance.toInt()}km"

                val listUpdate = item.showings.Standard.times

                adapterCinema7?.updateShowTime(listUpdate, item.cinema_name)

            }

            8 -> {
                binding?.cinema8?.text = "${item.cinema_name} ${item.distance.toInt()}km"

                val listUpdate = item.showings.Standard.times

                adapterCinema8?.updateShowTime(listUpdate, item.cinema_name)

            }

        }
    }

    private fun initialAdapter() {
        binding?.let { bind ->
            bind.cinema1.text = "Cinema 1"
            bind.cinema2.text = "Cinema 2"
            bind.cinema3.text = "Cinema 3"
            bind.cinema4.text = "Cinema 4"
            bind.cinema5.text = "Cinema 5"
            bind.cinema6.text = "Cinema 6"
            bind.cinema7.text = "Cinema 7"
            bind.cinema8.text = "Cinema 8"
        }

        adapterCinema1 = ShowTimeAdapter(listOf(), args.film, binding?.cinema1?.text.toString())
        binding?.rcvCinema1?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding?.rcvCinema1?.adapter = adapterCinema1

        adapterCinema2 = ShowTimeAdapter(listOf(), args.film,binding?.cinema2?.text.toString())
        binding?.rcvCinema2?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding?.rcvCinema2?.adapter = adapterCinema2

        adapterCinema3 = ShowTimeAdapter(listOf(), args.film, binding?.cinema3?.text.toString())
        binding?.rcvCinema3?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding?.rcvCinema3?.adapter = adapterCinema3

        adapterCinema4 = ShowTimeAdapter(listOf(), args.film, binding?.cinema4?.text.toString())
        binding?.rcvCinema4?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding?.rcvCinema4?.adapter = adapterCinema4

        adapterCinema5 = ShowTimeAdapter(listOf(), args.film, binding?.cinema5?.text.toString())
        binding?.rcvCinema5?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding?.rcvCinema5?.adapter = adapterCinema5

        adapterCinema6 = ShowTimeAdapter(listOf(), args.film, binding?.cinema6?.text.toString())
        binding?.rcvCinema6?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding?.rcvCinema6?.adapter = adapterCinema6

        adapterCinema7 = ShowTimeAdapter(listOf(), args.film, binding?.cinema7?.text.toString())
        binding?.rcvCinema7?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding?.rcvCinema7?.adapter = adapterCinema7

        adapterCinema8 = ShowTimeAdapter(listOf(), args.film, binding?.cinema8?.text.toString())
        binding?.rcvCinema8?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding?.rcvCinema8?.adapter = adapterCinema8
    }

    private fun setUpView(view: View) {

        Picasso.get()
            .load(args.film.poster)
            .into(binding?.imageView)

        binding?.textView?.text = args.film.film_name

        val videoView = binding?.videoView

        args.film.film_trailer?.let {trailer->

            val videoUri: Uri = Uri.parse(trailer)

            videoView?.setVideoURI(videoUri)
        }

        binding?.videoView?.setOnClickListener {
            args.film.film_trailer?.let { url ->
                val action = DetailFilmDirections.actionDetailFilmToVideoFragment(url)
                Navigation.findNavController(view).navigate(action)
            }
        }

        binding?.let { bind ->

            bind.cinema1?.setOnClickListener {
                if (bind?.rcvCinema1?.isVisible == true) {
                    bind?.rcvCinema1?.visibility = View.GONE
                } else
                    bind?.rcvCinema1?.visibility = View.VISIBLE
            }

            bind.cinema2?.setOnClickListener {
                if (bind?.rcvCinema2?.isVisible == true) {
                    bind?.rcvCinema2?.visibility = View.GONE
                } else
                    bind?.rcvCinema2?.visibility = View.VISIBLE
            }

            bind.cinema3?.setOnClickListener {
                if (bind?.rcvCinema3?.isVisible == true) {
                    bind?.rcvCinema3?.visibility = View.GONE
                } else
                    bind?.rcvCinema3?.visibility = View.VISIBLE
            }

            bind.cinema4?.setOnClickListener {
                if (bind?.rcvCinema4?.isVisible == true) {
                    bind?.rcvCinema4?.visibility = View.GONE
                } else
                    bind?.rcvCinema4?.visibility = View.VISIBLE
            }

            bind.cinema5?.setOnClickListener {
                if (bind?.rcvCinema5?.isVisible == true) {
                    bind?.rcvCinema5?.visibility = View.GONE
                } else
                    bind?.rcvCinema5?.visibility = View.VISIBLE
            }

            bind.cinema6?.setOnClickListener {
                if (bind?.rcvCinema6?.isVisible == true) {
                    bind?.rcvCinema6?.visibility = View.GONE
                } else
                    bind?.rcvCinema6?.visibility = View.VISIBLE
            }

            bind.cinema7?.setOnClickListener {
                if (bind?.rcvCinema7?.isVisible == true) {
                    bind?.rcvCinema7?.visibility = View.GONE
                } else
                    bind?.rcvCinema7?.visibility = View.VISIBLE
            }

            bind.cinema8?.setOnClickListener {
                if (binding?.rcvCinema8?.isVisible == true) {
                    binding?.rcvCinema8?.visibility = View.GONE
                } else
                    binding?.rcvCinema8?.visibility = View.VISIBLE
            }
        }
    }
}