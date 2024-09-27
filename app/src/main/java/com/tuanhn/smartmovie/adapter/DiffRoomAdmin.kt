package com.tuanhn.smartmovie.adapter

import androidx.recyclerview.widget.DiffUtil
import com.tuanhn.smartmovie.data.model.entities.Room

class DiffRoomAdmin (private val oldList: List<Room>, private val newList: List<Room>) :
    DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].room_id == newList[newItemPosition].room_id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
