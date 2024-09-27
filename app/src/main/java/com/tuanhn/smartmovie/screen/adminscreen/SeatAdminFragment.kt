package com.tuanhn.smartmovie.screen.adminscreen

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.tuanhn.smartmovie.adapter.SeatAdminAdapter
import com.tuanhn.smartmovie.data.model.entities.Room
import com.tuanhn.smartmovie.data.model.entities.Seat
import com.tuanhn.smartmovie.databinding.FragmentSeatAdminBinding


class SeatAdminFragment : Fragment() {

    private lateinit var binding: FragmentSeatAdminBinding

    private var adapter: SeatAdminAdapter? = null

    private var listCurrentSeat: List<Seat>? = null

    private var currentID: Int? = null

    private var currentSelectedSpinner = 0

    private val currentListRoom: MutableList<Room> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSeatAdminBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialAdapter()

        observeData()

        setEvent()

        binding?.swipeRefreshLayout?.setOnRefreshListener {

            listCurrentSeat?.let { list ->
                observeData()
                adapter?.updateSeat(list)
            }
            binding?.swipeRefreshLayout?.isRefreshing = false
        }
    }

    private fun setEvent() {
        binding?.btnFind?.setOnClickListener {
            val keyWord = binding?.edtSeatID?.text.toString()
            findRoom(keyWord)
        }

        binding?.btnDelete?.setOnClickListener {
            val id = binding?.edtSeatID?.text.toString()
            deleteRoom(id.trim().toInt())
        }

        binding?.btnUpdate?.setOnClickListener {
            val id = binding?.edtSeatID?.text.toString()
            val seat = getDataFromUI(id.toInt())
            seat?.let {
                addRoom(seat)
            }
        }

        binding?.btnAdd?.setOnClickListener {
            var id = currentID
            val newId = id!! + 1
            val seat = getDataFromUI(newId)
            seat?.let {
                addRoom(seat)
            }
        }
    }

    private fun findRoom(keyWord: String) {
        try {

            val ID = keyWord.trim().toInt()

            val db = FirebaseFirestore.getInstance()

            val listSeat: MutableList<Seat> = mutableListOf()

            db.collection("seats").get().addOnSuccessListener { result ->
                for (document in result) {

                    val seat = document.toObject<Seat>()
                    if (seat.seat_id == ID) {
                        listSeat.add(seat)
                        break
                    }
                }
                adapter?.updateSeat(listSeat)
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Please enter a valid ID", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteRoom(id: Int) {
        val db = FirebaseFirestore.getInstance()
        db.collection("seats")
            .whereEqualTo("id", id)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    // Delete each document that matches the query
                    document.reference.delete()
                        .addOnSuccessListener {
                            // Document deleted successfully
                            observeData()
                        }
                        .addOnFailureListener { e ->
                            // Error deleting the document
                        }
                }
            }
            .addOnFailureListener { e ->
                // Handle error when retrieving documents
                //     Toast.makeText(this, "Error retrieving documents: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getDataFromUI(id: Int): Seat? {

        val edtPrice = binding?.edtPrice?.text.toString()
        val edtSeatNumber = binding?.edtSeatNumber?.text.toString()

        var edtRoomID = 0
        if (currentSelectedSpinner > 0)
            edtRoomID = currentListRoom[currentSelectedSpinner - 1].room_id


        return if (edtPrice.isNotEmpty() && edtSeatNumber.isNotEmpty()) {
            val seat = Seat(id, edtRoomID, edtSeatNumber, edtPrice.toFloat())
            seat
        } else {
            Toast.makeText(requireContext(), "Please fill in the blank", Toast.LENGTH_SHORT).show()
            null
        }
    }

    private fun addRoom(seat: Seat) {
        val db = FirebaseFirestore.getInstance()
        db.collection("seats").document(seat.seat_id.toString()).set(seat).addOnSuccessListener {
            /*observeData()

            if(currentSelectedSpinner > 0) {
                val list =
                    listCurrentSeat?.filter { it.room_id == currentListRoom[currentSelectedSpinner - 1].room_id }

                list?.let {
                    adapter?.updateSeat(list)
                }
            }*/
            observeAfterAddData()
        }
    }

    private fun initialAdapter() {

        adapter = SeatAdminAdapter(listOf(), this@SeatAdminFragment::displaySeat)

        binding?.rcvRoom?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding?.rcvRoom?.adapter = adapter

        val db = FirebaseFirestore.getInstance()

        val data: MutableList<String> = mutableListOf()

        data.add("All")

        db.collection("rooms").get().addOnSuccessListener { result ->
            for (document in result) {

                val room = document.toObject<Room>(Room::class.java)

                data.add(room.room_name)

                currentListRoom.add(room)
            }
            setAdapterSpinner(data)
        }
    }

    private fun setAdapterSpinner(data: List<String>) {
        val adapterSpinner = ArrayAdapter(requireActivity(), R.layout.simple_spinner_item, data)

// Đặt kiểu hiển thị khi chọn 1 item (dropdown)
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

// Gán adapter cho Spinner
        binding?.spinnerSeatAdmin?.adapter = adapterSpinner

        binding?.spinnerSeatAdmin?.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    // Lấy giá trị đã chọn từ Spinner
                    val selectedItem = parent.getItemAtPosition(position).toString()
                    //status = selectedItem

                    currentSelectedSpinner = position

                    if (position == 0) {
                        observeData()
                    } else {
                        val list =
                            listCurrentSeat?.filter { it.room_id == currentListRoom[position - 1].room_id }

                        list?.let {
                            adapter?.updateSeat(list)
                        }

                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Xử lý khi không có mục nào được chọn
                }
            }
    }

    private fun displaySeat(seat: Seat) {
        binding?.edtSeatID?.setText(seat.seat_id.toString())
        binding?.edtSeatNumber?.setText(seat.seat_number)
        binding?.edtPrice?.setText(seat.price.toString())

    }

    private fun observeAfterAddData() {

        val db = FirebaseFirestore.getInstance()

        val listSeat: MutableList<Seat> = mutableListOf()

        val listSeat2: MutableList<Seat> = mutableListOf()

        db.collection("seats").get().addOnSuccessListener { result ->
            var count = 0

            for (document in result) {

                val seat = document.toObject<Seat>()

                if (seat.seat_id > count)
                    count = seat.seat_id

                if (seat.room_id == currentListRoom[currentSelectedSpinner - 1].room_id)
                    listSeat2.add(seat)
                listSeat.add(seat)
            }

            listCurrentSeat = listSeat

            currentID = count

            adapter?.updateSeat(listSeat2)
        }
    }

    private fun observeData() {

        val db = FirebaseFirestore.getInstance()

        val listSeat: MutableList<Seat> = mutableListOf()

        db.collection("seats").get().addOnSuccessListener { result ->
            var count = 0

            for (document in result) {

                val seat = document.toObject<Seat>()

                if (seat.seat_id > count)
                    count = seat.seat_id

                listSeat.add(seat)
            }

            listCurrentSeat = listSeat

            currentID = count

            adapter?.updateSeat(listSeat)
        }

    }
}