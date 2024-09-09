package com.tuanhn.smartmovie.screen.userscreen

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.toObject
import com.tuanhn.smartmovie.R
import com.tuanhn.smartmovie.adapter.MovieVerticalAdapter
import com.tuanhn.smartmovie.data.model.entities.Favorite
import com.tuanhn.smartmovie.data.model.entities.Film
import com.tuanhn.smartmovie.databinding.FragmentFavoriteScreenBinding
import com.tuanhn.smartmovie.databinding.FragmentViewpager2Binding
import java.util.Calendar

class FavoriteScreen : Fragment() {


    private var adapterVertical: MovieVerticalAdapter? = null

    private var currentUser: String? = null

    private var binding: FragmentFavoriteScreenBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        binding = FragmentFavoriteScreenBinding.inflate(inflater, container, false)

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = context?.getSharedPreferences("current_user", Context.MODE_PRIVATE)

        currentUser = sharedPreferences?.getString("current_user", "default_value")

        adapterVertical = MovieVerticalAdapter(
            listOf(), this@FavoriteScreen::solveFavorite, this@FavoriteScreen::pickDateTime, true
        )

        binding?.rcvFavorite?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding?.rcvFavorite?.adapter = adapterVertical

        observeData()
    }

    private fun pickDateTime(movie: Film) {
        val calendar = Calendar.getInstance()

        // Date Picker Dialog
        val datePicker = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                // After date is picked, show the time picker
                pickTime(year, month, dayOfMonth)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun pickTime(year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()

        // Time Picker Dialog
        val timePicker = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                // After time is picked, set the selected date and time
                val selectedDateTime = String.format(
                    "%02d/%02d/%04d %02d:%02d",
                    dayOfMonth, month + 1, year, hourOfDay, minute
                )
                // tvSelectedDateTime.text = "Selected DateTime: $selectedDateTime"
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
        timePicker.show()
    }

    private fun observeData() {

        val db = FirebaseFirestore.getInstance()

        val ref = db.collection("favoriteFilms")
        ref.get().addOnSuccessListener { result ->
            result?.let {
                getRef(result, db)
            }
        }
        ref.addSnapshotListener { result, e ->
            result?.let {
                getRef(result, db)
            }
        }
    }

    private fun getRef(result: QuerySnapshot, db: FirebaseFirestore) {
        val listFavorite: MutableList<Favorite> = mutableListOf()

        for (document in result) {

            val userName = document.getString("user_Name")

            if (userName == currentUser) {

                val favorite = document.toObject(Favorite::class.java)

                favorite?.let {
                    listFavorite.add(favorite)
                }
            }
        }
        Log.d("sj", listFavorite.toString())

        //adapterVertical?.updateFavorite(listFavorite)

        getFilms(db, listFavorite)
    }

    private fun getFilms(db: FirebaseFirestore, listFavorite: List<Favorite>) {
        val listFilm: MutableList<Film> = mutableListOf()
        db.collection("films").get().addOnSuccessListener { result ->
            for (document in result) {
                val film = document.toObject(Film::class.java)
                listFilm.add(film)
            }
            val listFinalFilm: MutableList<Film> = mutableListOf()

            for (item in listFilm) {
                val x = listFavorite.filter { it.film_id == item.film_id }

                if (x.isNotEmpty()) {
                    listFinalFilm.add(item)
                }
            }
            Log.d("sj", listFinalFilm.toString())

            adapterVertical?.updateMovies(listFinalFilm)

        }
    }

    private fun solveFavorite(film: Film) {

        val favoriteId = currentUser + film.film_id

        currentUser?.let { user ->

            val favorite = Favorite(favoriteId, user, film.film_id)

            checkData(favorite)
        }
    }

    private fun checkData(favorite: Favorite) {

        val db = FirebaseFirestore.getInstance()

        db.collection("favoriteFilms").get().addOnSuccessListener { result ->
            var isExist = false
            var docId: String? = null
            for (document in result) {
                Log.d("dsdd", document.getString("id_favorite").toString())
                if (favorite.id_favorite == document.getString("id_favorite")) {
                    isExist = true
                    docId = document.id
                    Log.d("equal", document.getString("id_favorite").toString())
                    break
                }
            }
            if (isExist) {
                docId?.let {
                    db.collection("favoriteFilms").document(docId).delete()
                }
                Log.d("delete", "finished")
            } else setData(favorite, db)
        }
    }

    private fun setData(favorite: Favorite, db: FirebaseFirestore) {

        val documentRef = db.collection("favoriteFilms")

        documentRef.add(favorite)

    }
}

