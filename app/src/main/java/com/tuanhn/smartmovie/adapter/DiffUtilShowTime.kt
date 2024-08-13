package com.tuanhn.smartmovie.adapter


import androidx.recyclerview.widget.DiffUtil
import com.tuanhn.smartmovie.data.network.respond.ShowTime


class DiffUtilShowTime(private val oldList: List<ShowTime>, private val newList: List<ShowTime>) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].start_time == newList[newItemPosition].start_time
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}