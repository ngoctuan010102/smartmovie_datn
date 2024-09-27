package com.tuanhn.smartmovie.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.tuanhn.smartmovie.R
import com.tuanhn.smartmovie.data.model.entities.Film

class TopSellerAdapter(
    private var items: List<Film>,
    private var listCountBuy: List<Int>
) : RecyclerView.Adapter<TopSellerAdapter.ViewHolder>() {

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        private val imgView: ImageView = view.findViewById(R.id.imgUserImage)

        private val tvCountBuy: TextView = view.findViewById(R.id.tvCountBuy)


        fun onBind(item: Film, position: Int) {
                Picasso.get()
                    .load(item.poster)
                    .into(imgView)

                tvCountBuy.text = listCountBuy[position].toString()
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout_top_seller, parent, false)
        return ViewHolder(view)
    }

    fun updateMovies(newMovies: List<Film>, listCount: List<Int>) {
        val diffResult = DiffUtil.calculateDiff(DiffCallBack(items, newMovies))

        items = newMovies

        listCountBuy = listCount

        diffResult.dispatchUpdatesTo(this)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(items[position], position)
    }

    override fun getItemCount() = items.size
}
