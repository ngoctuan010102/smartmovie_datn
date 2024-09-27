package com.tuanhn.smartmovie.screen.homescreen.bookticket

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import com.tuanhn.smartmovie.adapter.ShowTimeAdapter
import com.tuanhn.smartmovie.data.model.entities.Room
import com.tuanhn.smartmovie.data.model.entities.Showtime
import com.tuanhn.smartmovie.data.network.respond.Cinema
import com.tuanhn.smartmovie.databinding.FragmentDetailFilmBinding

import com.tuanhn4.smartmovie.data.utils.UiState
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class DetailFilm : Fragment() {
    private val args: DetailFilmArgs by navArgs()

    private var listShowtime : MutableList<Showtime> = mutableListOf()

    private var listRoom : MutableSet<Int> = mutableSetOf()

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

        getShowTimeFromFireStore()

     //   callAPI()
    }

    private fun getShowTimeFromFireStore() {
        val db = FirebaseFirestore.getInstance()

        db.collection("showtimes").get().addOnSuccessListener {result->

            listShowtime.clear()

            listRoom.clear()

            for(document in result){
                val showtime = document.toObject(Showtime::class.java)
                if(showtime.film_id == args.film.film_id)
                {
                    listShowtime.add(showtime)
                    listRoom.add(showtime.room_id)
                }
               getRoomFromFireStore()
            }
        }
    }

    private fun getRoomFromFireStore() {
        val db = FirebaseFirestore.getInstance()
        val list: MutableList<Room> = mutableListOf()
        db.collection("rooms").get().addOnSuccessListener { result ->
            for (document in result) {
                val room = document.toObject(Room::class.java)

                if (listRoom.contains(room.room_id)) {
                    list.add(room)
                }
            }

            for (item in list.indices){
                updateCinema(list[item], item+1)
            }
        }
    }

    private fun setValueCinemaName(cinema: TextView, item: Room, adapter: ShowTimeAdapter) {

        cinema.visibility = View.VISIBLE

        cinema.text = "Room: ${item.room_name}"


        val filterList = listShowtime.filter { it.room_id == item.room_id }

        val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

        val sortedList = filterList.sortedBy { LocalTime.parse(it.start_time, timeFormatter) }

        val lastList = sortedList.filter {  LocalTime.parse(it.start_time, timeFormatter).isAfter(LocalTime.now())}
        adapter.updateShowTime(lastList, item)
    }

    //room
    private fun updateCinema(item: Room, position: Int) {
        when (position) {
            1 -> {
                binding?.cinema1?.let { cinema ->
                    adapterCinema1?.let { adapterCinema ->
                        setValueCinemaName(cinema, item, adapterCinema)
                    }
                }
            }

            2 -> {

                binding?.cinema2?.let { cinema ->
                    adapterCinema2?.let { adapterCinema ->
                        setValueCinemaName(cinema, item, adapterCinema)
                    }
                }

            }

            3 -> {
                binding?.cinema3?.let { cinema ->
                    adapterCinema3?.let { adapterCinema ->
                        setValueCinemaName(cinema, item, adapterCinema)
                    }
                }

            }

            4 -> {
                binding?.cinema4?.let { cinema ->
                    adapterCinema4?.let { adapterCinema ->
                        setValueCinemaName(cinema, item, adapterCinema)
                    }
                }

            }

            5 -> {
                binding?.cinema5?.let { cinema ->
                    adapterCinema5?.let { adapterCinema ->
                        setValueCinemaName(cinema, item, adapterCinema)
                    }
                }
            }

            6 -> {
                binding?.cinema6?.let { cinema ->
                    adapterCinema6?.let { adapterCinema ->
                        setValueCinemaName(cinema, item, adapterCinema)
                    }
                }

            }

            7 -> {
                binding?.cinema7?.let { cinema ->
                    adapterCinema7?.let { adapterCinema ->
                        setValueCinemaName(cinema, item, adapterCinema)
                    }
                }

            }

            8 -> {
                binding?.cinema8?.let { cinema ->
                    adapterCinema8?.let { adapterCinema ->
                        setValueCinemaName(cinema, item, adapterCinema)
                    }
                }

            }

        }
    }

    private fun setAdapter(adapter: ShowTimeAdapter?, rcv: RecyclerView?) {
        rcv?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rcv?.adapter = adapter
    }

    private fun initialAdapter() {
        adapterCinema1 = ShowTimeAdapter(listOf(), args.film, null, false, this@DetailFilm::displayShowtime)

        setAdapter(adapterCinema1, binding?.rcvCinema1)

        adapterCinema2 = ShowTimeAdapter(listOf(), args.film, null, false, this@DetailFilm::displayShowtime)

        setAdapter(adapterCinema2, binding?.rcvCinema2)

        adapterCinema3 = ShowTimeAdapter(listOf(), args.film, null, false, this@DetailFilm::displayShowtime)

        setAdapter(adapterCinema3, binding?.rcvCinema3)

        adapterCinema4 = ShowTimeAdapter(listOf(), args.film, null, false, this@DetailFilm::displayShowtime)

        setAdapter(adapterCinema4, binding?.rcvCinema4)

        adapterCinema5 = ShowTimeAdapter(listOf(), args.film, null, false, this@DetailFilm::displayShowtime)

        setAdapter(adapterCinema5, binding?.rcvCinema5)

        adapterCinema6 = ShowTimeAdapter(listOf(), args.film, null, false, this@DetailFilm::displayShowtime)

        setAdapter(adapterCinema6, binding?.rcvCinema6)

        adapterCinema7 = ShowTimeAdapter(listOf(), args.film,null, false, this@DetailFilm::displayShowtime)

        setAdapter(adapterCinema7, binding?.rcvCinema7)

        adapterCinema8 = ShowTimeAdapter(listOf(), args.film, null, false, this@DetailFilm::displayShowtime)

        setAdapter(adapterCinema8, binding?.rcvCinema8)
    }

    private fun displayShowtime(showtime: Showtime){

    }
    private fun setUpView(view: View) {

        Picasso.get()
            .load(args.film.poster)
            .into(binding?.imageView)

        binding?.textView?.text = args.film.film_name

        val videoView = binding?.videoView

        args.film.film_trailer?.let { trailer ->

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