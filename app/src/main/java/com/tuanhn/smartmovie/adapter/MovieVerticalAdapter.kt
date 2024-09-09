package com.tuanhn.smartmovie.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.tuanhn.smartmovie.R
import com.tuanhn.smartmovie.data.model.entities.Favorite
import com.tuanhn.smartmovie.data.model.entities.Film
import com.tuanhn.smartmovie.screen.homescreen.HomeFragmentDirections

class MovieVerticalAdapter(
    private var items: List<Film>,
    private val solveFavorite: (Film) -> Unit,
    private val pickDate: (Film) -> Unit,
    private val isFavoriteScreen: Boolean
)

    : RecyclerView.Adapter<MovieVerticalAdapter.ViewHolder>() {

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val imgView: ImageView = view.findViewById(R.id.item_vertical)
        private val favouriteVer = view.findViewById<ImageView>(R.id.imgFavVer)
        private val tvName = view.findViewById<TextView>(R.id.tvNameMovieVertical)
        private val tvOverView = view.findViewById<TextView>(R.id.tvOverView)
        private val btnLayout = view.findViewById<LinearLayout>(R.id.verticalLayout)
        private val imgNotify = view.findViewById<ImageView>(R.id.imgNotify)

        fun onBindFavorite(fav: Int) {
            when (fav) {
                1 -> {
                    favouriteVer.setImageResource(R.drawable.baseline_star_24)
                }

                0 -> {
                    favouriteVer.setImageResource(R.drawable.baseline_star_border_24)
                }
            }
        }

        fun onBind(item: Film, position: Int) {

            imgNotify.setOnClickListener {
                pickDate(item)
            }

            Picasso.get()
                .load(item.poster)
                .into(imgView)

            if (isFavoriteScreen) {

                favouriteVer.setImageResource(R.drawable.baseline_star_24)

                imgNotify.visibility = View.GONE
            } else
                favouriteVer.setImageResource(R.drawable.baseline_star_border_24)

            tvName.text = item.film_name

            tvOverView.text = item.synopsis_long.slice(0..100)

            btnLayout.setOnClickListener {

                val action = HomeFragmentDirections.actionHomeFragmentToDetailFilm(item)

                Navigation.findNavController(view).navigate(action)
            }

            favouriteVer.setOnClickListener {
                solveFavorite(item)
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            for (payload in payloads) {
                if (payload is Bundle) {
                    if (payload.containsKey("favorite")) {
                        val favorite: Int? = payload.getInt("favorite")
                        favorite?.let {
                            holder.onBindFavorite(favorite)
                        }
                    }
                }
            }
        }
    }

    fun updateFavorite(list: List<Favorite>) {
        for (item in items.indices) {
            val bundle = Bundle()
            val checkList =
                list.any { favorite -> favorite.film_id.toString() == items[item].film_id.toString() }
            if (checkList) {
                bundle.putInt("favorite", 1)
                notifyItemChanged(item, bundle)
            } else {
                bundle.putInt("favorite", 0)
                notifyItemChanged(item, bundle)
            }
        }
    }

    override fun getItemCount() = items.size
}
