package com.tuanhn.smartmovie.screen.userscreen

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.tuanhn.smartmovie.R
import com.tuanhn.smartmovie.databinding.FragmentUserBinding

class UserFragment : Fragment() {

    private var binding: FragmentUserBinding? = null

    private val handler = Handler(Looper.getMainLooper())

    private var isButtonClicked = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserBinding.inflate(inflater, container, false)

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = context?.getSharedPreferences("current_user", Context.MODE_PRIVATE)

        val savedString = sharedPreferences?.getString("current_user", "default_value")

        binding?.tvAccount?.text = savedString

        binding?.layoutAccountInfor?.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_userFragment_to_accountInformationFragment)
        }

        binding?.layoutFavorite?.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_userFragment_to_favoriteScreen)
        }

        binding?.layoutPurchase?.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_userFragment_to_purchaseHistoryFragment)
        }

        binding?.layoutCoupons?.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_userFragment_to_couponsFragment)
        }

        binding?.layoutUserCoupons?.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_userFragment_to_userCouponsFragment)
        }

        setButtonMoveEvent(requireActivity())
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