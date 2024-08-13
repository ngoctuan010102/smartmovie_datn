package com.tuanhn.smartmovie.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tuanhn.smartmovie.R
import com.tuanhn.smartmovie.data.model.entities.Film
import com.tuanhn.smartmovie.data.network.respond.ShowTime
import com.tuanhn.smartmovie.screen.homescreen.bookticket.DetailFilmDirections

class ShowTimeAdapter(
    private var items: List<ShowTime>,
    private val film: Film,
    private var cinemaName: String
)

    : RecyclerView.Adapter<ShowTimeAdapter.ViewHolder>() {

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val tvName = view.findViewById<TextView>(R.id.tvSeat)
        fun onBind(item: ShowTime) {
            tvName.text =" ${item.start_time} - ${item.end_time}"

            tvName.setOnClickListener {

                val action = DetailFilmDirections.actionDetailFilmToChoosenSeatsFragment(film,item.start_time, cinemaName)

                Navigation.findNavController(view).navigate(action)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout_showtime, parent, false)
        return ViewHolder(view)
    }

    fun updateShowTime(newShowTime: List<ShowTime>, newCinemaName: String) {

        cinemaName = newCinemaName

        val diffResult = DiffUtil.calculateDiff(DiffUtilShowTime(items, newShowTime))
        items = newShowTime
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(items[position])
    }

    override fun getItemCount() = items.size
}
