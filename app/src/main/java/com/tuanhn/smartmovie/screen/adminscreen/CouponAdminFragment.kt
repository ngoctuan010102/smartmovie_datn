package com.tuanhn.smartmovie.screen.adminscreen

import android.R
import android.os.Bundle
import android.util.Log
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
import com.tuanhn.smartmovie.adapter.CouponAdapter
import com.tuanhn.smartmovie.data.model.entities.Coupon
import com.tuanhn.smartmovie.databinding.FragmentCouponAdminBinding

class CouponAdminFragment : Fragment() {

    private lateinit var binding: FragmentCouponAdminBinding

    private var adapter: CouponAdapter? = null

    private var listCurrentCoupon: List<Coupon>? = null

    private var currentSizeOfList: Int? = null

    private var status: String = "Expired"

    private val data = listOf("Active", "Expired")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCouponAdminBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialAdapter()

        observeData()

        setEvent()

        binding?.swipeRefreshLayout?.setOnRefreshListener {

            listCurrentCoupon?.let { list ->
                observeData()
                adapter?.updateCoupon(list)
            }
            binding?.swipeRefreshLayout?.isRefreshing = false
        }
    }

    private fun setEvent() {
        binding?.btnFind?.setOnClickListener {
            val keyWord = binding?.edtID?.text.toString()
            findCoupon(keyWord)
        }

        binding?.btnDelete?.setOnClickListener {
            val id = binding?.edtID?.text.toString()
            delete(id.trim().toInt())
        }

        binding?.btnUpdate?.setOnClickListener {
            val id = binding?.edtID?.text.toString()
            val coupon = getDataFromUI(id.toInt())
            coupon?.let {
                addCoupon(coupon)
                //updateCoupon(coupon)
            }
        }

        binding?.btnAdd?.setOnClickListener {
            var id = currentSizeOfList
            val newId = id!! + 1
            val coupon = getDataFromUI(newId)
            coupon?.let {
                addCoupon(coupon)
            }
        }
    }

    private fun getDataFromUI(id: Int): Coupon? {
        val startDate = binding?.edtStartDate?.text.toString()
        val endDate = binding?.edtEndDate?.text.toString()
        val discountValue = binding?.edtDiscountValue?.text.toString()
        val limitedCount = binding?.edtLimitedCount?.text.toString()

        return if (startDate.isNotEmpty() && endDate.isNotEmpty() && discountValue.isNotEmpty() && limitedCount.isNotEmpty()) {
            val coupon = Coupon(
                id!!.toInt(),
                discountValue.trim().toInt(),
                startDate,
                endDate,
                status,
                limitedCount.trim().toInt()
            )
            coupon
        } else {
            Toast.makeText(requireContext(), "Please fill in the blank", Toast.LENGTH_SHORT).show()
            null
        }
    }

    private fun displayCoupon(coupon: Coupon) {
        binding?.edtID?.setText(coupon.id.toString())
        binding?.edtDiscountValue?.setText(coupon.discountValue.toString())
        binding?.edtEndDate?.setText(coupon.endDate)
        binding?.edtStartDate?.setText(coupon.startDate)
        binding?.edtLimitedCount?.setText(coupon.limitedCount.toString())
        for (item in data.indices){
            if(coupon.status == data[item])
                binding?.spinnerStatus?.setSelection(item)
        }
    }

    private fun addCoupon(coupon: Coupon) {
        val db = FirebaseFirestore.getInstance()
        db.collection("coupons").document(coupon.id.toString()).set(coupon).addOnSuccessListener {
            observeData()
        }
    }

    private fun delete(id: Int) {
        val db = FirebaseFirestore.getInstance()
        db.collection("coupons")
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

    private fun initialAdapter() {

        adapter = CouponAdapter(listOf(), false, true, this@CouponAdminFragment::saveCoupon,this@CouponAdminFragment::displayCoupon)

        binding?.rcvCoupon?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding?.rcvCoupon?.adapter = adapter


        val adapter = ArrayAdapter(requireActivity(), R.layout.simple_spinner_item, data)

// Đặt kiểu hiển thị khi chọn 1 item (dropdown)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

// Gán adapter cho Spinner
        binding?.spinnerStatus?.adapter = adapter

        binding?.spinnerStatus?.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    // Lấy giá trị đã chọn từ Spinner
                    val selectedItem = parent.getItemAtPosition(position).toString()
                    status = selectedItem
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Xử lý khi không có mục nào được chọn
                }
            }

    }

    private fun findCoupon(keyWord: String) {
        try {

            val ID = keyWord.trim().toInt()

            val db = FirebaseFirestore.getInstance()

            val listCoupon: MutableList<Coupon> = mutableListOf()

            db.collection("coupons").get().addOnSuccessListener { result ->
                for (document in result) {

                    val coupon = document.toObject<Coupon>()
                    if (coupon.id == ID) {
                        listCoupon.add(coupon)
                        break
                    }
                }
                adapter?.updateCoupon(listCoupon)
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Please enter a valid ID", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeData() {
        val db = FirebaseFirestore.getInstance()

        val listCoupon: MutableList<Coupon> = mutableListOf()

        db.collection("coupons").get().addOnSuccessListener { result ->
            var count = 0

            for (document in result) {

                val coupon = document.toObject<Coupon>()

                if (coupon.id > count)
                    count = coupon.id

                listCoupon.add(coupon)
            }

            listCurrentCoupon = listCoupon

            currentSizeOfList = count

            adapter?.updateCoupon(listCoupon)
        }

    }

    private fun saveCoupon(coupon: Coupon) {

    }
}