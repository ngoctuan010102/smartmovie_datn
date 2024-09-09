package com.tuanhn.smartmovie.screen.homescreen

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.navigation.Navigation
import com.google.android.material.tabs.TabLayoutMediator
import com.tuanhn.smartmovie.R
import com.tuanhn.smartmovie.adapter.ViewPagerAdapter
import com.tuanhn.smartmovie.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding? = null

    private val handler = Handler(Looper.getMainLooper())

    private var isButtonClicked = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()

        setButtonMoveEvent(requireActivity())
    }


    private fun setupView() {

        setUpAdapter()


    }


    private fun setUpAdapter() {

        val viewPager = binding?.viewPager2

        val tabLayout = binding?.tabLayout

        val adapter = ViewPagerAdapter(requireActivity())

        adapter.addFragment(NowPlayingFragment(), "Now Playing")
        adapter.addFragment(ComingSoonFragment(), "Coming Soon")

        viewPager?.adapter = adapter

        //create all fragments
        binding?.viewPager2?.offscreenPageLimit = adapter.itemCount

        // Attach the ViewPager to the TabLayout
        TabLayoutMediator(tabLayout!!, viewPager!!) { tab, position ->
            tab.text = adapter.getPageTitle(position)
        }.attach()
    }

    private fun setButtonMoveEvent(activity: Activity) {

        val btnSearchFragment = activity.findViewById<TextView>(R.id.btnsearchFragment)

        btnSearchFragment.setOnClickListener {

            if (isButtonClicked) return@setOnClickListener
            isButtonClicked = true

            handler.postDelayed({
                isButtonClicked = false
            }, 1000)

            val tvSearch = activity.findViewById<TextView>(R.id.tvSearch)

            binding?.let { bind ->
                setMoveEvent(bind.root, btnSearchFragment, tvSearch, activity)
            }
        }

        val btnGenres = activity.findViewById<TextView>(R.id.btnGenres)

        btnGenres.setOnClickListener {

            if (isButtonClicked) return@setOnClickListener
            isButtonClicked = true

            handler.postDelayed({
                isButtonClicked = false
            }, 1000)


            val tvGenres = activity.findViewById<TextView>(R.id.tvGenres)
            binding?.let { bind ->
                setMoveEvent(bind.root, btnGenres, tvGenres, activity)
            }
        }

        val btnArtists = activity.findViewById<TextView>(R.id.btnArtists)

        btnArtists.setOnClickListener {
            if (isButtonClicked) return@setOnClickListener
            isButtonClicked = true

            handler.postDelayed({
                isButtonClicked = false
            }, 1000)


            val tvArtists = activity.findViewById<TextView>(R.id.tvArtists)

            binding?.let { bind ->
                setMoveEvent(bind.root, btnArtists, tvArtists, activity)
            }
        }

        val btnDiscover = activity.findViewById<TextView>(R.id.btnHome)

        btnDiscover.setOnClickListener {
            if (isButtonClicked) return@setOnClickListener
            isButtonClicked = true

            handler.postDelayed({
                isButtonClicked = false
            }, 1000)

            val tvDiscover = activity.findViewById<TextView>(R.id.tvHome)

            binding?.let { bind ->
                setMoveEvent(bind.root, btnDiscover, tvDiscover, activity)
            }
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
}