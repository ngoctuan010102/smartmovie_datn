package com.tuanhn.smartmovie.screen.adminscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.tuanhn.smartmovie.adapter.RoomAdminAdapter
import com.tuanhn.smartmovie.data.model.entities.Coupon
import com.tuanhn.smartmovie.data.model.entities.Room
import com.tuanhn.smartmovie.databinding.FragmentRoomAdminBinding


class RoomAdminFragment : Fragment() {

    private lateinit var binding: FragmentRoomAdminBinding

    private var adapter: RoomAdminAdapter? = null

    private var listCurrentRoom: List<Room>? = null

    private var currentID: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRoomAdminBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialAdapter()

        observeData()

        setEvent()

        binding?.swipeRefreshLayout?.setOnRefreshListener {

            listCurrentRoom?.let { list ->
                observeData()
                adapter?.updateRoom(list)
            }
            binding?.swipeRefreshLayout?.isRefreshing = false
        }
    }

    private fun setEvent() {
        binding?.btnFind?.setOnClickListener {
            val keyWord = binding?.edtRoomID?.text.toString()
            findRoom(keyWord)
        }

        binding?.btnDelete?.setOnClickListener {
            val id = binding?.edtRoomID?.text.toString()
            deleteRoom(id.trim().toInt())
        }

        binding?.btnUpdate?.setOnClickListener {
            val id = binding?.edtRoomID?.text.toString()
            val coupon = getDataFromUI(id.toInt())
            coupon?.let {
                addRoom(coupon)
            }
        }

        binding?.btnAdd?.setOnClickListener {
            var id = currentID
            val newId = id!! + 1
            val coupon = getDataFromUI(newId)
            coupon?.let {
                addRoom(coupon)
            }
        }
    }

    private fun findRoom(keyWord: String) {
        try {

            val ID = keyWord.trim().toInt()

            val db = FirebaseFirestore.getInstance()

            val listRoom: MutableList<Room> = mutableListOf()

            db.collection("rooms").get().addOnSuccessListener { result ->
                for (document in result) {

                    val room = document.toObject<Room>()
                    if (room.room_id == ID) {
                        listRoom.add(room)
                        break
                    }
                }
                adapter?.updateRoom(listRoom)
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Please enter a valid ID", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteRoom(id: Int) {
        val db = FirebaseFirestore.getInstance()
        db.collection("rooms")
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

    private fun getDataFromUI(id: Int): Room? {
        val edtRoomName = binding?.edtRoomName?.text.toString()
        val edtRoomCapacity = binding?.edtRoomCapacity?.text.toString()


        return if (edtRoomName.isNotEmpty() && edtRoomCapacity.isNotEmpty()) {
            val room = Room(id, edtRoomName, edtRoomCapacity.toInt())
            room
        } else {
            Toast.makeText(requireContext(), "Please fill in the blank", Toast.LENGTH_SHORT).show()
            null
        }
    }

    private fun addRoom(room: Room) {
        val db = FirebaseFirestore.getInstance()
        db.collection("rooms").document(room.room_id.toString()).set(room).addOnSuccessListener {
            observeData()
        }
    }

    private fun initialAdapter() {

        adapter = RoomAdminAdapter(listOf(), this@RoomAdminFragment::displayRoom)

        binding?.rcvRoom?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding?.rcvRoom?.adapter = adapter

    }

    private fun displayRoom(room: Room) {
        binding?.edtRoomID?.setText(room.room_id.toString())
        binding?.edtRoomName?.setText(room.room_name)
        binding?.edtRoomCapacity?.setText(room.capacity.toString())
    }


    private fun observeData() {

        val db = FirebaseFirestore.getInstance()

        val listRoom: MutableList<Room> = mutableListOf()

        db.collection("rooms").get().addOnSuccessListener { result ->
            var count = 0

            for (document in result) {

                val room = document.toObject<Room>()

                if (room.room_id > count)
                    count = room.room_id

                listRoom.add(room)
            }

            listCurrentRoom = listRoom

            currentID = count

            adapter?.updateRoom(listRoom)
        }

    }
}