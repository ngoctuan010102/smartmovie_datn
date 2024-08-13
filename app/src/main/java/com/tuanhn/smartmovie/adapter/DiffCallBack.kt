package com.tuanhn.smartmovie.adapter


import androidx.recyclerview.widget.DiffUtil
import com.tuanhn.smartmovie.data.model.entities.Film


class DiffCallBack(private val oldList: List<Film>, private val newList: List<Film>) : DiffUtil.Callback() {

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