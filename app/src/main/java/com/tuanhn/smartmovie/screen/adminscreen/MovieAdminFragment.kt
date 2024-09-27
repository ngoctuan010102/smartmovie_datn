package com.tuanhn.smartmovie.screen.adminscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.tuanhn.smartmovie.R
import com.tuanhn.smartmovie.adapter.MovieAdminAdapter
import com.tuanhn.smartmovie.adapter.RoomAdminAdapter
import com.tuanhn.smartmovie.data.model.entities.Film
import com.tuanhn.smartmovie.databinding.FragmentMovieAdminBinding
import com.tuanhn.smartmovie.databinding.FragmentRoomAdminBinding

class MovieAdminFragment : Fragment() {

    private lateinit var binding: FragmentMovieAdminBinding

    private var adapter: MovieAdminAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMovieAdminBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialAdapter()

        observeData()
    }

    private fun observeData() {
        val db = FirebaseFirestore.getInstance()

        db.collection("films").get().addOnSuccessListener { result ->
            val list: MutableList<Film> = mutableListOf()
            for (document in result) {

                val film = document.toObject(Film::class.java)

                list.add(film)
            }
            adapter?.updateMovies(list)
        }
    }

    private fun initialAdapter() {
        adapter = MovieAdminAdapter(listOf())

        binding?.rcvMovieAdmin?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding?.rcvMovieAdmin?.adapter = adapter

    }
}