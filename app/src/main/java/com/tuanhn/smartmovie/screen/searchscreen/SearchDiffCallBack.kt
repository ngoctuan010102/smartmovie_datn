package com.tuanhn.smartmovie.screen.searchscreen

import androidx.recyclerview.widget.DiffUtil
import com.tuanhn.smartmovie.data.model.entities.Film
import com.tuanhn.smartmovie.data.network.respond.SearchFilmRespond


class SearchDiffCallBack(private val oldList: List<SearchFilmRespond>, private val newList: List<SearchFilmRespond>) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].film_id == newList[newItemPosition].film_id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}