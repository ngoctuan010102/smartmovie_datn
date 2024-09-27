package com.tuanhn.smartmovie.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tuanhn.smartmovie.R
import com.tuanhn.smartmovie.data.model.entities.Seat

class SeatAdminAdapter(
    private var listSeat: List<Seat>,
    private val displaySeat: (Seat) -> Unit
) : RecyclerView.Adapter<SeatAdminAdapter.ViewHolder>() {

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        private val tvSeatID: TextView = view.findViewById(R.id.tvSeatID)

        private val tvRoomID: TextView = view.findViewById(R.id.tvRoomID)

        private val tvSeatName: TextView = view.findViewById(R.id.tvSeatName)

        private val tvPrice: TextView = view.findViewById(R.id.tvPrice)

        private val layoutSeat: LinearLayout = view.findViewById(R.id.layoutSeat)

        fun onBind(seat: Seat) {

            tvSeatID.text = seat.seat_id.toString()
            tvRoomID.text = seat.room_id.toString()
            tvSeatName.text = seat.seat_number
            tvPrice.text = seat.price.toString()

            layoutSeat.setOnClickListener {
                displaySeat(seat)
            }


        }
    }

    fun updateSeat(newList: List<Seat>) {
        val diffResult = DiffUtil.calculateDiff(DiffSeatAdmin(listSeat, newList))
        listSeat = newList
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout_seat_admin, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: SeatAdminAdapter.ViewHolder, position: Int) {
        holder.onBind(listSeat[position])
    }


    override fun getItemCount() = listSeat.size

}