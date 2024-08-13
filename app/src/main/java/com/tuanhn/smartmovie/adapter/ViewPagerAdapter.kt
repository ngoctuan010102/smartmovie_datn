package com.tuanhn.smartmovie.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragment: FragmentActivity) : FragmentStateAdapter(fragment) {

    private val fragmentList: MutableList<Fragment> = mutableListOf()

    private val fragmentTitleList: MutableList<String> = mutableListOf()

    fun getFragmentList() = fragmentList

    override fun getItemCount(): Int = fragmentList.size

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

    fun addFragment(fragment: Fragment, title: String) {
        fragmentList.add(fragment)
        fragmentTitleList.add(title)
    }

    fun getPageTitle(position: Int): CharSequence = fragmentTitleList[position]

}