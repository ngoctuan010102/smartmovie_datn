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
import com.tuanhn.smartmovie.screen.adminscreen.MovieAdminFragmentDirections
import com.tuanhn.smartmovie.screen.homescreen.HomeFragmentDirections

class MovieAdminAdapter(
    private var items: List<Film>
)

    : RecyclerView.Adapter<MovieAdminAdapter.ViewHolder>() {

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val imgView: ImageView = view.findViewById(R.id.item_vertical)
        private val tvName = view.findViewById<TextView>(R.id.tvNameMovieVertical)
        private val tvOverView = view.findViewById<TextView>(R.id.tvOverView)
        private val btnLayout = view.findViewById<LinearLayout>(R.id.verticalLayout)
        private val imgNotify = view.findViewById<ImageView>(R.id.imgNotify)
        private val imgFavorite = view.findViewById<ImageView>(R.id.imgFavVer)

        fun onBind(item: Film, position: Int) {

            Picasso.get()
                .load(item.poster)
                .into(imgView)

            tvName.text = item.film_name

            tvOverView.text = item.synopsis_long.slice(0..100)

            imgNotify.visibility = View.GONE

            imgFavorite.visibility = View.GONE

            btnLayout.setOnClickListener {
                val action = MovieAdminFragmentDirections.actionMovieAdminFragmentToMovieShowtimeAdminFragment(item)
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