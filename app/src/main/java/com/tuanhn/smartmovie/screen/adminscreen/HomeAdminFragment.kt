package com.tuanhn.smartmovie.screen.adminscreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.tuanhn.smartmovie.R
import com.tuanhn.smartmovie.databinding.FragmentHomeAdminBinding


class HomeAdminFragment : Fragment() {

    private lateinit var binding: FragmentHomeAdminBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeAdminBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setAccountEvent(view)

        setCouponEvent(view)

        setBillEvent(view)

        setMovieEvent(view)

        setRoomEvent(view)

        setSeatEvent(view)

        setAdjustMovieEvent(view)

        setStatisticEvent(view)
    }

    private fun setStatisticEvent(view: View){

        binding?.imgStatistical?.setOnClickListener {
            toStatisticFragment(view)
        }

        /* binding?.tvRoomAdmin?.setOnClickListener {
             toRoomFragment(view)
         }*/

    }

    private fun setAdjustMovieEvent(view: View){

        binding?.imgAdjustMovie?.setOnClickListener {
            toAdjustMovieFragment(view)
        }

       /* binding?.tvRoomAdmin?.setOnClickListener {
            toRoomFragment(view)
        }*/

    }

    private fun setRoomEvent(view: View){

        binding?.imgRoomAdmin?.setOnClickListener {
            toRoomFragment(view)
        }

        binding?.tvRoomAdmin?.setOnClickListener {
            toRoomFragment(view)
        }

    }

    private fun setSeatEvent(view: View){

        binding?.imgSeat?.setOnClickListener {
            toSeatFragment(view)
        }

        binding?.tvSeatAdmin?.setOnClickListener {
            toSeatFragment(view)
        }

    }

    private fun setCouponEvent(view: View){

        binding?.imgCoupon?.setOnClickListener {
            toCouponFragment(view)
        }

        binding?.tvCoupon?.setOnClickListener {
            toCouponFragment(view)
        }

    }

    private fun setAccountEvent(view: View){
        binding?.imgAccount?.setOnClickListener {
            toAccountFragment(view)
        }

        binding?.tvAccountAdmin?.setOnClickListener {
            toAccountFragment(view)
        }

    }

    private fun setMovieEvent(view: View){
        binding?.imgMovie?.setOnClickListener {
            toMovieFragment(view)
        }

        binding?.tvMovie?.setOnClickListener {
            toMovieFragment(view)
        }

    }

    private fun setBillEvent(view: View){
        binding?.imgBill?.setOnClickListener {
            toBillFragment(view)
        }

        binding?.tvBillAdmin?.setOnClickListener {
            toBillFragment(view)
        }

    }

    private fun toBillFragment(view: View){
        Navigation.findNavController(view).navigate(R.id.action_homeAdminFragment_to_billAdminFragment)
    }

    private fun toMovieFragment(view: View){
        Navigation.findNavController(view).navigate(R.id.action_homeAdminFragment_to_movieAdminFragment)
    }

    private fun toCouponFragment(view: View){
        Navigation.findNavController(view).navigate(R.id.action_homeAdminFragment_to_couponAdminFragment)
    }

    private fun toRoomFragment(view: View){
        Navigation.findNavController(view).navigate(R.id.action_homeAdminFragment_to_roomAdminFragment)
    }
    private fun toSeatFragment(view: View){
        Navigation.findNavController(view).navigate(R.id.action_homeAdminFragment_to_seatAdminFragment)
    }
    private fun toAccountFragment(view: View){
        Navigation.findNavController(view).navigate(R.id.action_homeAdminFragment_to_accountAdminFragment)
    }
    private fun toAdjustMovieFragment(view: View){
        Navigation.findNavController(view).navigate(R.id.action_homeAdminFragment_to_adjustingMovieAdminFragment)
    }
    private fun toStatisticFragment(view: View){
        Navigation.findNavController(view).navigate(R.id.action_homeAdminFragment_to_statisticalTable)
    }
}