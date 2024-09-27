package com.tuanhn.smartmovie.adapter

import androidx.recyclerview.widget.DiffUtil
import com.tuanhn.smartmovie.data.model.entities.Seat

class DiffSeatAdmin(private val oldList: List<Seat>, private val newList: List<Seat>) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].seat_id == newList[newItemPosition].seat_id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}