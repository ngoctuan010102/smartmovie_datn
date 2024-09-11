package com.tuanhn.smartmovie.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tuanhn.smartmovie.R
import com.tuanhn.smartmovie.data.model.entities.Bill

class BillAdapter(
    private var listBill: List<Bill>
) : RecyclerView.Adapter<BillAdapter.ViewHolder>() {

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        private val cinemaName: TextView = view.findViewById(R.id.cinemaName)

        private val seats: TextView = view.findViewById(R.id.tvEndDate)

        private val totalMoney: TextView = view.findViewById(R.id.tvdiscountValue)

        private val bookedDate: TextView = view.findViewById(R.id.bookedDate)

        fun onBind(bill: Bill) {
            cinemaName.text = bill.cinemaName

            seats.text = bill.seats

            totalMoney.text = bill.totalMoney.toString()

            bookedDate.text = bill.date
        }
    }

    fun updateBill(newList: List<Bill>) {
        val diffResult = DiffUtil.calculateDiff(DiffBill(listBill, newList))
        listBill = newList
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout_bill, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: BillAdapter.ViewHolder, position: Int) {
        holder.onBind(listBill[position])
    }


    override fun getItemCount() = listBill.size

}