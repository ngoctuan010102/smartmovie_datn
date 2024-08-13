package com.tuanhn.smartmovie.screen.homescreen.bookticket

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tuanhn.smartmovie.adapter.SeatsAdapter
import com.tuanhn.smartmovie.databinding.FragmentChoosenSeatsBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class ChosenSeatsFragment : Fragment() {

    private val args: ChosenSeatsFragmentArgs by navArgs()

    private var binding: FragmentChoosenSeatsBinding? = null

    private var adapter: SeatsAdapter? = null

    private var listSeats: MutableList<String> = mutableListOf()

    private var totalCount: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChoosenSeatsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialAdapter(view)

        setData()

        binding?.btnConfirm?.setOnClickListener {
            if (!listSeats.isNullOrEmpty()) {

                // setDataRealTime()

                val action =
                    ChosenSeatsFragmentDirections.actionChoosenSeatsFragmentToBookSeatsInformation(
                        listSeats.toTypedArray(),
                        totalCount.toFloat(),
                        args.cinemaName,
                        args.time,
                        args.film
                    )

                Navigation.findNavController(view).navigate(action)
            }
        }

    }

    private fun setData() {
        val list = listOf("A", "B", "C", "D", "E", "F", "G", "H")
        val listSeats: MutableList<String> = mutableListOf()
        for (j in 0..7) {
            for (i in 11 downTo 1) {
                listSeats.add("${list[j]}${i}")
            }
        }
        getDataRealTime(listSeats)

        Log.d("sdsd", "update")
    }


    private fun updateTotalMoney(double: Double, isAdd: Boolean) {
        val total = binding?.tvTotal?.text.toString()
        if (isAdd) {
            if (total.isNullOrEmpty()) {

                totalCount = double

                binding?.tvTotal?.text = "$double"
            } else {
                totalCount = total.toDouble() + double

                binding?.tvTotal?.text = "$totalCount"
            }
        } else {
            if (!total.isNullOrEmpty()) {
                totalCount = total.toDouble() - double
                binding?.tvTotal?.text = "$totalCount"
            }
        }
    }


    private fun updateBookedSeats(seat: String, isAdd: Boolean) {
        if (isAdd)
            listSeats.add(seat)
        else
            listSeats.remove(seat)
    }

    private fun clearDataRealTime() {

        val database = FirebaseDatabase.getInstance()

        val myRef = database.getReference("bookedSeats").child(args.cinemaName)

        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for (childSnapshot in snapshot.children) {

                    val timeSlot = childSnapshot.key ?: continue

                    if (timeSlot < currentTime) {
                        childSnapshot.ref.removeValue()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun getDataRealTime(listSeats: List<String>) {

        val database = FirebaseDatabase.getInstance()

        val myRef = database.getReference("bookedSeats").child(args.cinemaName)
            .child(args.time)

        clearDataRealTime()
        val listBookedSeats: MutableList<String> = mutableListOf()
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for (childSnapshot in snapshot.children) {

                    childSnapshot.key?.let { key ->
                        listBookedSeats.add(key)
                    }
                }
                adapter?.updateSeats(listSeats, listBookedSeats)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Xử lý lỗi
                println("Error reading data: ${databaseError.message}")
            }
        })
        //adapter?.updateSeats(listSeats, listBookedSeats)
    }

    private fun initialAdapter(view: View) {

        binding?.btnConfirm?.setOnClickListener {
            Navigation.findNavController(view).popBackStack()
        }

        adapter = SeatsAdapter(
            listOf(),
            listOf(),
            this@ChosenSeatsFragment::updateTotalMoney,
            this@ChosenSeatsFragment::updateBookedSeats
        )
        binding?.rcvSeats?.layoutManager = GridLayoutManager(context, 11)
        binding?.rcvSeats?.adapter = adapter
    }
}