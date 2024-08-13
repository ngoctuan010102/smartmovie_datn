package com.tuanhn.smartmovie.screen.searchscreen
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import com.tuanhn.smartmovie.R
import com.tuanhn.smartmovie.data.network.respond.Images
import com.tuanhn.smartmovie.data.network.respond.SearchFilmRespond

class SearchAdapter(
    private var items: List<SearchFilmRespond>
) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private val tvNameMovie: TextView = view.findViewById(R.id.tvNameMovie)
        private val tvTypes: TextView = view.findViewById(R.id.tvTypes)
        private val rateSearch: RatingBar = view.findViewById(R.id.rateSearch)
        private val imgView: ImageView = view.findViewById(R.id.imgSearch)
        private val btnLayout = view.findViewById<LinearLayout>(R.id.layoutSearch)
        fun onBind(film: SearchFilmRespond) {

            val imagesJson = Gson().toJson(film.images)

            val images = Gson().fromJson(imagesJson, Images::class.java)

            val poster: String? =
                images.poster?.values?.firstOrNull()?.medium?.film_image

            Picasso.get()
                .load(poster)
                .into(imgView)

            tvNameMovie.text = film.film_name

            val textType = StringBuilder()
            /*for (item in movie.genre.indices) {
                if (item == 0) {
                    textType.append(movie.genre[item].name)
                } else {
                    textType.append(" | ${movie.genre[item].name}")
                }
            }*/
            tvTypes.text = textType
            /*rateSearch.rating = film.duration.toString()*/
            btnLayout.setOnClickListener {
                /*val action = SearchFragmentDirections.actionSearchFragmentToDetailFilm(film)
                Navigation.findNavController(view)
                    .navigate(action)*/
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout_search, parent, false)
        return ViewHolder(view)
    }

    fun updateMovies(newMovies: List<SearchFilmRespond>) {
        val diffResult = DiffUtil.calculateDiff(SearchDiffCallBack(items, newMovies))
        items = newMovies
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(items[position])
    }

    override fun getItemCount() = items.size
}
