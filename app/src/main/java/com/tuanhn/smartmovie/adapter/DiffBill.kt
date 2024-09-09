package com.tuanhn.smartmovie.adapter

import androidx.recyclerview.widget.DiffUtil
import com.tuanhn.smartmovie.data.model.entities.Bill
import com.tuanhn.smartmovie.data.model.entities.Film

class DiffBill(private val oldList: List<Bill>, private val newList: List<Bill>) :
    DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].bill_id == newList[newItemPosition].bill_id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
