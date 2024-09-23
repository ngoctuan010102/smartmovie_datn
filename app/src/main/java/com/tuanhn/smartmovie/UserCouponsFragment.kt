package com.tuanhn.smartmovie

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.tuanhn.smartmovie.adapter.CouponAdapter
import com.tuanhn.smartmovie.data.model.entities.Coupon
import com.tuanhn.smartmovie.databinding.FragmentUserCouponsBinding

class UserCouponsFragment : Fragment() {

    private lateinit var binding: FragmentUserCouponsBinding

    private var adapter: CouponAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserCouponsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialAdapter()

        observeData()
    }

    private fun saveCoupon(id: String) {

    }

    private fun initialAdapter() {

        adapter = CouponAdapter(listOf(), true, false, this@UserCouponsFragment::saveCoupon, this@UserCouponsFragment::displayCoupon)

        binding?.rcvUserCoupons?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding?.rcvUserCoupons?.adapter = adapter
    }

    private fun displayCoupon(coupon: Coupon) {

    }

    private fun observeData() {
        val db = FirebaseFirestore.getInstance()

        val listCoupon: MutableList<Coupon> = mutableListOf()

        db.collection("coupons").get().addOnSuccessListener { result ->
            for (document in result) {

                val coupon = document.toObject<Coupon>()

                listCoupon.add(coupon)
            }
            getUserCoupons(db, listCoupon)
        }

    }

    private fun getUserCoupons(db: FirebaseFirestore, coupons: List<Coupon>) {

        val listCoupon: MutableList<Coupon> = mutableListOf()

        val sharedPreferences = context?.getSharedPreferences("current_user", Context.MODE_PRIVATE)

        val currentUser = sharedPreferences?.getString("current_user", "default_value")

        db.collection("userCoupons").get().addOnSuccessListener { result ->
            for (document in result) {
                if (document.getString("user") == currentUser) {
                    val id = document.getLong("couponId")

                    id?.let {
                        for (item in coupons.indices) {
                            if (coupons[item].id == id.toInt()) {
                                listCoupon.add(coupons[item])
                                break
                            }
                        }
                    }

                }
            }
            val listSet = listCoupon.toSet()
            adapter?.updateCoupon(listSet.toList())
        }
    }
}