package com.tuanhn.smartmovie.screen.searchscreen

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.tuanhn.smartmovie.R
import com.tuanhn.smartmovie.data.network.respond.SearchFilmRespond
import com.tuanhn.smartmovie.databinding.FragmentSearchBinding
import com.tuanhn4.smartmovie.data.utils.UiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {


    private lateinit var binding: FragmentSearchBinding

    private var adapter: SearchAdapter? = null

    //private var queryText: String? = null

    private val handler = Handler(Looper.getMainLooper())

    private var isButtonClicked = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSearchBinding.inflate(LayoutInflater.from(context), container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setButtonMoveEvent(requireActivity())
        //val list: MutableList<SearchFilmRespond> = mutableListOf()


        setUpAdapter(listOf())

        binding?.tvCancel?.setOnClickListener { tv ->
            adapter?.updateMovies(listOf())
            binding.edtSearch.text = null
            closeKeyboard()
            tv.visibility = View.GONE
        }

        observeData() // can review the history

        //setEvent(view)

        setEventForEdtSearch()
    }

    private fun observeData() {

 /*       viewModelAPI.getStateSearch().observe(
            viewLifecycleOwner,
            Observer { state ->
                when (state) {
                    is UiState.Loading -> {}
                    is UiState.Error -> {}
                    is UiState.Success -> {
                        adapter?.updateMovies(state.data)
                    }
                }
            })*/
    }


    private fun setUpAdapter(list: List<SearchFilmRespond>) {

        adapter = SearchAdapter(list)

        with(binding) {

            rcvSearch.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            rcvSearch.adapter = adapter
        }
    }


    private fun setEventForEdtSearch() {
        val typingDelay: Long = 1000

        val handler = Handler(Looper.getMainLooper())

        var userStoppedTypingRunnable: Runnable? = null

        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                userStoppedTypingRunnable?.let { handler.removeCallbacks(it) }

                binding?.searchProgressBar?.visibility = View.VISIBLE
            }

            override fun afterTextChanged(s: Editable?) {

                userStoppedTypingRunnable = Runnable {

                    val query = s.toString()

                    if (query.isNotEmpty()) {

                   //     callAPI(query)

                        //  queryText = query

                        binding.tvCancel.visibility = View.VISIBLE

                        binding.searchProgressBar.visibility = View.GONE
                    } else
                        adapter?.updateMovies(listOf())
                }

                handler.postDelayed(userStoppedTypingRunnable!!, typingDelay)
            }
        })
    }

/*    private fun callAPI(query: String) {
        viewModelAPI.getAPISearch(100, query)
    }*/


    private fun setButtonMoveEvent(activity: Activity) {

        val btnSearchFragment = activity.findViewById<TextView>(R.id.btnsearchFragment)

        btnSearchFragment.setOnClickListener {

            if (isButtonClicked) return@setOnClickListener
            isButtonClicked = true

            handler.postDelayed({
                isButtonClicked = false
            }, 1000)

            val tvSearch = activity.findViewById<TextView>(R.id.tvSearch)

            setMoveEvent(binding.root, btnSearchFragment, tvSearch, activity)
        }

        val btnGenres = activity.findViewById<TextView>(R.id.btnGenres)

        btnGenres.setOnClickListener {

            if (isButtonClicked) return@setOnClickListener
            isButtonClicked = true

            handler.postDelayed({
                isButtonClicked = false
            }, 1000)


            val tvGenres = activity.findViewById<TextView>(R.id.tvGenres)
            setMoveEvent(binding.root, btnGenres, tvGenres, activity)
        }

        val btnArtists = activity.findViewById<TextView>(R.id.btnArtists)

        btnArtists.setOnClickListener {
            if (isButtonClicked) return@setOnClickListener
            isButtonClicked = true

            handler.postDelayed({
                isButtonClicked = false
            }, 1000)


            val tvArtists = activity.findViewById<TextView>(R.id.tvArtists)
            setMoveEvent(binding.root, btnArtists, tvArtists, activity)
        }

        val btnDiscover = activity.findViewById<TextView>(R.id.btnHome)

        btnDiscover.setOnClickListener {
            if (isButtonClicked) return@setOnClickListener
            isButtonClicked = true

            handler.postDelayed({
                isButtonClicked = false
            }, 1000)

            val tvDiscover = activity.findViewById<TextView>(R.id.tvHome)

            setMoveEvent(binding.root, btnDiscover, tvDiscover, activity)
        }
    }


    private fun setMoveEvent(
        view: View,
        newChoice: TextView,
        newTVChoice: TextView,
        activity: Activity
    ) {
        setUpView(activity)
        changeBackgroundTint(newChoice, Color.GREEN)
        newTVChoice.setTextColor(Color.GREEN)
        moveToDestination(view, newTVChoice)
    }

    private fun setUpView(activity: Activity) {
        //change text color
        val tvSearch = activity.findViewById<TextView>(R.id.tvSearch)

        val tvDiscover = activity.findViewById<TextView>(R.id.tvHome)

        val tvGenres = activity.findViewById<TextView>(R.id.tvGenres)

        val tvArtists = activity.findViewById<TextView>(R.id.tvArtists)

        tvSearch.setTextColor(Color.BLACK)

        tvDiscover.setTextColor(Color.BLACK)

        tvGenres.setTextColor(Color.BLACK)

        tvArtists.setTextColor(Color.BLACK)

        //change button color
        val btnSearch = activity.findViewById<TextView>(R.id.btnsearchFragment)

        changeBackgroundTint(btnSearch, Color.BLACK)

        val btnDiscover = activity.findViewById<TextView>(R.id.btnHome)

        changeBackgroundTint(btnDiscover, Color.BLACK)

        val btnGenres = activity.findViewById<TextView>(R.id.btnGenres)

        changeBackgroundTint(btnGenres, Color.BLACK)

        val btnArtists = activity.findViewById<TextView>(R.id.btnArtists)

        changeBackgroundTint(btnArtists, Color.BLACK)
    }

    private fun changeBackgroundTint(view: View, color: Int) {
        ViewCompat.setBackgroundTintList(view, ColorStateList.valueOf(color))
    }

    private fun moveToDestination(view: View, tvChoice: TextView) {

        if (tvChoice.text.equals(getString(R.string.home_title)))
            Navigation.findNavController(view).navigate(R.id.homeFragment)

        if (tvChoice.text.equals(getString(R.string.search_title)))
            Navigation.findNavController(view).navigate(R.id.searchFragment)

        /* if (tvChoice.text.equals(getString(R.string.genres_title)))
             Navigation.findNavController(view).navigate(R.id.genresFragment)
*/
        if (tvChoice.text.equals(getString(R.string.artists_title)))
            Navigation.findNavController(view).navigate(R.id.userFragment)
    }

    private fun closeKeyboard() {

        val keyboard =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        val view = requireActivity().currentFocus

        if (view != null) {
            keyboard.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}