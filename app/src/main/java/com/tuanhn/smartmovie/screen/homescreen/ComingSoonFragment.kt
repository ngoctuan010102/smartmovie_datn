package com.tuanhn.smartmovie.screen.homescreen

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import com.tuanhn.smartmovie.adapter.MovieVerticalAdapter
import com.tuanhn.smartmovie.data.model.entities.Film
import com.tuanhn.smartmovie.databinding.FragmentViewpager2Binding
import com.tuanhn.smartmovie.screen.NotificationWorker
import com.tuanhn.smartmovie.viewmodels.ViewModelAPI
import com.tuanhn.smartmovie.viewmodels.ViewModelDB
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class ComingSoonFragment : Fragment() {

    private val viewModelAPI: ViewModelAPI by viewModels()

    private val viewModelDB: ViewModelDB by viewModels()

    private var binding: FragmentViewpager2Binding? = null

    private var adapterVertical: MovieVerticalAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentViewpager2Binding.inflate(inflater, container, false)
        return binding?.root
    }
    private fun solveFavorite(film: Film) {

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModelAPI.getAPIFilmComingSoon(100)

        //initial adapter
        adapterVertical = MovieVerticalAdapter(listOf(), this@ComingSoonFragment::solveFavorite,
            this@ComingSoonFragment::pickDateTime, false)

        binding?.recyclerView?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding?.recyclerView?.adapter = adapterVertical
        //observe Data
        observeData()

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
                    if (!item.isNowPlaying)
                        filterList.add(item)
                }

                adapterVertical?.let { adapter ->
                    adapter.updateMovies(filterList)
                }
            })

        }
    }

    /*
        override fun onDestroyView() {
            super.onDestroyView()
            binding = null
        }

        override fun initialAdapter() {
            adapterHorizontal =
                MovieHorizontalAdapter(
                    this@NowPlayingFragment::solveFavorite,
                    listOf()
                )

            adapterVertical = MovieVerticalAdapter(
                this@NowPlayingFragment::solveFavorite,
                listOf()
            )

            binding?.let { bind ->

                bind.recyclerView.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

                bind.recyclerView.adapter = adapterVertical
            }
        }

        override fun changeListView(isGridLayout: Boolean) {

            binding?.let { bind ->
                if (isGridLayout) {

                    with(bind) {
                        adapterHorizontal?.let {adapter->
                            position = adapter.currentPosition
                        }

                        recyclerView.layoutManager =
                            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

                        recyclerView.adapter = adapterVertical

                        recyclerView.scrollToPosition(position)
                    }
                } else {

                    with(bind) {

                        adapterVertical?.let {adapter->
                            position = adapter.currentPosition
                        }

                        recyclerView.layoutManager = GridLayoutManager(context, 2)

                        recyclerView.adapter = adapterHorizontal

                        recyclerView.scrollToPosition(position - 1)
                    }
                }
            }
        }*/

}