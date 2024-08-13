package com.tuanhn.smartmovie.screen.homescreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import com.tuanhn.smartmovie.adapter.ViewPagerAdapter
import com.tuanhn.smartmovie.databinding.FragmentHomeBinding
import com.tuanhn.smartmovie.screen.homescreen.ComingSoonFragment
import com.tuanhn.smartmovie.screen.homescreen.NowPlayingFragment

class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding? = null

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

}