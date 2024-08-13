package com.tuanhn.smartmovie.adapter


import androidx.recyclerview.widget.DiffUtil
import com.tuanhn.smartmovie.data.network.respond.ShowTime


class DiffUtilSeats(private val oldList: List<String>, private val newList: List<String>) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}