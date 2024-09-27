package com.tuanhn.smartmovie

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.tuanhn.smartmovie.adapter.CouponAdapter
import com.tuanhn.smartmovie.data.model.entities.Coupon
import com.tuanhn.smartmovie.databinding.FragmentCouponsBinding


class CouponsFragment : Fragment() {

    private lateinit var binding: FragmentCouponsBinding

    private var adapter: CouponAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCouponsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialAdapter()

        observeData()

    }

    private fun saveCoupon(coupon: Coupon) {
        val sharedPreferences = context?.getSharedPreferences("current_user", Context.MODE_PRIVATE)

        val currentUser = sharedPreferences?.getString("current_user", "default_value")

        val db = FirebaseFirestore.getInstance()

        db.collection("userCoupons").get().addOnSuccessListener { result ->
            if (result.isEmpty) {
                currentUser?.let {
                    addItem(db, currentUser, coupon.id)

                    coupon.apply {
                        coupon.limitedCount = coupon.limitedCount - 1
                    }

                    updateCoupon(coupon)
                }
            } else {
                var check = 0
                for (document in result) {

                    // val coupon = document.toObject(Coupon::class.java)
                    val x = document.getString("user")
                    val y = document.getLong("couponId")?.toInt()

                    if (x == currentUser) {
                        if (y == coupon.id) {
                            check++
                            break
                        }
                    }
                }
                if (check == 0) {
                    currentUser?.let {

                        addItem(db, currentUser, coupon.id)

                    }

                    coupon.apply {
                        coupon.limitedCount = coupon.limitedCount - 1
                    }

                    updateCoupon(coupon)

                    Toast.makeText(
                        requireContext(),
                        "Add coupon succeed",
                        Toast.LENGTH_LONG
                    )
                        .show()
                } else
                    Toast.makeText(
                        requireContext(),
                        "Add coupon failed",
                        Toast.LENGTH_LONG
                    )
                        .show()
            }
        }


    }

    private fun updateCoupon(coupon: Coupon) {

        val db = FirebaseFirestore.getInstance()

        val ref = db.collection("coupons").document(coupon.id.toString())
        ref.set(coupon)

        observeData()
    }

    private fun addItem(db: FirebaseFirestore, currentUser: String, couponId: Int) {

        val field = mapOf(
            "user" to currentUser,
            "couponId" to couponId,
            "isValid" to true
        )

        db.collection("userCoupons").add(field)
    }

    private fun initialAdapter() {

        adapter = CouponAdapter(
            listOf(),
            false,
            false,
            this@CouponsFragment::saveCoupon,
            this@CouponsFragment::displayCoupon
        )

        binding?.rcvCoupon?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding?.rcvCoupon?.adapter = adapter
    }


    private fun displayCoupon(coupon: Coupon) {

    }

    private fun observeData() {
        val db = FirebaseFirestore.getInstance()

        val listCoupon: MutableList<Coupon> = mutableListOf()

        val ref = db.collection("coupons").get().addOnSuccessListener { result ->
            for (document in result) {
                val bill = document.toObject(Coupon::class.java)
                listCoupon.add(bill)
            }
            adapter?.updateCoupon(listCoupon)
        }
    }

}