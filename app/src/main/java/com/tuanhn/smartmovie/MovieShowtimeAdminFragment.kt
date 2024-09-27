package com.tuanhn.smartmovie

import android.R
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.tuanhn.smartmovie.adapter.ShowTimeAdapter
import com.tuanhn.smartmovie.data.model.entities.Room
import com.tuanhn.smartmovie.data.model.entities.Showtime
import com.tuanhn.smartmovie.databinding.FragmentMovieShowtimeAdminBinding
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class MovieShowtimeAdminFragment : Fragment() {

    private val args: MovieShowtimeAdminFragmentArgs by navArgs()

    private var listShowtime: MutableList<Showtime> = mutableListOf()

    private var currentSelectedSpinner = 0

    private lateinit var binding: FragmentMovieShowtimeAdminBinding

    private var adapter: ShowTimeAdapter? = null

    private val currentListRoom: MutableList<Room> = mutableListOf()

    private var currentSizeOfList: Int? = null

    private val data: MutableList<String> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMovieShowtimeAdminBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        intitialAdapter()

        setEvent()

        binding?.swipeRefreshLayout?.setOnRefreshListener {
            getShowTimeFromFireStore()
            binding?.swipeRefreshLayout?.isRefreshing = false
        }
    }

    private fun setEvent() {
        binding?.btnFind?.setOnClickListener {
            val keyWord = binding?.edtShowtimeID?.text.toString()
            findCoupon(keyWord)
        }

        binding?.btnDelete?.setOnClickListener {
            val id = binding?.edtShowtimeID?.text.toString()
            delete(id.trim().toInt())
        }

        binding?.btnUpdate?.setOnClickListener {
            val id = binding?.edtShowtimeID?.text.toString()
            val showtime = getDataFromUI(id.toInt())
            showtime?.let {
                addShowtimes(showtime)
                //updateCoupon(coupon)
            }
        }

        binding?.btnAdd?.setOnClickListener {
            var id = currentSizeOfList
            val newId = id!! + 1
            val showtime = getDataFromUI(newId)
            showtime?.let {
                addShowtimes(showtime)
            }
        }
    }

    private fun getDataFromUI(id: Int): Showtime? {
        val startDate = binding?.edtStartDate?.text.toString()
        val endDate = binding?.edtEndDate?.text.toString()
        val edtShowtimeDate = binding?.edtShowtimeDate?.text.toString()


        return if (startDate.isNotEmpty() && endDate.isNotEmpty() && edtShowtimeDate.isNotEmpty()) {
            val showtime = Showtime(
                id!!.toInt(),
                args.film.film_id,
                currentListRoom[currentSelectedSpinner].room_id,
                edtShowtimeDate,
                startDate,
                endDate
            )
            showtime
        } else {
            Toast.makeText(requireContext(), "Please fill in the blank", Toast.LENGTH_SHORT).show()
            null
        }
    }


    private fun addShowtimes(showtime: Showtime) {

        if (checkShowtime(showtime)) {
            val db = FirebaseFirestore.getInstance()
            db.collection("showtimes").document(showtime.showtime_id.toString()).set(showtime)
                .addOnSuccessListener {
                    getShowTimeFromFireStore()
                }
        }
        else
            Toast.makeText(requireContext(), "The room is busy", Toast.LENGTH_SHORT).show()
    }

    private fun checkShowtime(showtime: Showtime): Boolean {

        val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

        val startTime = LocalTime.parse(showtime.start_time, timeFormatter)

        val endTime = LocalTime.parse(showtime.end_time, timeFormatter)

        val sortedList = listShowtime.sortedBy { LocalTime.parse(it.start_time, timeFormatter) }

        val listBeforeStart = sortedList.filter { LocalTime.parse(it.end_time, timeFormatter).isBefore(startTime) }

        val listAfterEnd = sortedList.filter { LocalTime.parse(it.start_time, timeFormatter).isAfter(endTime) }

        val count = listBeforeStart.size + listAfterEnd.size

        return count == sortedList.size
    }

    private fun delete(id: Int) {
        val db = FirebaseFirestore.getInstance()
        db.collection("showtimes")
            .whereEqualTo("showtime_id", id)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    // Delete each document that matches the query
                    document.reference.delete()
                        .addOnSuccessListener {
                            // Document deleted successfully
                            getShowTimeFromFireStore()
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

    private fun findCoupon(keyWord: String) {
        try {

            val ID = keyWord.trim().toInt()

            val db = FirebaseFirestore.getInstance()

            val listShowtime: MutableList<Showtime> = mutableListOf()

            db.collection("showtimes").get().addOnSuccessListener { result ->
                for (document in result) {

                    val showtime = document.toObject<Showtime>()
                    if (showtime.showtime_id == ID) {
                        listShowtime.add(showtime)
                        break
                    }
                }
                adapter?.updateShowTime(listShowtime, currentListRoom[currentSelectedSpinner])
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Please enter a valid ID", Toast.LENGTH_SHORT).show()
        }
    }

    private fun displayShowtime(showtime: Showtime) {

        binding?.edtShowtimeID?.setText(showtime.showtime_id.toString())
        binding?.edtShowtimeDate?.setText(showtime.showtime_date)
        binding?.edtEndDate?.setText(showtime.end_time)
        binding?.edtStartDate?.setText(showtime.start_time)
        for (item in currentListRoom.indices) {
            if (currentListRoom[item].room_id == showtime.room_id)
                binding?.spinnerRoom?.setSelection(item)
        }

    }

    private fun intitialAdapter() {
        adapter = ShowTimeAdapter(
            listOf(),
            args.film,
            null,
            true,
            this@MovieShowtimeAdminFragment::displayShowtime
        )

        binding?.rcvShowTime?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding?.rcvShowTime?.adapter = adapter

        val db = FirebaseFirestore.getInstance()

        db.collection("rooms").get().addOnSuccessListener { result ->

            data.clear()

            currentListRoom.clear()
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
        binding?.spinnerRoom?.adapter = adapterSpinner

        binding?.spinnerRoom?.onItemSelectedListener =
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

                    getShowTimeFromFireStore()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Xử lý khi không có mục nào được chọn
                }
            }
    }

    private fun getShowTimeFromFireStore() {
        val db = FirebaseFirestore.getInstance()

        listShowtime.clear()
        if (currentListRoom.size > 0)
            binding?.roomName?.text = currentListRoom[currentSelectedSpinner].room_name

        db.collection("showtimes").get().addOnSuccessListener { result ->

            var count = 0
            for (document in result) {
                val showtime = document.toObject(Showtime::class.java)
                if (showtime.room_id == currentSelectedSpinner + 1) {
                    listShowtime.add(showtime)
                }
                count++
            }

            currentSizeOfList = count

            val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

            val sortedList = listShowtime.sortedBy { LocalTime.parse(it.start_time, timeFormatter) }

            val room = currentListRoom[currentSelectedSpinner]
            adapter?.updateShowTime(sortedList, room)
        }
    }
}