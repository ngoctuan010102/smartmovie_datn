package com.tuanhn.smartmovie.adapter

import androidx.recyclerview.widget.DiffUtil
import com.tuanhn.smartmovie.data.model.entities.Bill
import com.tuanhn.smartmovie.data.model.entities.Coupon
import com.tuanhn.smartmovie.data.model.entities.Film

class DiffCoupon(private val oldList: List<Coupon>, private val newList: List<Coupon>) :
    DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
