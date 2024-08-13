package com.tuanhn.smartmovie.adapter


import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tuanhn.smartmovie.R

class SeatsAdapter(
    private var items: List<String>,
    private var listBookedSeats: List<String>,
    private val updateTotal: (Double, Boolean) -> Unit,
    private val updateBookedSeats: (String, Boolean) -> Unit
) : RecyclerView.Adapter<SeatsAdapter.ViewHolder>() {

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val tvSeat = view.findViewById<TextView>(R.id.tvSeat)
        private val layoutSeat = view.findViewById<LinearLayout>(R.id.layoutSeat)
        fun onBind(item: String) {

            tvSeat.text = item

            if (listBookedSeats.contains(item))

                layoutSeat.setBackgroundResource(R.drawable.background_booked_seat)
            else {

                setSeatColor(item[0], layoutSeat, tvSeat)

                tvSeat.setOnClickListener {

                    if (tvSeat.currentTextColor == Color.BLACK) {

                        updateBookedSeats(item, false)

                        setSeatColor(item[0], layoutSeat, tvSeat)

                        setEvent(item[0], false)
                    } else {

                        updateBookedSeats(item, true)

                        layoutSeat.setBackgroundResource(R.drawable.background_chosen_seat)

                        tvSeat.setTextColor(Color.BLACK)

                        setEvent(item[0], true)
                    }
                }
            }

        }
    }

    private fun setEvent(item: Char, boolean: Boolean) {
        when (item) {
            'A', 'B', 'C' -> {
               updateTotal(45000.0, boolean)
            }

            else -> {
                updateTotal(65000.0, boolean)
            }
        }
    }

    private fun setSeatColor(item: Char, layoutSeat: LinearLayout, tvSeat: TextView) {
        when (item) {
            'A', 'B', 'C' -> {
                layoutSeat.setBackgroundResource(R.drawable.background_seat_normal)
                tvSeat.setTextColor(Color.WHITE)
            }

            else -> {
                layoutSeat.setBackgroundResource(R.drawable.background_seat_special)
                tvSeat.setTextColor(Color.WHITE)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout_seat, parent, false)
        return ViewHolder(view)
    }

    fun updateSeats(newSeats: List<String>, newListBookedSeats: List<String>) {
        val diffResult = DiffUtil.calculateDiff(DiffUtilSeats(items, newSeats))
        items = newSeats
        diffResult.dispatchUpdatesTo(this)

        val diffResultBooked = DiffUtil.calculateDiff(DiffUtilSeats(listBookedSeats, newListBookedSeats))
        listBookedSeats = newListBookedSeats
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(items[position])
    }

    /*override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {

        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {

            for (payload in payloads) {

                if (payload is Bundle) {

                    if (payload.containsKey("favorite")) {

                        val booked: Int? = payload.getInt("booked")

                        booked?.let {
                            holder.onBindBooked(booked)
                        }
                    }
                }
            }
        }
    }
*/
    override fun getItemCount() = items.size
}
