package com.tuanhn.smartmovie.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.tuanhn.smartmovie.R
import com.tuanhn.smartmovie.data.model.entities.Film
import com.tuanhn.smartmovie.screen.homescreen.HomeFragmentDirections

class MovieVerticalAdapter(
    private var items: List<Film>
)

    : RecyclerView.Adapter<MovieVerticalAdapter.ViewHolder>() {

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val imgView: ImageView = view.findViewById(R.id.item_vertical)
        private val favouriteVer = view.findViewById<ImageView>(R.id.imgFavVer)
        private val tvName = view.findViewById<TextView>(R.id.tvNameMovieVertical)
        private val tvOverView = view.findViewById<TextView>(R.id.tvOverView)
        private val btnLayout = view.findViewById<LinearLayout>(R.id.verticalLayout)

        fun onBind(item: Film, position: Int) {
            Picasso.get()
                .load(item.poster)
                .into(imgView)
            tvName.text = item.film_name
            tvOverView.text = item.synopsis_long.slice(0..100)
            btnLayout.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToDetailFilm(item)
                Navigation.findNavController(view).navigate(action)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout_vertical, parent, false)
        return ViewHolder(view)
    }

    fun updateMovies(newMovies: List<Film>) {
        val diffResult = DiffUtil.calculateDiff(DiffCallBack(items, newMovies))
        items = newMovies
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(items[position], position)
    }

    override fun getItemCount() = items.size
}
