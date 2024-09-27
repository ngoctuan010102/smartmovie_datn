package com.tuanhn.smartmovie.screen.homescreen

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.tuanhn.smartmovie.adapter.MovieVerticalAdapter
import com.tuanhn.smartmovie.data.model.entities.Favorite
import com.tuanhn.smartmovie.data.model.entities.Film
import com.tuanhn.smartmovie.databinding.FragmentViewpager2Binding
import com.tuanhn.smartmovie.screen.NotificationWorker
import com.tuanhn.smartmovie.viewmodels.ViewModelDB
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class NowPlayingFragment : Fragment() {


    private val viewModelDB: ViewModelDB by viewModels()

    private var binding: FragmentViewpager2Binding? = null

    private var adapterVertical: MovieVerticalAdapter? = null

    private var currentUser: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentViewpager2Binding.inflate(inflater, container, false)
        return binding?.root
    }

    private fun setData(favorite: Favorite, db: FirebaseFirestore) {

        val documentRef = db.collection("favoriteFilms")

        documentRef.add(favorite)

    }

    private fun solveFavorite(film: Film) {

        val db = FirebaseFirestore.getInstance()

        val favoriteId = currentUser + film.film_id

        currentUser?.let { user ->

            val favorite = Favorite(favoriteId, user, film.film_id)

            checkData(favorite, db, film)
        }
    }

    private fun checkData(favorite: Favorite, db: FirebaseFirestore, film: Film) {

        db.collection("favoriteFilms")
            .get()
            .addOnSuccessListener { result ->
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
                } else
                    setData(favorite, db)
            }
    }

    private fun updateFavorite() {

        val db = FirebaseFirestore.getInstance()

        val collectionRef = db.collection("favoriteFilms")

        collectionRef.get().addOnSuccessListener { snapshots ->
            snapshots?.let {
                getRef(snapshots)
            }
        }

        collectionRef.addSnapshotListener { snapshots, e ->

            if (e != null) {
                Log.w("Firestore", "Listen failed.", e)
                return@addSnapshotListener
            }

            snapshots?.let {
                getRef(snapshots)
            }
        }
    }

    private fun getRef(snapshots: QuerySnapshot) {

        val list: MutableList<Favorite> = mutableListOf()

        for (document in snapshots) {

            val userName = document.getString("user_Name")

            if (userName == currentUser) {

                val favoriteId = document.getString("id_favorite")

                val filmId = document.getLong("film_id")?.toInt()

                userName?.let {

                    favoriteId?.let {

                        filmId?.let {

                            val favorite = Favorite(favoriteId, userName, filmId.toInt())

                            list.add(favorite)
                        }
                    }
                }
            }
        }
        adapterVertical?.updateFavorite(list)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val sharedPreferences = context?.getSharedPreferences("current_user", Context.MODE_PRIVATE)

        currentUser = sharedPreferences?.getString("current_user", "default_value")


        //viewModelAPI.getAPIFilmNowShowing(100)
        getDataFromFirestore()

        //initial adapter
        adapterVertical = MovieVerticalAdapter(
            listOf(),
            this@NowPlayingFragment::solveFavorite,
            this@NowPlayingFragment::pickDateTime,
            false
        )

        binding?.recyclerView?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding?.recyclerView?.adapter = adapterVertical
        //observe Data
        observeData()

        updateFavorite()
        // setButtonMoveEvent(requireActivity())
        //refresh data
        binding?.let { bind ->
            bind.swipeRefreshLayout.setOnRefreshListener {
                bind.swipeRefreshLayout.isRefreshing = false
            }
        }
        //load more data
        /*        binding?.let { bind ->

                    bind.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

                        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                            super.onScrolled(recyclerView, dx, dy)

                            if (!recyclerView.canScrollVertically(1) && dy > 0) {
                                val page = currentPageAPI + 1

                                viewModelAPI.getAPINowPlayingMovies(page.toString())

                                currentPageAPI = page

                                Toast.makeText(context, "Loading data", Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
                }*/
    }

    private fun getDataFromFirestore() {

        val db = FirebaseFirestore.getInstance()

        viewModelDB.deleteAllFilms()

        db.collection("films").get().addOnSuccessListener { result ->
            for (document in result) {
                val newFilm = document.toObject(Film::class.java)

                viewModelDB.insertFilm(newFilm)
            }
        }
    }

    private fun pickDateTime(movie: Film) {
        val calendar = Calendar.getInstance()

        // Date Picker Dialog
        val datePicker = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                // After date is picked, show the time picker
                pickTime(year, month, dayOfMonth, movie)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun pickTime(year: Int, month: Int, dayOfMonth: Int, movie: Film) {
        val calendar = Calendar.getInstance()

        // Time Picker Dialog
        val timePicker = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                // After time is picked, set the selected date and time
                val selectedDateTime = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    set(Calendar.HOUR_OF_DAY, hourOfDay)
                    set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, 0)
                }

                scheduleNotification(selectedDateTime, movie)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
        timePicker.show()
    }

    private fun scheduleNotification(selectedDateTime: Calendar, movie: Film) {
        val currentDateTime = Calendar.getInstance()

        if (selectedDateTime.before(currentDateTime)) {
            // If the selected date and time is in the past, adjust it to the future (e.g., next year)
            selectedDateTime.add(Calendar.YEAR, 1)
        }

        val delayInMillis = selectedDateTime.timeInMillis - currentDateTime.timeInMillis

        val inputData = Data.Builder()
            .putString("movie", movie.film_name)
            .putString("movieId", movie.film_id.toString())
            .build()

        // CreateOrder a one-time WorkRequest to trigger at the selected date and time
        val notificationWorkRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(delayInMillis, TimeUnit.MILLISECONDS) // Schedule with the delay
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(requireContext()).enqueueUniqueWork(
            "ScheduledNotificationWork",
            ExistingWorkPolicy.REPLACE,
            notificationWorkRequest
        )
    }

    private fun observeData() {

        with(viewModelDB) {
            getAllFilms().observe(viewLifecycleOwner, Observer { list ->

                val filterList: MutableList<Film> = mutableListOf()

                for (item in list) {
                    filterList.add(item)
                }

                adapterVertical?.let { adapter ->
                    adapter.updateMovies(filterList)
                }
            })
        }
    }
}