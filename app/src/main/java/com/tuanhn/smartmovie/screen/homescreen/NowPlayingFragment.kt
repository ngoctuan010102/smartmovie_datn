package com.tuanhn.smartmovie.screen.homescreen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.tuanhn.smartmovie.adapter.MovieVerticalAdapter
import com.tuanhn.smartmovie.databinding.FragmentViewpager2Binding
import com.tuanhn.smartmovie.viewmodels.ViewModelAPI
import com.tuanhn.smartmovie.ui.viewmodels.ViewModelDB
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NowPlayingFragment : Fragment() {

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //initial adapter
        adapterVertical = MovieVerticalAdapter(listOf())

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


    private fun observeData() {
        with(viewModelDB) {
            getAllFilms().observe(viewLifecycleOwner, Observer { list ->

                for (item in list)
                    Log.d("HS", item.toString())

                adapterVertical?.let { adapter ->
                    adapter.updateMovies(list)
                }
            })

            /*getAllFavorite().observe(viewLifecycleOwner, Observer { listFavorite ->

                adapterHorizontal?.let { adapter ->

                    adapter.updateFavorite(listFavorite)
                }

                adapterVertical?.let { adapter ->

                    adapter.updateFavorite(listFavorite)
                }
            })
        }*/
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