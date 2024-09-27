package com.tuanhn.smartmovie.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tuanhn.smartmovie.R
import com.tuanhn.smartmovie.data.model.entities.Coupon

class CouponAdapter(
    private var listCoupon: List<Coupon>,
    private var isUserCoupons: Boolean,
    private var isAdmin: Boolean,
    private val saveCoupon: (Coupon) -> Unit,
    private val displayCoupon: (Coupon) -> Unit
) : RecyclerView.Adapter<CouponAdapter.ViewHolder>() {

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        private val tvIdCoupon: TextView = view.findViewById(R.id.tvIdCoupon)

        private val tvStartDate: TextView = view.findViewById(R.id.tvStartDate)

        private val tvEndDate: TextView = view.findViewById(R.id.tvEndDate)

        private val tvDiscountValue: TextView = view.findViewById(R.id.tvdiscountValue)

        private val tvSaveCoupon: TextView = view.findViewById(R.id.tvSaveCoupon)

        private val tvStatus: TextView = view.findViewById(R.id.tvStatusChange)

        private val tvLimitedCount: TextView = view.findViewById(R.id.tvlimitedCount)

        private val layoutCoupon: LinearLayout = view.findViewById(R.id.layoutCoupon)

        fun onBind(coupon: Coupon) {

            if (isUserCoupons)
                tvSaveCoupon.visibility = View.GONE
            else
            {
                tvSaveCoupon.setOnClickListener {
                    saveCoupon(coupon)
                }
            }

            tvStatus.text = coupon.status

            tvIdCoupon.text = coupon.id.toString()

            tvStartDate.text = coupon.startDate

            tvEndDate.text = coupon.endDate

            tvDiscountValue.text = coupon.discountValue.toString()

            tvLimitedCount.text = coupon.limitedCount.toString()

            if(isAdmin) {
                tvSaveCoupon.visibility = View.GONE
                layoutCoupon.setOnClickListener {
                    displayCoupon(coupon)
                }
            }

        }
    }

    fun updateCoupon(newList: List<Coupon>) {
        val diffResult = DiffUtil.calculateDiff(DiffCoupon(listCoupon, newList))
        listCoupon = newList
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout_coupon, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CouponAdapter.ViewHolder, position: Int) {
        holder.onBind(listCoupon[position])
    }


    override fun getItemCount() = listCoupon.size

}