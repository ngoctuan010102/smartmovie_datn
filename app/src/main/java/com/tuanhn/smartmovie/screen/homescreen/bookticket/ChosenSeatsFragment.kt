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
import com.google.firebase.firestore.FirebaseFirestore
import com.tuanhn.smartmovie.adapter.SeatsAdapter
import com.tuanhn.smartmovie.data.model.entities.Seat
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

    private val listSeat: MutableList<Seat> = mutableListOf()

    private val listBookedSeat: MutableList<String> = mutableListOf()

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
                        listBookedSeat.toTypedArray(),
                        totalCount.toFloat(),
                        args.room.room_name,
                        args.time,
                        args.film
                    )

                Navigation.findNavController(view).navigate(action)
            }
        }

    }

    private fun setData() {
        val db = FirebaseFirestore.getInstance()

        db.collection("seats").get().addOnSuccessListener { result ->

            listSeat.clear()

            listSeats.clear()
            for (document in result) {
                val seat = document.toObject(Seat::class.java)

                if (seat.room_id == args.room.room_id) {

                    listSeat.add(seat)

                    listSeats.add(seat.seat_number)
                }
            }
        }
        /*     val list = listOf("A", "B", "C", "D", "E", "F", "G", "H")
             val listSeats: MutableList<String> = mutableListOf()
             for (j in 0..7) {
                 for (i in 11 downTo 1) {
                     listSeats.add("${list[j]}${i}")
                 }
             }
     */
        //  adapter?.updateSeats(listSeats, listOf())

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
            listBookedSeat.add(seat)
        else
            listBookedSeat.remove(seat)
    }
    /*
        private fun clearDataRealTime() {

            val database = FirebaseDatabase.getInstance()

            val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

            val myRef = database.getReference("bookedSeats").child(currentDate).child(args.cinemaName)

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
        }*/

    private fun getDataRealTime(listSeats: List<String>) {

        val db = FirebaseFirestore.getInstance()

        val currentDate = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())

        /*val myRef = database.getReference("bookedSeats").child(currentDate).child(args.cinemaName)
            .child(args.time)*/

        // clearDataRealTime()

        val listBookedSeats: MutableList<String> = mutableListOf()

        // Tham chiếu đến document chứa thông tin các ghế đã đặt
        val documentRef = db.collection("bookedSeats")
            .document(currentDate)
            .collection(args.room.room_name)
            .document(args.time)

        // Lấy dữ liệu từ document
        documentRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // Dữ liệu tồn tại trong document
                    val bookedSeats =
                        documentSnapshot.data // Hoặc chuyển đổi thành kiểu dữ liệu cụ thể nếu cần
                    if (bookedSeats != null) {
                        // Xử lý bookedSeats (Map<String, String> hoặc kiểu dữ liệu khác)
                        for ((seat, email) in bookedSeats) {
                            Log.d("Firestore", "Seat: $seat, Email: $email")
                            listBookedSeats.add(seat)
                        }
                    } else {
                        Log.d("Firestore", "Document is empty")
                    }
                } else {
                    Log.d("Firestore", "Document does not exist")
                }
                adapter?.updateSeats(listSeats, listBookedSeats)
            }
            .addOnFailureListener { e ->
                // Xử lý lỗi khi lấy dữ liệu
                Log.w("Firestore", "Error getting document.", e)
            }

        /*   myRef.addValueEventListener(object : ValueEventListener {
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
           })*/
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
            this@ChosenSeatsFragment::updateBookedSeats,
            listSeat
        )
        binding?.rcvSeats?.layoutManager = GridLayoutManager(context, 11)
        binding?.rcvSeats?.adapter = adapter
    }
}